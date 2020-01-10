(ns zentaur.controllers.tests-controller-test
  "Integration tests with HTTP calls"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [ring.mock.request :as mock]
            [zentaur.handler :as zh]
            [zentaur.config-test :as ct]))

(deftest ^:integration get-test-nodes
  (testing "JSON response for the API"
    (let [response (zh/app (mock/request :post "/admin/tests/load" {"test-id" 1 "user-id" 1}))
          _        (ct/foo)
          body     (:body response)]
      (is (= (:msg body) true)))))

;; (deftest ^:integration a-test
;;    (testing "Test POST request to /api/v1/check returns expected response"
;;      (let [response (zh/app (mock/request :post "/api/v1/check" {"str1" "cedewaraaossoqqyt" "str2" "codewars"}))
;;            body     (:body response)]
;;        (is (= (:status response) 200))
;;        (is (= (:msg body) true)))))

(run-tests)  ;; run tests in this NS

;; (defn create-question
;;   "POST /admin/tests/createquestion. JSON response."
;;   [request]
;;   (let [params       (:params request)
;;         user-id      (-> request :identity :id)
;;         new-params   (assoc params :user-id user-id :active true)
;;         response     (model-test/create-question! new-params)]
;;     (response/ok (ches/encode response non-ascii))))
