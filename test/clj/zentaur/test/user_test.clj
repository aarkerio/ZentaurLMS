(ns zentaur.test.user-test
  (:require [clojure.test :as ct ]
            [factory-time.core :as ftime]
            [zentaur.db.core :as db]
            [zentaur.config-test :as config]
            [zentaur.models.users :as model-users]))   ;; [deftest testing is run-tests]

;;(ct/use-fixtures :once config/wrap-setup) ;; wrap-setup around the whole namespace of tests.
                                          ;; use :each to wrap around each individual test
                                          ;; in this package.

;; (ct/use-fixtures :once config/once-fixture)
;; (ct/use-fixtures :each config/each-fixture)


(ct/deftest create
  (ct/testing "With valid input"
    (ct/testing "it should returns an empty map"
      (ct/is (= 1 (model-users/create (ftime/build :admin {:fname "Robert"})))))))

;; (ct/deftest equality-test
;;  (ct/testing "Is 'foo' equal 'fooer'"
;;    (ct/is (= "foo" "foo"))))

;; (deftest ^:test-refresh/focus test-addition
;;  (is (= 2 (+ 1 1))))

;; (ct/use-fixtures :each clean-database)

(ct/run-tests)

