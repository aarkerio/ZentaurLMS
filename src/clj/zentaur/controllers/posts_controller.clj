(ns ^{:doc "Posts controller"} zentaur.controllers.posts-controller
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.posts-view :as posts-view]
            [zentaur.hiccup.admin.posts-view :as admin-posts-view]
            [zentaur.libs.helpers :as h]
            [zentaur.models.posts :as model-post]))

(def msg-erfolg "Veränderung wurden erfolgreich gespeichert")
(def msg-fehler "Etwas ging schief")

(defn get-posts
  "GET  /  (index site)"
  [request]
  (let [base     (basec/set-vars request)
        posts    (model-post/get-posts)]
    (basec/parser
     (layout/application (merge base {:title "Posts" :contents (posts-view/index posts)})))))

(defn save-comment
  "POST /post/savecomment"
  [request]
  (let [body-params (:body-params request)
        identity    (:identity request)
        comment     (:comment body-params)
        post_id     (Integer/parseInt (:post_id body-params))
        user_id     (:id identity)]
    (model-post/save-comment!
     (assoc {} :post_id post_id :comment comment :user_id user_id))
    (basec/json-parser {:comment comment :last_name (:last_name identity)})))

(defn single-post
  "GET /posts/view/:id"
  [request]
  (let [base     (basec/set-vars request)
        pre-id   (-> request :path-params :id)
        id       (Integer/parseInt pre-id)
        post     (model-post/get-post id)
        comments (model-post/get-comments id)]
    (basec/parser
     (layout/application (merge base { :contents (posts-view/show post base comments) })))))

(defn toggle-published
  "GET '/admin/posts/publish/:id/:published'"
  [{:keys [path-params]}]
  (model-post/toggle path-params)
    (assoc (response/found "/admin/posts") :flash msg-erfolg))

;;;;;;;;;;;;;;;;     ADMIN SECTION      ;;;;;;;;;;;;;;;;;;;;;;;

(defn map-to-query-string [m]
  (str/join " " (map (fn [[k v]] (str (name k) " " v)) m)))

(defn admin-posts
  "GET /admin/posts"
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        posts    (model-post/admin-get-posts user-id)]
    (basec/parser (layout/application
                   (merge base {:title "Admin Posts" :contents (admin-posts-view/index posts) })))))

(defn save-post
  "POST /admin/posts"
  [params]
  (let [errors (model-post/save-post! (dissoc params :__anti-forgery-token :button-save))]
    (if (contains? errors :flash)
      (assoc (response/found "/admin/posts/new") :flash (map-to-query-string errors))
      (assoc (response/found "/admin/posts") :flash "Beiträge wurden erfolgreich gespeichert"))))

(defn show-post
  "GET. /admin/posts/:id"
  [request]
  (log/info (str ">>> PARAM >>>>> " request))
  (let [base     (basec/set-vars request)
        params   (:path-params request)
        _        (log/info (str ">>> PARAMS >>>>> " params))
        post-id  (Integer/parseInt (:id params))
        _        (log/info (str ">>> POSTS-ID >>>>> " post-id))
        post     (model-post/get-post post-id)]
    (basec/parser (layout/application
                   (merge base {:title "Edit Post" :contents (admin-posts-view/edit base post)})))))

(defn update-post
  "POST /admin/posts/update"
  [params]
  (let [errors (model-post/save-post! (dissoc params :__anti-forgery-token :button-save))]
    (if (contains? errors :flash)
      (assoc (response/found "/admin/posts/new") :flash (map-to-query-string errors))
      (assoc (response/found "/admin/posts") :flash "Beiträge wurden erfolgreich gespeichert"))))


(defn admin-new
  "GET /admin/posts/new"
  [request]
  (let [base     (basec/set-vars request)]
    (basec/parser (layout/application
                   (merge base {:title "New Post" :contents (admin-posts-view/new base)})))))

(defn delete-post
  "DELETE /admin/posts/:id"
  [params]
  (let [id (params :id)]
    (model-post/destroy id)
    (assoc (response/found "/admin/posts") :flash msg-erfolg)))
