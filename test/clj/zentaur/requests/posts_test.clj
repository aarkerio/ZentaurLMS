(ns zentaur.core-test
  (:require [clojure.test :refer :all]
            [your-app.core :refer :all]
            [ring.mock.request :as mock]))

(deftest your-handler-test
  (is (= (your-handler (mock/request :get "/doc/10"))
         {:status  200
          :headers {"content-type" "text/plain"}
          :body    "Your expected result"})))

(deftest your-json-handler-test
  (is (= (your-handler (-> (mock/request :post "/api/endpoint")
                           (mock/json-body {:foo "bar"})))
         {:status  201
          :headers {"content-type" "application/json"}
          :body    {:key "your expected result"}})))
