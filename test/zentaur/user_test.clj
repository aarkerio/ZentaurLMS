(ns zentaur.user-test
  (:require [zentaur.models.users :as model-users]
            [clojure.test :refer [deftest testing is run-tests]]))

(def person {:fname "Perro" :lname "Aguayo", :uname "perrin" :password "s0m3p4ss" :email "perrog@gmail.com" :admin true})

(deftest create
    (testing "With valid input"
      (testing "it should return a header map with filename included"
        (is (= {} ([model-users/create person]))))))

(deftest equality-test
  (testing "Is 'foo' equal 'fooer'"
    (is (= "foo" "fooer"))))

(run-tests)

