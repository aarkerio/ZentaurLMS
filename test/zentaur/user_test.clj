(ns zentaur.upload-test
  (:require [zentaur.models.uploads :as uploads]
            [factory-time.core :refer [build create!]]
            [clojure.test :refer [deftest is run-tests]])))

(def upload (create! :upload {:done false}))

(deftest download
  (testing "With valid input"
    (testing "it should return a header map with filename included"
      (is (= first_map (unit/download {:id 1}))))))

(deftest download
  (with-redefs [upload-factory/load-upload-db (constantly {:filename "cool.pdf"})]
    (testing "With valid input"
      (testing "it should return a header map with filename included"
        (is (= first_map (uploads/download {:id 1})))))))

(ns http)

(defn post [url]
  {:body "Hello world"})

(ns app
  (:require [clojure.test :refer [deftest is run-tests]]))

(deftest is-a-macro
  (with-redefs [http/post (fn [url] {:body "Goodbye world"})]
    (is (= {:body "Goodbye world"} (http/post "http://service.com/greet")))))


(run-tests)
