(ns ^{:doc "Posts controller"} zentaur.controllers.posts-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.admin.posts-view :as admin-posts-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.posts-view :as posts-view]
            [zentaur.models.posts :as model-post]
            [zentaur.models.tests :as model-test]))

(defn get-posts
  "GET  /  (index site)"
  [request]
  (let [base       (basec/set-vars request)
        posts      (model-post/get-posts)
        csrf-field (:csrf-field base)
        subjects   (model-test/get-subjects)
        levels     (model-test/get-levels)
        langs      (model-test/get-langs)
        identity   (:identity base)]
    (basec/parser
     (layout/application (merge base {:title "List of Posts" :contents (posts-view/index posts csrf-field subjects levels langs identity)})))))

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
    (assoc (response/found "/admin/posts") :flash basec/msg-erfolg))

;;;;;;;;;;;;;;;;     ADMIN SECTION      ;;;;;;;;;;;;;;;;;;;;;;;

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
      (assoc (response/found "/admin/posts/new") :flash (basec/map-to-query-string errors))
      (assoc (response/found "/admin/posts") :flash basec/msg-erfolg))))

(defn show-post
  "GET. /admin/posts/:id"
  [request]
  (let [base     (basec/set-vars request)
        params   (:path-params request)
        post-id  (Integer/parseInt (:id params))
        post     (model-post/get-post post-id)]
    (basec/parser (layout/application
                   (merge base {:title "Edit Post" :contents (admin-posts-view/edit base post)})))))

(defn update-post
  "POST /admin/posts/update"
  [{:keys [params]}]
  (let [errors (model-post/update-post! (dissoc params :__anti-forgery-token :button-save))]
    (if (contains? errors :flash)
      (assoc (response/found (str "/admin/posts/" (:id params))) :flash (basec/map-to-query-string errors))
      (assoc (response/found "/admin/posts") :flash basec/msg-erfolg))))


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
    (assoc (response/found "/admin/posts") :flash basec/msg-erfolg)))
