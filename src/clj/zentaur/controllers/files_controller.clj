(ns zentaur.controllers.files-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.layouts.basic-layout :as blay]
            [zentaur.hiccup.files-view :as files-view]
            [zentaur.libs.helpers :as h]
            [zentaur.models.files :as model-files]))

(defn- ^:private load-tpl
  "Load full or basic template"
  [view type]
  (if (= type "img")
    (blay/application view)
    (layout/application view)))

(defn index
  "GET. /vclass/files"
  [request]
  (let [type     (-> request :path-params :type)
        base     (basec/set-vars request)
        user-id  (-> base :identity :id)
        files    (model-files/get-files user-id)
        view     (merge base {:title "My Files" :contents (files-view/index files base type)})
        tpl      (load-tpl view type)]
    (basec/parser tpl)))

(defn upload
  "POST. /vclass/files"
  [{:keys [params identity]}]
  (let [type      (:type params)
        user-id   (:id identity)
        uname     (:uname identity)
        result    (model-files/upload-file params user-id uname)
        message   (if (= result false) h/msg-fehler h/msg-erfolg)]
    (assoc (response/found (str "/vclass/files/" type)) :flash  message)))

(defn download
  "GET. /vclass/files/download/:uurlid"
  [{:keys [path-params entity]}]
  (let [uurlid  (:uurlid path-params)
        user-id (:id entity)]
    (model-files/download uurlid user-id)))

(defn archive
  "GET. /vclass/files/archive/:type/:uurlid"
  [{:keys [path-params]}]
  (log/info (str ">>> PARAM path-params >>>>> " path-params))
  (let [{:keys [type uurlid archived]} path-params]
    (model-files/toggle-archive uurlid archived)
    (assoc (response/found (str "/vclass/files/" type)) :flash "File modified" )))

