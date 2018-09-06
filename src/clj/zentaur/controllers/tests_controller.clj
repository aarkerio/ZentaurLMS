(ns zentaur.controllers.tests-controller
  (:require [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.admin.tests-view :as tests-view]
            [ring.util.http-response :as response]))

;; GET /tests
(defn get-tests [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests {:user-id user-id})]
    (layout/application
        (merge base {:title "Tests" :contents (tests-view/index tests) }))))

(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

(defn about-page [request]
  (let [base     (basec/set-vars request)]
     (layout/application {:contents base})))

;;;;;  ADMIN FUNCTIONS


(defn create-test
  "POST /admin/tests"
  [request]
  (log/info (str ">>> REQUEST >>>>> " request))
  (let [params       (-> request :params)
        user-id      (-> request :identity :id)
        clean-params (dissoc params :__anti-forgery-token :submit :button-save)]
    (model-test/create-test! clean-params user-id)
    (-> (response/found "/admin/tests"))))

(defn admin-index
  "GET /admin/tests"
  [request]
  (let [base     (basec/set-vars request)
        _        (log/info (str ">>> BAAASSEEEEEEEEE >>>>> " base))
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
        test-id (Integer/parseInt (get params "test-id"))
        _ (log/info (str ">>> user-id >>>>> " user-id "     >>>>> test-id " test-id ))]
    (response/ok (model-test/get-test-nodes test-id user-id) )))
