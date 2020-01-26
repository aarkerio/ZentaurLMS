(ns zentaur.middleware
  (:require
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   [buddy.auth.accessrules :refer [wrap-access-rules restrict]]
   [buddy.auth.backends.session :refer [session-backend]]
   [cheshire.generate :as cheshire]
   [clojure.tools.logging :as log]
   [cognitect.transit :as transit]
   [immutant.web.middleware :refer [wrap-session]]
   [muuntaja.middleware :refer [wrap-format wrap-params]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.flash :refer [wrap-flash]]
   [ring-ttl-session.core :refer [ttl-memory-store]]
   [zentaur.config :refer [env]]
   [zentaur.env :refer [defaults]]     ;; from the env/ dir
   [zentaur.layout :refer [error-page]]
   [zentaur.middleware.formats :as formats]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid JJJJ anti-forgery token"})}))

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

;; AUTH CONFIG STARTS
(defn admin-access [request]
  (let [identity (:identity request)]
    (true? (:admin identity))))

(defn user-access [request]
  (let [identity (:identity request)]
    (> (count (:email identity)) 4)))

(defn open-gates [request]
    true)

(def rules [{:pattern #"^\/admin.*"
             :handler admin-access
             :redirect "/notauthorized"},
            {:pattern #"^\/vclass.*"
             :handler user-access
             :redirect "/notauthorized"},
            {:pattern #"^\/api.*"
             :handler open-gates
             :redirect "/notauthorized"},
            {:pattern #"^\/user.*"
             :handler authenticated?}])
;; AUTH CONFIG ENDS

(defn wrap-auth [handler]
  (let [backend (session-backend)]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn wrap-base
  "Assembling all the pieces of he middleware"
  [handler]
  (-> ((:middleware defaults) handler)  ;; from env/../dev_middleware.clj
      (wrap-access-rules {:rules rules :on-error on-error})
      wrap-auth
      wrap-flash
      (wrap-session)
      (wrap-defaults
      (-> site-defaults
         (assoc-in [:security :anti-forgery] false)
         (assoc-in  [:session :store] (ttl-memory-store (* 500 30)))))
      wrap-internal-error))
