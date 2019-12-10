(ns zentaur.reframe.tests.libs
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(defn index-by-id
  "Define index in a single Test data structure"
  [v id]
  (first (filter #(= (:id (v %)) id) (range (count v)))))

(defn add-answer
  "Add new answer into single Test dstructure"
  [answer]
  (let [q-index (index-by-id (:questions data) (:question-id answer))]
    (update-in data [:questions q-index :full-question :answers]
           conj answer)))
