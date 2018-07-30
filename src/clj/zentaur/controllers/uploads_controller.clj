(ns zentaur.controllers.uploads-controller
  (:require [zentaur.models.uploads :as model-upload]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.admin.uploads-view :as admin-uploads-view]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]))

;;;;;;;;;;;;;;;;;;;;;;;     ADMIN FUNCTIONS    ;;;;;;;;;;;;;;;;;;;;
;; GET /admin/uploads
(defn admin-uploads [request]
  (let [base       (basec/set-vars request)
        user-id    (-> request :identity :id)
        csrf-field (:csrf-field base)
        files      (model-upload/get-uploads user-id)]
    (layout/application (merge base {:title "Posts" :contents (admin-uploads-view/index files csrf-field) }))))

;; POST /admin/uploads
(defn upload-file [request]
  (let [user-id   (-> request :identity :id)
        params    (-> request :params)]
    (log/info (str ">>> REQUEST >>>>> " request ))
    (model-upload/upload-file params user-id)
    (-> (response/found "/admin/uploads"))))

;; GET /admin/uploads/process/:id
(defn process [request]
  (let [base       (basec/set-vars request)
        id         (-> request :identity :id)
        csrf-field (:csrf-field base)
        file       (model-upload/get-upload id)]
    (log/info (str ">>> REQUEST >>>>> " request ))
    (layout/application (merge base {:title "Process" :contents (admin-uploads-view/process file csrf-field) }))))

;; GET /admin/uploads/extract/:id
(defn extract [params]
  (let [id    (-> params :id)
        file  (model-upload/extract-text id)]
    (log/info (str ">>> REQUEST >>>>> " file))
    (-> (response/found "/admin/uploads"))))

(defn download
  "GET /admin/uploads/download/:id"
  [params]
  (let [id       (-> params :id)
        _        (log/info (str ">>> PARAM >>>>> " params))
        upload   (model-upload/get-upload id)
        filename (:filename upload)
        body     (clojure.java.io/file (str "resources/public/uploads/" filename))]
    {:status 200
     :body body
     :headers {"Content-Type" "application/pdf"
               "Content-Length" (str (.length body))
               "Cache-Control" "no-cache"
               "Content-Disposition" (str "attachment; filename=" filename)}}))

;; GET /admin/uploads/archive/:id
(defn archive [request]
  (let [base      (basec/set-vars request)
        id        (-> request :identity :id)
        csrf-field (:csrf-field base)
        file      (model-upload/get-upload id)]
    (log/info (str ">>> REQUEST >>>>> " request ))
    (-> (response/found "/admin/uploads"))))
