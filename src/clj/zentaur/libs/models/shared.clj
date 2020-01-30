(ns zentaur.libs.models.shared
  (:require [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn get-last-id [table]
  (db/clj-generic-last-id {:table-name table}))

;;;;; TEST BUILD SECTION STARTS

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question-id id})
        index-seq        (map #(keyword (str (% :id))) answers)
        mapped-answers   (zipmap index-seq answers)]
    (assoc question :answers mapped-answers)))

(defn- ^:private get-questions
  "Get and convert to map keyed"
  [test-id]
  (let [questions        (db/get-questions { :test-id test-id })
        questions-index  (map-indexed
                            (fn [idx question]
                              (assoc question :index (inc idx))) questions)
        index-seq        (map #(keyword (str (% :id))) questions-index)]
    (->> questions-index
         (map get-answers)
         (zipmap index-seq))))

(defn build-test-structure
  "Build the map with the test, the questions and the answers.
   Function used by the Web and the Phone App."
  [test-id user-id]
  (let [test          (db/get-one-test { :id test-id :user-id user-id })
        questions     (get-questions test-id)
        subjects      (db/get-subjects)]
    (try
      (assoc test :questions questions :subjects subjects)
      (catch Exception e (str "******** >>> Caught exception: " (.getMessage e)))
      (finally (assoc {} :error "function get-test-nodes in model error")))))

;;;;; TEST BUILD SECTION ENDS

(defmacro with-resources [[var expr & other :as resources]
                          body cleanup-block
                          [error-name error-block :as error-handler]]
  (if (empty? resources)
    `(try ~body
          (catch Throwable e#
            (let [~error-name e#] ~error-block))
          (finally ~cleanup-block))
    `(try
       (let ~[var expr]
         (with-resources ~other ~body ~cleanup-block ~error-handler))
       (catch Throwable e#
         (let ~(vec (interleave (take-nth 2 resources)
                                (repeat nil)))
           ~cleanup-block
           (let [~error-name e#] ~error-block))))))

