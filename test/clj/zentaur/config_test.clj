(ns zentaur.config-test
  (:require [clojure.tools.logging :as log]
            [conman.core :as conman]
            [cprop.source :as source]
            [zentaur.db.core :as db]))

(declare ^:dynamic *txn*) ;; transaction

(defn foo []
  (log/info (str "from-system-props: >> " (:lein-profile (source/from-system-props))   " >>>>>>" (System/getenv "lein.profile")   )))

;; (conman/with-transaction [db]
;;   (jdbc/db-set-rollback-only! db)
;;   (create-user!
;;     {:id         "foo"
;;      :first_name "Sam"
;;      :last_name  "Smith"
;;      :email      "sam.smith@example.com"})
;;   (get-user {:id "foo"}))

;; (deftest transaction
;;   (conman/with-transaction
;;     [db]
;;     (sql/db-set-rollback-only! db)
;;     (is
;;       (= 1
;;          (add-fruit!
;;            {:name       "apple"
;;             :appearance "red"
;;             :cost       1
;;             :grade      1})))

;; (use-fixtures
;;   :once
;;   (fn [f]
;;     (println "one time setup")
;;     (m/in-clj-mode)
;;     (m/start #'conn)
;;     (delete-test-db)
;;     (create-test-table)
;;     (f)
;;     (println "teardown in test")))

;; (defn delete-test-db []
;;   (let [profile (:lein-profile (source/from-system-props))]
;;   (not= profile "test" (db/delete-all-tables!))))

