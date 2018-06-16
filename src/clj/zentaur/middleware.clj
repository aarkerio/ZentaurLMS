(ns zentaur.middleware
  (:require  [zentaur.config :refer [env]]
             [zentaur.env :refer [defaults]]
             [zentaur.hiccup_templating.layout-view :as layout]
             [zentaur.hiccup_templating.helpers-view :as helper-view]
             [zentaur.layout :refer [*app-context* error-page]]
             [clojure.tools.logging :as log]
             [cognitect.transit :as transit]
             [hiccup.middleware :only (wrap-base-url)]
             [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
             [ring.middleware.flash :refer [wrap-flash]]
             [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
             [ring.middleware.format :refer [wrap-restful-format]]
             [ring.util.response :refer [response]]
             [buddy.auth :refer [authenticated?]]
             [buddy.auth.accessrules :refer [restrict wrap-access-rules]]
             [buddy.auth.backends.session :refer [session-backend]]
             [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]])
  (:import [javax.servlet ServletContext]
           [org.joda.time ReadableInstant]))

(def ^:const available-roles ["admin" "user" "none"])

(defn admin-access [req]
  (let [identity (:identity req)]
    (= true (:admin identity))))

(defn loggedin-access [req]
  (some? (-> req :session :identity)))

;; Open Access
(defn unauthorized-access [_] true)

(defn on-error [request response]
  {:status  403
   :headers {"Content-Type" "text/plain"}
   :body    (str "Access to " (:uri request) " is not authorized")})

(defn wrap-restricted [handler]
  (restrict handler {:handler unauthorized-access
                     :on-error on-error}))

(def rules [{:uri "/about"
             :handler unauthorized-access}
            {:pattern #"^/admin.*"
             :handler admin-access
             :redirect "/notauthorized"}
            {:pattern #"^/user/changepassword"
             :handler loggedin-access}
            {:uris ["/post/savecomment" "/post/listing"]
             :handler loggedin-access}
            {:pattern #"^/user.*"
             :handler admin-access}
            {:pattern #"^/"
             :handler unauthorized-access}
            ])

(defn unauthorized-handler
  [request _]
  (let [current-url (:uri request)]
    (response (format "/login?nexturl=%s" current-url))))

(def auth-backend
  (session-backend))

;; build the context
(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a servlet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                ;; if the context is not specified in the request
                ;; we check if one has been specified in the environment
                ;; instead
                (:app-context env))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error (str "####### wrap-internal-error ########### >>>>>" t))
        (error-page {:status 500
                     :title "Etwas sehr Schlechtes ist passiert!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
      (layout/application {:title "Invalid anti-forgery token"
                           :contents (helper-view/http-status {:status 403
                                                               :title "Invalid anti-forgery token"
                                                               :message "Invalid anti-forgery token"})})}))
(def joda-time-writer
  (transit/write-handler
    (constantly "m")
    (fn [v] (-> ^ReadableInstant v .getMillis))
    (fn [v] (-> ^ReadableInstant v .getMillis .toString))))

(def all-the-sessions (atom {}))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-access-rules {:rules rules :on-error on-error})
      (wrap-authentication auth-backend)
      (wrap-authorization auth-backend)
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)))
      (wrap-flash)
      (wrap-restful-format handler [:json :transit-json])
      (wrap-internal-error)))
