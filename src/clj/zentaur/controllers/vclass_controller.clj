(ns zentaur.controllers.vclass-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.vclassrooms-view :as vclass-view]
            [zentaur.libs.helpers :as h]
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
        params    (:params request)
        result    (model-vclass/create-vclass params user-id)
        message   (if (= result false) "wrong" "success")]
    (assoc (response/found "/vclass/index") :flash  message)))

(defn show
  "GET /vclass/show/:id"
  [request]
  (let [base       (basec/set-vars request)
        id         (-> request :params :id)
        csrf-field (:csrf-field base)
        upload     (model-vclass/get-vclass id)]
    (basec/parser
     (layout/application (merge base {:title "Process" :contents (vclass-view/show upload csrf-field) })))))

(defn toggle-published
  "GET /vclass/toggle/:uurlid/:draft"
  [{:keys [path-params]}]
  (model-vclass/toggle path-params)
    (assoc (response/found "/vclass/index") :flash h/msg-erfolg))


(defn delete-vclass
  "DELETE /vclass/delete/:uurlid"
  [params]
  (let [uurlid (params :uurlid)]
    (model-vclass/destroy {:uurlid uurlid})
    (assoc (response/found "/vclass/index") :flash h/msg-erfolg)))
