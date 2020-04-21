(ns zentaur.test-runner
  (:require [cljs.test]
            [cljs-test-display.core]
            [zentaur.core-test])
  (:require-macros
   [cljs.test]))

(defn test-run []
  ;; where "app" is the HTML node where you want to mount the tests
  (cljs.test/run-tests
    (cljs-test-display.core/init! "app") ;;<-- initialize cljs-test-display here
    ;; 'zentaur.post-test
    'zentaur.core-test))
