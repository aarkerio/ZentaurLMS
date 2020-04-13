(ns zentaur.controllers.users-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.admin.users-view :as users-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.libs.models.shared :as sh]
            [zentaur.models.users :as model-user]))

(defn admin-users
  "GET /admin/users/:archived"
  [request]
  (let [archived      (sh/truthy? (or (-> request :path-params :archived) "false"))
        base          (basec/set-vars request)
        users         (model-user/get-users archived)
        roles         (model-user/get-roles)]
    (basec/parser
     (layout/application (merge base {:contents (users-view/index base users roles archived)})))))

(defn create-user
  "POST /admin/users"
  [request]
  (let [params       (:params request)
        clean-params (dissoc params :__anti-forgery-token  :submit)
        response     (model-user/create clean-params)
        flash        (if (string? response) response basec/msg-erfolg)]
        (assoc (response/found "/admin/users/true") :flash flash)))

(defn login-page
  "GET /login"
  [request]
  (let [base (basec/set-vars request)]
    (basec/parser
     (layout/application (merge base {:title "Login" :contents (users-view/login base)})))))

(defn post-login
  "POST /login"
  [{{email "email" password "password"} :form-params session :session}]
  (let [user (model-user/get-user-by-email-and-password email password)]
    (if-not (nil? (:user user))
      (assoc (response/found "/") :session (assoc session :identity (:user user)) :flash "Willkommen zurück!")
      (assoc (response/found "/login") :flash "Etwas stimmt nicht mit deinem Zugriffsprozess"))))

(defn clear-session! [request]
  (assoc (response/found "/") :session nil :flash "Du bist raus!"))

