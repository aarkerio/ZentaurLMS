(ns zentaur.controllers.tests-controller
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.tests-view :as tests-view]
            [ring.util.http-response :as response]))

(def non-ascii {:escape-non-ascii true}) ;; UTF-8 support for cheshire

(defn get-tests
  "GET /tests. HTML response."
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests {:user-id user-id})]
    (layout/application
     (merge base {:title "List Tests" :contents (tests-view/index tests base)}))))

;;;;;  ADMIN FUNCTIONS

(defn create-test
  "POST /admin/tests"
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)]
    (model-test/create-test! clean-params user-id)
    (response/found "/admin/tests")))

(defn create-question
  "POST /admin/tests/createquestion. JSON reponse."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id :active true)
        response     (model-test/create-question! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn update-question
  "POST /admin/tests/updatequestion. JSON reponse."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id :active true)
        response     (model-test/update-question! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn update-answer
  "POST /admin/tests/updateanswer. JSON reponse."
  [request]
  (let [params       (:params request)
        new-params   (assoc params :active true)
        response     (model-test/update-answer! new-params)]
    (log/info (str ">>> update-answer response >>>>> " response))
    (response/ok (ches/encode response non-ascii))))

(defn create-answer
  "POST /admin/tests/createanswer. JSON reponse."
  [request]
  (let [params       (:params request)
        user-id      (-> request :identity :id)
        new-params   (assoc params :user-id user-id)
        response     (model-test/create-answer! new-params)]
    (response/ok (ches/encode response non-ascii))))

(defn admin-index
  "GET /admin/tests. Display user's tests."
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests user-id)]
    (basec/parser
     (layout/application (merge base {:title "Quiz Tests" :contents (tests-view/index tests base)})))))

(defn admin-edit
  "GET /admin/tests/edit/:id. Html response."
  [request]
  (let [base     (basec/set-vars request)
        test-id  (-> request :path-params :id)]
    (basec/parser
     (layout/application (merge base {:title "New Quiz Tests" :contents (tests-view/edit base test-id) })))))

(defn load-json
  "POST /admin/tests/load.  Build a JSON to charge one test in ClojureScript"
  [{:keys [identity params]}]
  (let [user-id  (:id identity)
        test-id  (Integer/parseInt (:test-id params))
        response (model-test/get-test-nodes test-id user-id)]
    (response/ok (ches/encode response non-ascii))))

(defn export-test-pdf
  "GET /admin/tests/exporttestpdf/:id. Create PDF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
    (model-test/export-pdf test-id user-id)))

(defn export-test-odf
  "GET /admin/tests/exporttestodf/:id. Create PDF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
    (model-test/export-odf test-id user-id)))

(defn delete-test
  "DELETE /admin/tests/deletetest. JSON response."
  [{:keys [params]}]
  (response/ok {:response (model-test/remove-test params)}))

(defn delete-question
  "DELETE /admin/tests/deletequestion. JSON response."
  [{:keys [params]}]
  (response/ok {:response (model-test/remove-question params)}))

(defn delete-answer
  "DELETE /admin/tests/deleteanswer. JSON response."
  [{:keys [params]}]
    (response/ok {:response (model-test/remove-answer params)}))
