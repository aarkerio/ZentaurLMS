(ns zentaur.reframe.tests.libs
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(defn index-by-qid
  "Define index in a single Test data structure"
  [v id]
  (first (filter #(= (:qid (v %)) id) (range (count v)))))

(defn get-index [needle haystack]
  (keep-indexed #(when (= %2 needle) %1) haystack))

(defn vec-remove-by-id
  "Remove element in coll"
  [coll id]
  (let [pos (index-by-qid coll id)]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos))))))
