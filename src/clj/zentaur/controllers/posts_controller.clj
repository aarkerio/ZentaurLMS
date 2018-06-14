(ns zentaur.controllers.posts-controller
  (:require [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.posts-view :as posts-view]
            [zentaur.hiccup_templating.admin.posts-view :as admin-posts-view]
            [zentaur.models.posts :as model-post]
            [zentaur.libs.helpers :as h]
            [clj-time.local :as l]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [ring.util.request :refer [body-string]]))

;; GET /posts
(defn get-posts [request]
  (let [base     (basec/set-vars request)
        posts    (model-post/get-posts)]
    (layout/application
        (merge base {:title "Posts" :contents (posts-view/index posts) }))))

;; POST /post/savecomment
(defn save-comment [request]
  (let [body-params (:body-params request)
        identity    (:identity request)
        comment     (:comment body-params)
        post_id     (Integer/parseInt (:post_id body-params))
        user_id     (:id identity)]
    (log/info (str ">>>BODY  PARAM >>>>> " body-params))
    (model-post/save-comment!
      (assoc {} :created_at (l/local-now) :post_id post_id :comment comment :user_id user_id))
    (basec/json-response { :comment comment :created_at (h/format-time) :last_name (:last_name identity) } )))

;; GET /posts/:id
(defn single-post [request]
  (let [base     (basec/set-vars request)
        params   (:params request)
        id       {:id (Integer/parseInt (get params :id))}
        post     (model-post/get-post id)
        comments (model-post/get-comments id)]
    (layout/application
       (merge base { :contents (posts-view/show post base comments) }))))

;; POST /posts
(defn save-post! [ {:keys [params]} ]
  (if-let [errors (model-post/validate-post params)]
    (-> (response/found "/posts")
        (assoc :flash (assoc params :errors errors)))
    (do
      (let [active    (contains? params :active)
            discution (contains? params :discution)
            int_ui    (Integer/parseInt (get params :user_id))]
      (model-post/save-post!
        (assoc params :created_at (l/local-now) :active active :discution discution :user_id int_ui))
      (response/found "/posts")))))

;; DELETE /posts
(defn delete-post [{:keys [params]}]
  (do
    (let [newparams {:id (Integer/parseInt (get params :id))}]
      (model-post/destroy newparams)
      (response/ok "body"))))

;;;;;;;;;;;;;;;;     ADMIN SECTION      ;;;;;;;;;;;;;;;;;;;;;;;

;; GET /admin/posts
(defn admin-posts [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        posts    (model-post/admin-get-posts user-id)]
    (layout/application
        (merge base {:title "Admin Posts" :contents (admin-posts-view/index posts) }))))

