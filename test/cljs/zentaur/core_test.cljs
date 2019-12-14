(ns zentaur.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [zentaur.core :as rc]
            [zentaur.reframe.tests.libs :as lib]))

(enable-console-print!)

(use-fixtures :once
  {:before (fn [] (into [] '(1 2)))
   :after  (fn [] ...)})

(deftest test-home
  (is (= true true)))


(run-tests)

;; (run-tests (cljs-test-display.core/init! "app-testing")
;;            'example.core-test
;;            'example.other-test)
