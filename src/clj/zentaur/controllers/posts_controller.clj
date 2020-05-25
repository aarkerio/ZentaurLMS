(ns ^{:doc "Posts controller"} zentaur.controllers.posts-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.admin.posts-view :as admin-posts-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.posts-view :as posts-view]
            [zentaur.models.posts :as model-post]
            [zentaur.models.tests :as model-test]))

(defn index
  "GET  /  (index site)"
  [request]
  (let [base       (basec/set-vars request)
        csrf-field (:csrf-field base)
        subjects   (model-test/get-subjects)
        levels     (model-test/get-levels)
        langs      (model-test/get-langs)
        identity   (:identity base)]
    (basec/parser
     (layout/application (merge base {:title "Welcome" :contents (posts-view/index csrf-field subjects levels langs identity)})))))

(defn listing
  "GET  /posts/listing/:page"
  [request]
  (let [base           (basec/set-vars request)
        items-per-page 5 ;; model and view need this
        pre-page       (-> request :path-params :page)
        page           (if (every? #(Character/isDigit %) pre-page) (Integer/parseInt pre-page) 1)
        posts          (model-post/get-posts page items-per-page)]
    (basec/parser
     (layout/application (merge base {:title "List of Posts" :contents (posts-view/listing posts page)})))))

(defn show
  "GET /posts/show/:id"
  [request]
  (let [base     (basec/set-vars request)
        pre-id   (-> request :path-params :id)
        id       (Integer/parseInt pre-id)
        post     (model-post/get-post id)]
    (if (nil? post)
      (basec/redirect-to "/posts/listing/1" "404. Page not found")
      (basec/parser
       (layout/application (merge base { :contents (posts-view/show post base)}))))))

(defn toggle-published
  "GET '/admin/posts/publish/:id/:published'"
  [{:keys [path-params]}]
  (model-post/toggle path-params)
  (assoc (response/found "/admin/posts/listing/1") :flash basec/msg-erfolg ))

(defn search
  "POST /search"
  [request]
  (let [base    (basec/set-vars request)
        terms   (-> request :params :terms)
        lang    (or (-> request :params :lang) "en")
        results (model-post/search terms lang)]
    (basec/parser
     (layout/application (merge base { :contents (posts-view/search base results)})))))

;;;;;;;;;;;;;;;;     ADMIN SECTION      ;;;;;;;;;;;;;;;;;;;;;;;

(defn admin-posts
  "GET /admin/posts/listing/:page"
  [request]
  (let [base           (basec/set-vars request)
        user-id        (-> request :identity :id)
        pre-page       (-> request :path-params :page)
        page           (if (every? #(Character/isDigit %) pre-page) (Integer/parseInt pre-page) 1)
        items-per-page 10 ;; model and view need this
        posts          (model-post/admin-get-posts user-id page items-per-page)]
    (if (empty? posts)
      (assoc (response/found "/admin/posts/listing/1") :flash basec/msg-fehler)
      (basec/parser (layout/application
                     (merge base {:title "Admin Posts" :contents (admin-posts-view/index posts page items-per-page)}))))))

(defn save-post
  "POST /admin/posts"
  [params]
  (let [errors (model-post/save-post! (dissoc params :__anti-forgery-token :button-save))]
    (if (contains? errors :flash)
      (assoc (response/found "/admin/posts/new") :flash (basec/map-to-query-string errors))
      (assoc (response/found "/admin/posts/listing/1") :flash basec/msg-erfolg))))

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
      (assoc (response/found "/admin/posts/listing/1") :flash basec/msg-erfolg))))

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
    (assoc (response/found "/admin/posts/listing/1") :flash basec/msg-erfolg)))
