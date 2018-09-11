(ns zentaur.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [zentaur.core :as rc]))

(deftest test-home
  (is (= true true)))

