(ns zentaur.core-test
  (:use midje.sweet)
  (:require [zentaur.models.uploads :as uploads]
            [factory-time.core :as ft]
            [factory-time.core :refer [build create!]]
            [clojure.test :refer [deftest is run-tests]])))


(deftest download
  (testing "With valid input"
    (testing "it should return a header map with filename included"
      (is (= first_map (unit/download {:id 1}))))))

(deftest download
  (with-redefs [model-upload/get-upload (constantly {:filename "cool.pdf"})]
    (testing "With valid input"
      (testing "it should return a header map with filename included"
        (is (= first_map (uploads/download {:id 1})))))))




