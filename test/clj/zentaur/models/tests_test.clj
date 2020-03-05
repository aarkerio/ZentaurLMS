(ns zentaur.models.tests-test
  "Business logic in models test"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [talltale.core :as tac]
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
    (let [person (tac/person)
          params  {:fname (:first-name person) :lname (:last-name person) :email (:email person) :prepassword "iam4h4ck3r" :role_id "1"}
          user    (mu/create params)]
      (reset! first-user user)
      (log/info (str ">>> UUSER >>  ********** >>>>> " @first-user))
      (is (not (nil? (:id @first-user)))))))

(deftest ^:business-logic create-test
  (testing "Create a new test"
    (let [user-id (:id @first-user)
          params  {:title "Test title" :hint "Some hint" :tags "tags" :user_id user-id :subject_id "3"}
          test    (mt/create-test! params user-id)]
      (reset! first-test test)
      (is (not (nil? (:id @first-test)))))))

(deftest ^:business-logic create-question
  (testing "Create a new question"
    (let [test   (mt/get-tests (:id @first-test))
          uurlid  (:uurlid @first-test)
          params  {:question "Some cool Question" :explanation "Explanation" :active true :points 2 :hint "Some hint" :qtype 2 :user_id 1 :uurlid uurlid}
          question  (mt/create-question! params)]
      (reset! first-question question)
      (is (not (nil? (:id @first-question))))
      (is (= (:question @first-question) "Some cool Question")))))

(deftest ^:business-logic update-question
  (testing "Update a question"
    (let [question-id (:id @first-question)
          params      {:id question-id :question "Some question edited" :hint "hint" :explanation "" :qtype 1 :points 3}
          upquestion    (mt/update-question! params)]
      (is (not (nil? (:id upquestion)))))))

(deftest ^:business-logic create-answer
  (testing "Create a new answer"
    (let [question-id (:id @first-question)
          params      {:question_id question-id :answer "Some not so cool answer" :correct true}
          answer      (mt/create-answer! params)]
      (is (not (nil? (:id answer))))
      (is (= (:answer answer) "Some not so cool answer")))))

(run-tests)

