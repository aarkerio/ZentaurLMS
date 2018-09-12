(ns zentaur.controllers.users-controller
  (:require [zentaur.models.users :as model-user]
            [zentaur.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.users-view :as users-view]
            [ring.util.http-response :as response]))

;; GET /admin/users
(defn admin-users [request]
  (let [base  (basec/set-vars request)
        users (model-user/get-users true)
        roles (model-user/get-roles)]
    (log/info (str ">>> R******** >>>>> " roles))
    (layout/application (merge base {:contents (users-view/index base users roles)} ))))

;; POST /admin/users
(defn create-user [request]
  (log/info (str ">>> REQUEST >>>>> " request))
  (let [params       (-> request :params)
        clean-params (dissoc params :__anti-forgery-token  :submit)]
    (model-user/create clean-params)
    (-> (response/found "/admin/users"))))

;; GET /login
(defn login-page [request]
  (let [base  (basec/set-vars request)]
    (layout/application (merge base {:title "Login" :contents (users-view/login base) }))))

;; POST /login
(defn post-login
  [{{email "email" password "password"} :form-params session :session :as req}]
  (let [user (model-user/get-user-by-email-and-password email password)
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


