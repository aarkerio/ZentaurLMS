(ns zentaur.controllers.vclass-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.vclassrooms-view :as vclass-view]
            [zentaur.models.vclassrooms :as model-vclass]
            [ring.util.http-response :as response]))

(defn index
  "GET /vclass/index"
  [request]
  (let [base          (basec/set-vars request)
        user-id       (-> request :identity :id)
        csrf-field    (:csrf-field base)
        vclassrooms   (model-vclass/get-vclassrooms user-id)]
    (basec/parser
     (layout/application (merge base {:title "vClassrooms" :contents (vclass-view/index vclassrooms csrf-field)})))))

(defn create-vclass
  "POST /vclass/index"
  [request]
  (let [user-id   (-> request :identity :id)
        params    (dissoc (:params request) :__anti-forgery-token)
        result    (model-vclass/create-vclass params user-id)
        message   (if (= result false) basec/msg-fehler basec/msg-erfolg)]
    (assoc (response/found "/vclass/index") :flash  message)))

(defn update-vc
  "POST /vclass/show"
  [request]
  (let [user-id   (-> request :identity :id)
        params    (dissoc (:params request) :__anti-forgery-token)
        uurlid    (:uurlid params)
        result    (model-vclass/update-vclass params user-id)
        message   (if (= result false) basec/msg-fehler basec/msg-erfolg)]
    (assoc (response/found (str "/vclass/show/" uurlid)) :flash  message)))

(defn show
  "GET /vclass/show/:uurlid"
  [request]
  (let [base       (basec/set-vars request)
        uurlid     (-> request :path-params :uurlid)
        csrf-field (:csrf-field base)
        user-id    (-> base :identity :id)
        vclass     (model-vclass/get-vclass uurlid user-id)]
    (basec/parser
     (layout/application (merge base {:title (:name vclass) :contents (vclass-view/show vclass csrf-field) })))))

(defn toggle-published
  "GET /vclass/toggle/:uurlid/:draft"
  [{:keys [path-params]}]
  (model-vclass/toggle path-params)
    (assoc (response/found "/vclass/index") :flash basec/msg-erfolg))

(defn delete-vclass
  "DELETE /vclass/delete/:uurlid"
  [params]
  (let [uurlid (params :uurlid)]
    (model-vclass/destroy {:uurlid uurlid})
    (assoc (response/found "/vclass/index") :flash basec/msg-erfolg)))
