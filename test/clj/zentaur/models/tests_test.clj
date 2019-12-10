(ns zentaur.test.tests
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [zentaur.routes.home :as fh]
            [flex-webapp.lib.common :as com]
            [flex-webapp.middleware.formats :as formats]
            [ring.mock.request :as mock]))

(deftest test-function
  (testing "scramble function"
    (is (= true  (com/scramble? "rekqodlw" "world")))
    (is (= true  (com/scramble? "cedewaraaossoqqyt" "codewars")))
    (is (= false (com/scramble? "katas" "steak")))
    (is (= true  (com/scramble? "nacisnegxamddmtimajsrmete" "argentina")))
    (is (= false (com/scramble? "grcoemtwYYYtj" "ios")))))

(defn test-request [resource web-app & params]
  (web-app {:request-method :get :uri resource :params params}))

(deftest search-result
  (testing "The index page"
    (is (= 200
        (:status (fh/home-routes (mock/request :get "/")))))))

(deftest a-test
   (testing "Test POST request to /api/v1/check returns expected response"
     (let [response (fh/home-routes (mock/request :post "/api/v1/check" {"str1" "cedewaraaossoqqyt" "str2" "codewars"}))
           body     (:body response)]
       (is (= (:status response) 200))
       (is (= (:msg body) true)))))


(deftest get-test-nodes
  (testing "JSON response for the API"
    (let [response (fh/home-routes (mock/request :post "/admin/tests/load" {"test-id" 1 "user-id" 1}))
          body     (:body response)]
      (is (= (:msg body) true)))))
