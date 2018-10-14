(ns zentaur.test.user-test
  (:require [zentaur.models.users :as model-users]
            [zentaur.config :as config]
            [clojure.test :as ct ]))   ;; [deftest testing is run-tests]

(def db "postgresql://localhost:5432/robb1e_test")

(defn clean-database [f]
  (db/delete-all-posts db)
  (f))

(use-fixtures :each clean-database)

(deftest can-insert-new-post
  (testing "Inserting new records, and retriving those"
    (is (= 0 (count (post/all db))))
    (post/insert db {:excerpt "excerpt" :publication "publication" :uri "uri" :title "Hello, world" :published_at (to-sql-date (now))})
    (is (= 1 (count (post/all db))))))

(deftest can-get-posts
  (testing "Retriving existing records"
    (post/insert db {:excerpt "excerpt" :publication "publication" :uri "uri" :title "Hello, world" :published_at (to-sql-date (now))})
    (is (= "Hello, world" ((first (post/all db)) :title)))))
