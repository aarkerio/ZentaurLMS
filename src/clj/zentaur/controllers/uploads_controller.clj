(ns zentaur.controllers.uploads-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.models.uploads :as model-upload]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.uploads-view :as admin-uploads-view]))

;;;;;;;;;;;;;;;;;;;;;;;     ADMIN FUNCTIONS    ;;;;;;;;;;;;;;;;;;;;
(defn admin-uploads
  "GET /admin/uploads"
  [request]
  (let [base       (basec/set-vars request)
        user-id    (-> request :identity :id)
        csrf-field (:csrf-field base)
        files      (model-upload/get-uploads user-id)]
    (basec/parser
     (layout/application (merge base {:title "Uploads" :contents (admin-uploads-view/index files csrf-field) })))))

(defn upload-file
  "POST /admin/uploads"
  [request]
  (let [user-id   (-> request :identity :id)
        params    (-> request :params)
        result    (model-upload/upload-file params user-id)
        message   (if (= result false) "wrong" "success")]
    (log/info (str ">>> user-id >>>>> " user-id  ">>> params >> " params))
    (-> (response/found "/admin/uploads")
        (assoc :flash message))))

(defn process
  "GET /admin/uploads/process/:id"
  [request]
  (let [base       (basec/set-vars request)
        id         (-> request :params :id)
        csrf-field (:csrf-field base)
        upload     (model-upload/get-upload id)]
    (basec/parser
     (layout/application (merge base {:title "Process" :contents (admin-uploads-view/process upload csrf-field) })))))

(defn extract
  "GET /admin/uploads/extract/:id
  Convert to text"
  [params]
  (let [id    (:id params)
        file  (model-upload/extract-text id)]
    (-> (response/found "/admin/uploads"))))

(defn download
  "GET /admin/uploads/download/:id"
  [request]
  (let [id (-> request :path-params :id)]
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

(defn archive
  "GET /admin/uploads/archive/:id"
  [request]
  (let [base      (basec/set-vars request)
        id        (-> request :identity :id)
        csrf-field (:csrf-field base)
        file      (model-upload/get-upload id)]
    (-> (response/found "/admin/uploads"))))

(defn token
  "POST /uploads/token"
  [request]
  (log/info (str ">>> REQUEST >>>>> " request))
  (let [csrf-field      (:anti-forgery-token request)]
    (basec/json-parser {:anti-forgery-token csrf-field})))
