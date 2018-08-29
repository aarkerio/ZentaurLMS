(ns zentaur.controllers.uploads-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.models.uploads :as model-upload]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.uploads-view :as admin-uploads-view]))

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

(defn process
  ;; GET /admin/uploads/process/:id
  [request]
  (let [_          (log/info (str ">>> REQUEST >>>>> " request ))
        base       (basec/set-vars request)
        id         (-> request :params :id)
        csrf-field (:csrf-field base)
        upload     (model-upload/get-upload id)]
    (layout/application (merge base {:title "Process" :contents (admin-uploads-view/process upload csrf-field) }))))

;; GET /admin/uploads/extract/:id
(defn extract [params]
  (let [id    (-> params :id)
        file  (model-upload/extract-text id)]
    (-> (response/found "/admin/uploads"))))

(defn download
  "GET /admin/uploads/download/:id"
  [params]
  (let [id (:id params)]
    (model-upload/download id)))

(defn export-test
  "POST /admin/uploads/export"
  [request]
  (let [user-id  (-> request :identity :id)
        body     (-> request :params :body)]
    (model-upload/export-test body user-id)))

(defn save-body
  "POST /admin/uploads/save"
  [params]
  (let [body      (:body params)
        db-record (:id   params)
        response  (model-upload/save-body body db-record)]
    (-> (response/ok response))))

;; GET /admin/uploads/archive/:id
(defn archive [request]
  (let [base      (basec/set-vars request)
        id        (-> request :identity :id)
        csrf-field (:csrf-field base)
        file      (model-upload/get-upload id)]
    (-> (response/found "/admin/uploads"))))
