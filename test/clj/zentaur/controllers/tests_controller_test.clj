(ns zentaur.controllers.tests-test
  "Integration tests with HTTP calls"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [ring.mock.request :as mock]
            [zentaur.routes.home :as rh]))

(deftest ^:integration get-test-nodes
  (testing "JSON response for the API"
    (let [response (rh/home-routes (mock/request :post "/admin/tests/load" {"test-id" 1 "user-id" 1}))
          body     (:body response)]
      (is (= (:msg body) true)))))

(deftest ^:integration a-test
   (testing "Test POST request to /api/v1/check returns expected response"
     (let [response (fh/home-routes (mock/request :post "/api/v1/check" {"str1" "cedewaraaossoqqyt" "str2" "codewars"}))
           body     (:body response)]
       (is (= (:status response) 200))
       (is (= (:msg body) true)))))

