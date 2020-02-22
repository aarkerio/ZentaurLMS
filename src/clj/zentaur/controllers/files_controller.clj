(ns zentaur.controllers.files-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.layouts.basic-layout :as blay]
            [zentaur.hiccup.files-view :as files-view]
            [zentaur.models.files :as model-files]
            [zentaur.models.vclassrooms :as model-vclass]))

(defn- ^:private get-files
  "Load full or basic template"
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> base :identity :id)
        archived (= "true" (-> request :path-params :archived))
        files    (model-files/get-files user-id archived)]
    (merge base {:title "My Files" :contents (files-view/index files base archived)})))

(defn index
  "GET. /vclass/files/:archived"
  [request]
  (let [view (get-files request)]
    (basec/parser (layout/application view))))

(defn popup
  "GET. /vclass/popup"
  [request]
  (let [view (get-files request)]
    (basec/parser (blay/application view))))

(defn upload
  "POST. /vclass/files"
  [{:keys [params identity]}]
  (let [type      (:type params)
        user-id   (:id identity)
        uname     (:uname identity)
        result    (model-files/upload-file params user-id uname)
        message   (if (= result false) basec/msg-fehler basec/msg-erfolg)]
    (assoc (response/found (str "/vclass/files/" type)) :flash  message)))

(defn download
  "GET. /vclass/files/download/:uurlid"
  [{:keys [path-params entity]}]
  (let [uurlid  (:uurlid path-params)
        user-id (:id entity)]
    (model-files/download uurlid user-id)))

(defn share
  "GET. /vclass/files/share/:uurlid"
  [request]
  (let [base        (basec/set-vars request)
        user-id     (-> base :identity :id)
        uname       (-> base :identity :uname)
        uurlid      (-> request :path-params :uurlid)
        file        (model-files/get-one-file user-id uurlid)
        vclassrooms (model-vclass/get-vclassrooms user-id)
        view        (merge base {:title "Share File" :contents (files-view/share-file file uname vclassrooms base)})]
    (basec/parser (layout/application view))))

(defn archive
  "GET. /vclass/files/archive/:uurlid/:achived"
  [{:keys [path-params]}]
  (log/info (str ">>> PARAM path-params >>>>> " path-params))
  (let [{:keys [uurlid archived]} path-params]
    (model-files/toggle-archive uurlid archived)
    (assoc (response/found (str "/vclass/files/" archived)) :flash "File modified" )))

