(ns zentaur.models.tests-test
  "Tests for Business logic in the Test model"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [talltale.core :as tac]
            [zentaur.models.users :as mu]
            [zentaur.models.tests :as mt]))

(use-fixtures
  :once
  (fn [f]
     (require '[mount.core :as mount])
    (mount/start #'zentaur.config/env
                 #'zentaur.handler/app-routes
                 #'zentaur.db.core/*db*)
     (require '[zentaur.db.core :as db])
    (f)))

(def first-user (atom nil))
(def first-test (atom nil))
(def first-question (atom nil))

(deftest ^:business-logic create-user
  (testing "Creates a new user"
    (let [person (tac/person)
          params  {:fname (:first-name person) :lname (:last-name person) :email (:email person) :prepassword "iam4h4ck3r" :role_id "1"}
          user    (mu/create params)]
      (reset! first-user user)
      (is (not (nil? (:uuid @first-user)))))))

(deftest ^:business-logic create-test
  (testing "Creates a new test"
    (let [user-id (:id @first-user)
          params  {:title "Test title" :hint "Some hint" :tags "tags" :user_id user-id :subject_id 3 :level_id 1 :lang_id 1}
          test    (mt/create-test! params user-id)]
      (reset! first-test test)
      (is (not (nil? (:id @first-test)))))))

(deftest ^:business-logic create-question
  (testing "Creates a new question"
    (let [test     (mt/get-tests (:id @first-test))
          uurlid   (:uurlid @first-test)
          params   {:question "Some cool Question" :explanation "Explanation" :active true :points 2 :hint "Some hint" :qtype 2 :user_id 1 :uurlid uurlid :subject_id 3 :level_id 1 :lang_id 1}
          question (mt/create-question! params)]
      (reset! first-question question)
      (log/info (str ">>> @first-question >>>>> " @first-question))
      (is (not (nil? (:id @first-question))))
      (is (= (:ordnen @first-question) 1)) ))

  (testing "It updates a question"
    (let [question-id (:id @first-question)
          params      {:id question-id :question "Some question edited" :hint "hint" :explanation "" :qtype 1 :points 3}
          upquestion  (mt/update-question! params)]
      (is (= (:question upquestion) "Some question edited")  "Updating was not correct")
      (is (not (nil? (:id upquestion))))))

  (testing "It creates a new answer"
    (let [question-id (:id @first-question)
          params      {:question_id question-id :answer "Some not so cool answer" :correct true}
          answer      (mt/create-answer! params)]
      (is (not (nil? (:ordnen answer))))
      (is (= (:answer answer) "Some not so cool answer")))))

(deftest ^:business-logic create-test-after-questions-selected-and-saved
  (testing "It creates a new test"
    (let [test    (mt/build-test (:user_id @first-test))
          uurlid  (:uurlid @first-test)
          params  {:question "Some cool Question" :explanation "Explanation" :active true :points 2 :hint "Some hint" :qtype 2 :user_id 1 :uurlid uurlid :subject_id 3 :level_id 1 :lang_id 1}
          question  (mt/create-question! params)]
      (reset! first-question question)
      (log/info (str ">>> @first-question >>>>> " @first-question))
      (is (not (nil? (:id @first-question))))
      (is (= (:ordnen @first-question) 1)) ))

(run-tests)
