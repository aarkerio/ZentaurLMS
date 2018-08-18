(ns zentaur.user-test
  (:require [zentaur.models.users :as model-users]
            [clojure.test :refer [deftest testing is run-tests]]))

(def person {:fname "Perro" :lname "Aguayo", :uname "perrin" :prepassword "s0m3p4ss" :email "perrog@gmail.com" :preadmin "1" :role_id "1"})

(deftest create
    (testing "With valid input"
      (testing "it should returns an empty map"
        (is (= {} (model-users/create person))))))

(deftest equality-test
  (testing "Is 'foo' equal 'fooer'"
    (is (= "foo" "foo"))))

(run-tests)

