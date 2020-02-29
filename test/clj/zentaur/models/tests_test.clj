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
          params  {:title "Test title" :hint "Some hint" :tags "tags" :user_id user-id :subject_id "3"}
          test    (mt/create-test! params user-id)]
      (is (not (nil? (:id test)))))))

(deftest ^:business-logic create-question!
  (testing "Create a new "
    (let [tests   (mt/get-tests 1)
          uurlid  (:uurlid (first tests))
          params  {:question "Question" :explanation "Explanation" :active true :points 2 :hint "Some hint" :qtype 2 :user_id 1 :uurlid uurlid}
          question  (mt/create-question! params)]
      (is (not (nil? (:id question))))
      (is (= (:question question) "Question" )))))

(run-tests)
