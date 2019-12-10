(ns zentaur.reframe.tests.libs
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(defn index-by-qid
  "Define index in a single Test data structure"
  [v id]
  (first (filter #(= (:qid (v %)) id) (range (count v)))))

(defn add-answer
  "Add new answer into single Test dstructure"
  [answer]
  (let [q-index (index-by-id (:questions data) (:question-id answer))]
    (update-in data [:questions q-index :full-question :answers]
           conj answer)))

(defn get-index [needle haystack]
  (keep-indexed #(when (= %2 needle) %1) haystack))
