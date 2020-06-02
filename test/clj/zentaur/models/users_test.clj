(ns zentaur.models.users-test
  "Business logic in models test"
  (:require [clojure.test :as ct]
            [zentaur.factories.user-factory :as ufac]
            [zentaur.db.core :as db]
            [zentaur.models.users :as mu]))

;; (deftest ^:business-logic eg-tests (is (= 1 1)))

;; (defn destroy [id]
;;   (let [int-id (Integer/parseInt id)]
;;     (db/delete-post! {:id int-id})))

;; (ct/deftest create
;;   (ct/testing "With valid input"
;;     (ct/testing "it should returns an empty map"
;;       (ct/is (= 1 (mu/create (ftime/build :admin {:fname "Robert"})))))))

;; (ct/ run-tests)

