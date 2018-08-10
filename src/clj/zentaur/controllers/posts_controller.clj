(ns zentaur.controllers.posts-controller
  (:require [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.posts-view :as posts-view]
            [zentaur.hiccup_templating.admin.posts-view :as admin-posts-view]
            [zentaur.models.posts :as model-post]
            [zentaur.libs.helpers :as h]
            [clj-time.local :as l]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [ring.util.request :refer [body-string]]))

;; GET  /   (index site)
(defn get-posts [request]
  (let [base     (basec/set-vars request)
        posts    (model-post/get-posts)]
    (layout/application
        (merge base {:title "Posts" :contents (posts-view/index posts)}))))

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

;; DELETE /posts
(defn delete-post [{:keys [params]}]
  (do
    (let [newparams {:id (Integer/parseInt (get params :id))}]
      (model-post/destroy newparams)
      (response/ok "body"))))

;;;;;;;;;;;;;;;;     ADMIN SECTION      ;;;;;;;;;;;;;;;;;;;;;;;

(defn map-to-query-string [m]
  (str/join " " (map (fn [[k v]] (str (name k) " " v)) m)))

;; GET /admin/posts
(defn admin-posts [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        posts    (model-post/admin-get-posts user-id)]
    (layout/application
        (merge base {:title "Admin Posts" :contents (admin-posts-view/index posts) }))))

;; POST /admin/posts
(defn save-post [params]
  (let [errors (model-post/save-post! (dissoc params :__anti-forgery-token :button-save))]g
    (if (contains? errors :flash)
      (assoc (response/found "/admin/posts/new") :flash (map-to-query-string errors))
      (assoc (response/found "/admin/posts") :flash "Beiträge wurden erfolgreich gespeichert"))))

;; GET /admin/posts/new
(defn admin-new [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)]
    (layout/application
        (merge base {:title "New Post" :contents (admin-posts-view/new base user-id)}))))

