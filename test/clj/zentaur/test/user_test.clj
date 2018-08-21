(ns zentaur.test.user-test
  (:require [zentaur.models.users :as model-users]
            [clojure.test :refer [deftest testing is run-tests]]))

(def person {:fname "Pdsferro" :lname "Samuel" :uname "samuel" :prepassword "s0m3p4ss" :email "samuel@gmail.com" :preadmin "1" :role_id "1"})

(deftest create
    (testing "With valid input"
      (testing "it should returns an empty map"
        (is (= 1 (model-users/create person))))))

(deftest equality-test
  (testing "Is 'foo' equal 'fooer'"
    (is (= "foo" "foo"))))

(run-tests)

