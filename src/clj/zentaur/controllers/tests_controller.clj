(ns zentaur.controllers.tests-controller
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.tests-view :as tests-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.models.tests :as model-test]
            [zentaur.models.users :as model-user]
            [ring.util.http-response :as response]))

(defn index
  "GET /vclass/tests. Display user's tests. Html response."
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests user-id)
        subjects (model-test/get-subjects)
        levels   (model-test/get-levels)]
    (basec/parser
     (layout/application (merge base {:title "Quiz Tests" :contents (tests-view/index tests base subjects levels)})))))

(defn edit
  "GET /vclass/tests/edit/:uurlid. Html response."
  [request]
  (let [base    (basec/set-vars request)
        uurlid  (-> request :path-params :uurlid)]
    (basec/parser
     (layout/application (merge base {:title "New Quiz Tests" :contents (tests-view/edit base uurlid) })))))

(defn create-test
  "POST /vclass/tests"
  [{:keys [params identity]}]
  (let [user-id      (:id identity)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)
        result       (model-test/create-test! clean-params user-id)
        msg          (if (false? result) "Etwas ging schief ;-(" "Test hinzufügen!! ;-)")]
    (assoc (response/found "/vclass/tests") :flash msg)))

(defn generate-test
  "POST /vclass/tests/generate"
  [{:keys [params session identity]}]
  (log/info (str ">>> PARAMSSSS kkkkkkkkkk >>>>> " params))
  (let [clean-params (dissoc params :__anti-forgery-token :submit :button-save)
        user-id      (:id identity)
        new-uurlid   (model-test/generate-test clean-params user-id)
        msg          (if (nil? new-uurlid) "Etwas ging schief ;-(" "Test hinzufügen!! ;-)")]
    (assoc (response/found (str "/vclass/tests/edit/" new-uurlid)) :flash msg)))

(defn delete-test
  "DELETE /vclass/tests/delete. Not really a delete."
  [{:keys [params]}]
  (let [result (model-test/remove-test params)
        msg    (if result basec/msg-erfolg basec/msg-fehler)]
    (basec/json-parser {:response msg})))

(defn apply-test
  "GET /vclass/tests/apply/:uurlid"
  [{:keys [path-params] :as request}]
  (let [base    (basec/set-vars request)
        uurlid  (:uurlid path-params)
        msg     (if true basec/msg-erfolg basec/msg-fehler)]
    (basec/parser
     (layout/application (merge base {:title "Vclassrrooms" :contents (tests-view/edit base uurlid) })))))


