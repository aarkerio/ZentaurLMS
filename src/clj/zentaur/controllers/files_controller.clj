(ns zentaur.controllers.files-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.models.files :as model-files]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.files-view :as files-view]))

(defn index
  "GET /vclass/files/:type"
  [request]
  (let [base       (basec/set-vars request)
        csrf-field (:csrf-field base)
        files      (model-files/get-files user-id)]
    (basec/parser
     (layout/application (merge base {:title "My Files" :contents (files-view/index files csrf-field) })))))

(defn upload
  "POST /vclass/files/uploads"
  [request]
  (let [user-id   (-> request :identity :id)
        params    (:params request)
        result    (model-upload/upload-file params user-id)
        message   (if (= result false) "wrong" "success")]
    (assoc (response/found "/vclass/files") :flash (assoc params :message message))))

(defn download
  "GET /vclass/files/:identif"
  [{:keys [path-params]}]
  (let [identif (:identif path-params)]
    (model-files/download identif)))

(defn archive
  "GET /vclass/files/archive/:identif"
  [{:keys [path-params]}]
  (model-files/toggle-archive path-params)
    (assoc (response/found "/vclass/files")))

