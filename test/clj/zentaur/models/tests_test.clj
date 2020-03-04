(ns zentaur.models.tests-test
  "Business logic in models test"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [zentaur.factories.test-factory :as tf]
            [zentaur.models.users :as mu]
            [zentaur.models.tests :as mt]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'zentaur.config/env
                 #'zentaur.handler/app-routes
                 #'zentaur.db.core/*db*)
    (f)))

(def first-user (atom nil))
(def first-test (atom nil))
(def first-question (atom nil))

(deftest ^:business-logic create-user
  (testing "Create a new user"
    (let [params  {:fname "Hugo" :lname "Sánchez" :email "77samu@gmail.com" :prepassword "iam4h4ck3r" :role_id "1"}
          user    (mu/create params)]
      (reset! first-user user)
      (is (not (nil? (:id @first-test)))))))


(deftest ^:business-logic create-test
  (testing "Create a new test"
    (let [user-id 1
          params  {:title "Test title" :hint "Some hint" :tags "tags" :user_id user-id :subject_id "3"}
          test    (mt/create-test! params user-id)]
      (reset! first-test test)
      (is (not (nil? (:id @first-test)))))))

(deftest ^:business-logic create-question!
  (testing "Create a new "
    (let [test   (mt/get-tests 1)
          _      (log/info (str ">>> @first-test >>>>> " @first-test))
          uurlid  (:uurlid (first test))
          params  {:question "Some cool Question" :explanation "Explanation" :active true :points 2 :hint "Some hint" :qtype 2 :user_id 1 :uurlid uurlid}
          question  (mt/create-question! params)]
      (reset! first-question question)
      (is (not (nil? (:id @first-question))))
      (is (= (:question @first-question) "Some cool Question")))))

(deftest ^:business-logic update-question
  (testing "Update question"
    (let [question-id (:id @first-question)
          params      {:id 1 :question "Some question edited" :hint "hint" :explanation "" :qtype 1 :points 3}
          question    (mt/update-question! params)]
      (is (not (nil? (:id question)))))))

(run-tests)

