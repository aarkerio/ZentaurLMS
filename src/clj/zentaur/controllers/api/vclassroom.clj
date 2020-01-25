(ns zentaur.controllers.api.vclassroom
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.application-layout :as layout]
            [zentaur.hiccup.admin.tests-view :as tests-view]
            [ring.util.http-response :as response]))

(def non-ascii {:escape-non-ascii true}) ;; UTF-8 support for cheshire

(defn create-test
  "POST /api/tests"
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)]
    (model-test/create-test! clean-params user-id)
    (response/found "/admin/tests")))

(defn create-question
  "POST /api/createquestion. JSON response."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id :active true)
        response     (model-test/create-question! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn update-question
  "POST /api/updatequestion. JSON response."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id :active true)
        response     (model-test/update-question! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn update-answer
  "POST /api/updateanswer. JSON response."
  [request]
  (let [params       (:params request)
        new-params   (assoc params :active true)
        response     (model-test/update-answer! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn update-test
  "POST /api/updatetest. JSON response."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        full-params  (assoc params :user-id user-id)
        response     (model-test/update-test! full-params)]
    (log/info (str ">>> update-test response >>>>> " response))
    (response/ok (ches/encode response non-ascii))))

(defn create-answer
  "POST /api/createanswer. JSON response."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id)
        response     (model-test/create-answer! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn load-test
  "POST /api/load-test.  Build a JSON to charge one test in ClojureScript"
  [{:keys [identity params]}]
  (let [user-id  (:id identity)
        test-id  (Integer/parseInt (:test-id params))
        response (model-test/get-test-nodes test-id user-id)]
    (response/ok (ches/encode response non-ascii))))

(defn delete-test
  "DELETE /api/deletetest. JSON response."
  [{:keys [params]}]
  (response/ok {:response (model-test/remove-test params)}))

(defn delete-question
  "DELETE /api/deletequestion. JSON response."
  [{:keys [params]}]
  (response/ok {:response (model-test/remove-question params)}))

(defn delete-answer
  "DELETE /api/deleteanswer. JSON response."
  [{:keys [params]}]
    (response/ok {:response (model-test/remove-answer params)}))
