(ns zentaur.controllers.files-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.layouts.basic-layout :as blay]
            [zentaur.hiccup.files-view :as files-view]
            [zentaur.models.files :as model-files]))

(defn index
  "GET. /vclass/files/:type"
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> base :identity :id)
        files    (model-files/get-files user-id)]
    (basec/parser
     (blay/application (merge base {:title "My Files" :contents (files-view/index files base)})))))

(defn upload
  "POST. /vclass/files/uploads"
  [{:keys [params identity]}]
  (let [user-id   (:id identity)
        uname     (:uname identity)
        result    (model-files/upload-file params user-id uname)
        message   (if (= result false) "wrong" "success")]
    (assoc (response/found "/vclass/files/img") :flash (assoc params :message message))))

(defn download
  "GET. /vclass/files/:identif"
  [{:keys [path-params]}]
  (let [identif (:identif path-params)]
    (model-files/download identif)))

(defn archive
  "GET. /vclass/files/archive/:identif"
  [{:keys [path-params]}]
  (model-files/toggle-archive path-params)
    (assoc (response/found "/vclass/files")))

