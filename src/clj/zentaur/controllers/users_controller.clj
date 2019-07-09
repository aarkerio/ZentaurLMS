(ns zentaur.controllers.users-controller
  (:require [zentaur.models.users :as model-user]
            [zentaur.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.users-view :as users-view]
            [ring.util.http-response :as response]))

(defn admin-users
  "GET /admin/users"
  [request]
  (let [base  (basec/set-vars request)
        users (model-user/get-users true)
        roles (model-user/get-roles)]
    (basec/parser
     (layout/application (merge base {:contents (users-view/index base users roles)})))))

(defn create-user
  "POST /admin/users"
  [request]
  (let [params       (-> request :params)
        clean-params (dissoc params :__anti-forgery-token  :submit)]
    (model-user/create clean-params)
    (-> (response/found "/admin/users"))))

(defn login-page
  "GET /login"
  [request]
  (let [base  (basec/set-vars request)]
    (basec/parser
     (layout/application (merge base {:title "Login" :contents (users-view/login base) })))))

(defn post-login
  "POST /login"
  [{{email "email" password "password"} :form-params session :session :as req}]
  (let [user (model-user/get-user-by-email-and-password email password)]
    ;; If authenticated
    (if-not (nil? (:user user))
      (do
         (assoc (response/found "/")
           :session (assoc session :identity (:user user)) :flash "Bitte schön!"))
         (assoc (response/found "/login") :flash "Etwas stimmt nicht mit deinem Zugriffsprozess") )) )

(defn clear-session! [request]
  (-> (response/found "/")
      (assoc :session nil :flash "Du bist raus!")))


