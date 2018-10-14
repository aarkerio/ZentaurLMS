(ns zentaur.test.user-test
  (:require [zentaur.models.users :as model-users]
            [zentaur.config :as config]
            [clojure.test :as ct ]))   ;; [deftest testing is run-tests]

(use-fixtures :once config/wrap-setup) ;; wrap-setup around the whole namespace of tests.
                                       ;; use :each to wrap around each individual test
                                       ;; in this package.

(use-fixtures :once config/once-fixture)
(use-fixtures :each config/each-fixture)

(def person {:fname "Pedro" :lname "Samuel" :uname "77samdddl" :prepassword "s0m3p4ss" :email "77samu@gmail.com" :preadmin "1" :role_id "1"})

(ct/deftest create
  (ct/testing "With valid input"
    (ct/testing "it should returns an empty map"
      (ct/is (= 1 (model-users/create person))))))

(ct/deftest equality-test
  (ct/testing "Is 'foo' equal 'fooer'"
    (ct/is (= "foo" "foo"))))

;; (deftest ^:test-refresh/focus test-addition
;;  (is (= 2 (+ 1 1))))

(def db "postgresql://localhost:5432/robb1e_test")

(defn clean-database [f]
  (post/delete-all db)
  (f))

(use-fixtures :each clean-database)


(ct/run-tests)

