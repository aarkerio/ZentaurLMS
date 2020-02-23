(ns zentaur.controllers.tests-controller
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.tests-view :as tests-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [ring.util.http-response :as response]))

(defn index
  "GET /vclass/tests. Display user's tests. Html response."
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests user-id)
        subjects (model-test/get-subjects)]
    (basec/parser
     (layout/application (merge base {:title "Quiz Tests" :contents (tests-view/index tests base subjects)})))))

(defn edit
  "GET /vclass/tests/edit/:uurlid. Html response."
  [request]
  (let [base    (basec/set-vars request)
        uurlid  (-> request :path-params :uurlid)]
    (basec/parser
     (layout/application (merge base {:title "New Quiz Tests" :contents (tests-view/edit base uurlid) })))))

(defn create-test
  "POST /vclass/tests"
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)
        result       (model-test/create-test! clean-params user-id)
        msg          (if (false? result) "Etwas ging schief ;-(" "Test hinzufügen!! ;-)")]
    (assoc (response/found "/vclass/tests") :flash msg)))

(defn delete-test
  "DELETE /vclass/tests/delete. Not really a delete."
  [{:keys [params]}]
  (let [result (model-test/remove-test params)
        msg    (if result basec/msg-erfolg basec/msg-fehler)]
    (basec/json-parser {:response msg})))
