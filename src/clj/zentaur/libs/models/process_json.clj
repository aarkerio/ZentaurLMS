(ns zentaur.libs.models.process-json
  (:require [cheshire.core :as ches]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [ring.util.codec :as c]
            [zentaur.db.core :as db]
            [zentaur.libs.models.shared :as shar]))

(def built-in-formatter (f/formatters :mysql))

(defn- insert-columns []
  )

(defn- build-test [{:keys [title description instructions level lang tags origin] :or {level "1"}} user-id]
   (let [int-level (Integer/parseInt level)]
     {:title title :description  description  :instructions instructions :level int-level :lang lang :tags tags :origin origin :user-id user-id}))

(defn- build-questions [questions user-id]
  (map #(-> {:question (get % :question) :qtype (Integer/parseInt (get % :qtype)) :hint (get % :hint)
             :answers (get % :answers) :explanation (get % :explanation) :user-id user-id :active true } ) questions))

(defn- process-json [body-map]
  (log/info (str ">>> body-map >>>>> " body-map))
  (let [questions (:questions body-map)]
    (+ 5 6)))

(defn- insert-answers [answers question-id]
  (log/info (str ">>> 888888 INSERT ANSWERSSS > >>>>> " answers))
  (doseq [answer answers]
    (log/info (str ">>> 999999 INSERT ANSWER > >>>>> " answer))
    (db/create-answer! (assoc answer {:question-id question-id}))))

(defn insert-questions [questions {id :id}]
  (doseq [question questions]
         (log/info (str ">>>  555555 pre-save > >>>>> " question))
         (let [_             (db/create-question! question)
               last-question (shar/get-last-id "questions")
               _             (log/info  (str ">>> question  last-question-id >>>>> " (pr-str last-question)))
               question-id   (:id last-question)
               answers (:answers question)]
           (log/info (str ">>> ###  6666666   question-id  QQQQQ > >>>>> " question-id))
           (db/create-question-test! {:question-id question-id :test-id id})
           (log/info (str ">>> ### 77777 ANSWERS >>>>> " answers))
           (insert-answers answers question-id))))

(defn- insert-test [test]
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
          _         (log/info (str ">>> 1111 QUESTIONS >>>>>>>>>>>>> " (:questions body-map)))
          questions (build-questions (:questions body-map) user-id)
          _         (log/info (str ">>> 22222   questions >>>>> " questions))]
      (insert-test test-map)
      (insert-questions questions (shar/get-last-id "tests")))))
