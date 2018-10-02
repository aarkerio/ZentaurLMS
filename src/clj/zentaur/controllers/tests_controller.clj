(ns zentaur.controllers.tests-controller
  (:require [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.tests-view :as tests-view]
            [ring.util.http-response :as response]))

(defn get-tests
  ;; GET /tests
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests {:user-id user-id})]
    (layout/application
     (merge base {:title "Tests" :contents (tests-view/index tests) }))))

;;;;;  ADMIN FUNCTIONS

(defn create-test
  "POST /admin/tests"
  [request]
  (let [params       (-> request :params)
        user-id      (-> request :identity :id)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)]
    (model-test/create-test! clean-params user-id)
    (-> (response/found "/admin/tests"))))

(defn create-question
  "POST /admin/tests/savequestion"
  [request]
  (let [params       (-> request :params)
        user-id      (-> request :identity :id)
        _ ((log/info (str ">>> PARAMS NDEW QUESTION >>>>> " params)))
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)]
    (model-test/create-question! clean-params user-id)
    (-> (response/ok {:ok true}))))

(defn admin-index
  "GET /admin/tests"
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests user-id)]
    (layout/application
     (merge base {:title "Quiz Tests" :contents (tests-view/index tests base) }))))

(defn admin-edit
  "GET /admin/tests/edit"
  [request]
  (let [base     (basec/set-vars request)
        test-id  (-> request :params :id)]
    (layout/application
     (merge base {:title "New Quiz Tests" :contents (tests-view/edit base test-id) }))))

(defn load-json
  "POST /admin/tests/load"
  [{:keys [identity params]}]
  (let [user-id (:id identity)
        test-id (Integer/parseInt (get params :test-id))]
    (response/ok (model-test/get-test-nodes test-id user-id))))
