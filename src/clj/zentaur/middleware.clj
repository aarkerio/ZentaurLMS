(ns zentaur.middleware
  (:require [zentaur.env :refer [defaults]]
            [cheshire.generate :as cheshire]
            [cognitect.transit :as transit]
            [clojure.tools.logging :as log]
            [zentaur.layout :refer [error-page]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [zentaur.middleware.formats :as formats]
            [muuntaja.middleware :refer [wrap-format wrap-params]]
            [zentaur.config :refer [env]]
            [ring.middleware.flash :refer [wrap-flash]]
            [immutant.web.middleware :refer [wrap-session]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict wrap-access-rules]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.session :refer [session-backend]])
  (:import
           [org.joda.time ReadableInstant]))

;; ******  Using wrap-access-rules middleware STARTS.
(defn admin-access [request]
  (let [identity (:identity request)]
    (= true (:admin identity))))

(defn loggedin-access [request]
  (some? (-> request :session :identity)))

(defn on-error [request response]
  {:status  403
   :headers {"Content-Type" "text/plain"}
   :body    (str "Access to " (:uri request) " is not authorized")})

(def rules [{:pattern #"^/admin.*"
             :handler admin-access
             :redirect "/notauthorized"}
            {:pattern #"^/user/changepassword"
             :handler loggedin-access}
            {:uris ["/post/savecomment" "/post/listing"]
             :handler loggedin-access}
            {:pattern #"^/user.*"
             :handler loggedin-access}
            ])
;; ******  Using wrap-access-rules middleware ENDS.

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Etwas sehr Schlechtes ist passiert!"
                     :message "Wir haben ein Team von gut ausgebildeten Gnomen entsandt, um das Problem zu lösen."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn on-error [request response]
  (error-page
    {:status 403
     :title (str "Access to " (:uri request) " is not authorized")}))

(defn wrap-restricted [handler]
  (restrict handler {:handler authenticated?
                     :on-error on-error}))

(defn wrap-auth [handler]
  (let [backend (session-backend)]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-access-rules {:rules rules :on-error on-error})
      wrap-auth
      wrap-webjars
      wrap-flash
      (wrap-session {:timeout 0 :cookie-attrs {:http-only true}})   ;;  A :timeout value less than or equal to zero indicates the session should never expire.
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc :timeout 0)
            (dissoc :session)))
      wrap-internal-error))
