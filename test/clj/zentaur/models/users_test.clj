(ns zentaur.models.users-test
  "Business logic in models test"
  (:require [clojure.test :as ct ]
            [factory-time.core :as ftime]
            [zentaur.models.users :as mu]))   ;; [deftest testing is run-tests]

(deftest ^:business-logic eg-tests (is (= 1 1)))

(ct/deftest create
  (ct/testing "With valid input"
    (ct/testing "it should returns an empty map"
      (ct/is (= 1 (mu/create (ftime/build :admin {:fname "Robert"})))))))

(ct/run-tests)

