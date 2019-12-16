(ns zentaur.config-test
  (:require [conman.core :as conman]
            [zentaur.db.core :as db]))

(declare ^:dynamic *txn*) ;; transaction

(conman/with-transaction [db]
  (jdbc/db-set-rollback-only! db)
  (create-user!
    {:id         "foo"
     :first_name "Sam"
     :last_name  "Smith"
     :email      "sam.smith@example.com"})
  (get-user {:id "foo"}))

(deftest transaction
  (conman/with-transaction
    [db]
    (sql/db-set-rollback-only! db)
    (is
      (= 1
         (add-fruit!
           {:name       "apple"
            :appearance "red"
            :cost       1
            :grade      1})))

(use-fixtures
  :once
  (fn [f]
    (println "one time setup")
    (m/in-clj-mode)
    (m/start #'conn)
    (delete-test-db)
    (create-test-table)
    (f)
    (println "teardown")))

(defn delete-test-db []
  (db/delete-all-tables!))

(defn one-time-setup []
  (println "one time setup"))

(defn one-time-teardown []
  (println "one time teardown"))

(defn once-fixture [f]
  (one-time-setup)
  (f)
  (one-time-teardown))

(defn setup-test []
  (println "setup"))

(defn teardown-test []
  (println "teardown"))

(defn wrap-setup
  [f]
  (println "wrapping setup")
  ;; note that you generally want to run teardown-tests in a try ...
  ;; finally construct, but this is just an example
  (setup-test)
  (f)
  (teardown-test))

(defn setup []
  (println "Setup before each"))

(defn teardown []
  (println "Teardown after each"))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))

(defn once-fixture [f]
  (setup)
  (f)
  (teardown))

