(ns zentaur.models.tests-test
  "Business logic in models test"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [zentaur.models.tests :as mt]))

(deftest ^:business-logic test-function
   (testing "sub and string"
     (is (= true  (.contains "The Band Named Isis" "Isis")))))

(deftest ^:business-logic create-test!
  (testing "Create a new test"
    (let [user-id 1
          title " Test title"
          params  {:title title :hint "Some hint" :tags "tags" :user_id user-id :subject_id 1}
          test    (mt/create-test! params user-id)]
      (is (not (nil? (:tags test))))
      (is (= title  (:title test)))))



(deftest ^:business-logic eg-tests (is (= 1 1)))

;; (run-tests)
