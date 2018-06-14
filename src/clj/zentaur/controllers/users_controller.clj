(ns zentaur.controllers.users-controller
  (:require [zentaur.models.users :as modusers]
            [zentaur.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.admin.users-view :as users-view]
            [ring.util.http-response :as response]))

(defn admin-users [request]
  (let [base (basec/set-vars request)]
    (layout/application (merge base { :contents "foo" } ))))

;; GET /login
(defn login-page [request]
  (log/info (str ">>> request >>>>> " request))
  (let [base (basec/set-vars request)]
    (layout/application (merge base {:title "Login" :contents (users-view/login base) }))))

;; POST /login
(defn post-login
  ;; Login into the app
  [{{email "email" password "password"} :form-params session :session :as req}]
  (let [user (modusers/get-user-by-email-and-password email password)
        _    (log/info (str ">>> password >>>>> " password))]
    ;; If authenticated
    (if-not (nil? (:user user))
      (do
         (assoc (response/found "/")
           :session (assoc session :identity (:user user)) :flash "Bitte schön!"))
         (assoc (response/found "/login") :flash "Etwas stimmt nicht mit deinem Zugriffsprozess") )) )

(defn clear-session! [request]
  (-> (response/found "/")
      (assoc :session nil :flash "Du bist raus!")))


