(ns zentaur.libs.models.process-json
  (:require [cheshire.core :as ches]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [ring.util.codec :as c]
            [zentaur.db.core :as db]
            [zentaur.libs.models.shared :as shar]))

(def built-in-formatter (f/formatters :mysql))

(defn- build-test [{:keys [title description instructions level lang tags origin] :or {level "1"}} user-id]
   (let [int-level (Integer/parseInt level)]
     {:title title :description  description  :instructions instructions :level int-level :lang lang :tags tags :origin origin :user-id user-id}))

(defn- build-questions [questions user-id]
  (map #( {:question (get % :question) :qtype (get % :qtype) :hint (get % :hint) :answer (get % :answer) :user-id user-id } questions)))

(defn- process-json [body-map]
  (log/info (str ">>> body-map >>>>> " body-map))
  (let [questions (:questions body-map)]
    (+ 5 6)))

(defn- insert-answers [answers question-id]
  (log/info (str ">>> ANSWERSSS > >>>>> " answers))
  (map (fn [answer]
         (db/create-answer! (assoc answer {:question-id question-id}))
         ) answers))

(defn- insert-questions [questions {id :id}]
  (log/info (str ">>> QUESTIONNNNNNN> >>>>> " questions))
  (map (fn [q]
         (log/info (str ">>> 1111111  QQQQQ > >>>>> " q))
           (let [question-id (db/create-question! q)]
             (db/create-question-test! {:question-id question-id :test-id id})
             (insert-answers (:answers q) question-id))
           ) questions))

(defn- insert-test 0[test]
  (try
    (db/create-test! test)
    (throw
       (ex-info "The test has melted!"
        {:causes "Test problem"}))
    (catch Exception e (ex-data e))))

(defn export-json
  ([] (export-json "body" 1))
  ([body user-id]
    (let [upload    (db/get-upload {:id 9})
          body-map  (ches/parse-string (:json upload) true)
          test-map  (build-test body-map user-id)
          _         (log/info (str ">>> test-map >>>>> " test-map))
          questions (build-questions (:questions body-map) user-id)
          _         (log/info (str ">>> questions >>>>> " questions))]
      (insert-test test-map)
      (insert-questions questions (shar/get-last-id "tests")))))
