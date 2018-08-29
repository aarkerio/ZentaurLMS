(ns zentaur.libs.models.process-json
  (:require [cheshire.core :as ches]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [ring.util.codec :as c]
            [zentaur.db.core :as db]))

(def built-in-formatter (f/formatters :mysql))

(defn- build-test [{:keys [title description instructions level lang tags origin]} user-id]
  {:title title :description  description  :instructions instructions :level level :lang lang :tags tags :origin origin :user-id user-id})

(defn- build-questions [questions user-id]
  (->> ["photoset" "photo"]
       (get-in questions)
       (map #( {:question (get % :question) :qtype (get % :qtype) :hint (get % :hint) :answer (get % :answer) :user-id user-id } ))))

(defn- process-json [body-map]
  (log/info (str ">>> body-map >>>>> " body-map))
  (let [questions (:questions body-map)]
    (+ 5 6)))

(defn- insert-questions [questions test-id]
  (try
    (db/create-test! test)
    (throw
       (ex-info "The ice cream has melted!"
       {:causes             #{:fridge-door-open :dangerously-high-temperature}
        :current-temperature {:value 25 :unit :celsius}}))
    (catch Exception e (ex-data e))))

(defn- insert-test [test]
  (try
    (db/create-test! test)
    (throw
      (ex-info "Test has melted!"
        {:causes             #{:fridge-door-open :dangerously-high-temperature}}))
    (catch Exception e (ex-data e))))

(defn export-json
  ([] (export-json "body" 1))
  ([body user-id]
    (let [upload    (db/get-upload "9")
          body-map  (ches/parse-string (:json upload) true)
          test-map  (build-test body-map user-id)
          _         (log/info (str ">>> test-map >>>>> " test-map))
          questions (build-questions (:questions body-map) user-id)]
    (-> test-map
        (insert-test)))))
