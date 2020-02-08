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

(defn str-to-int [coll & int-keys]
  (let [listed (set int-keys)]
    (reduce-kv #(assoc %1 %2 (if (contains? listed %2 ) (js/parseInt %3) %3)) {} coll)))

(defn index-questions [questions]
(let  [questions-index  (map-indexed
                            (fn [idx question]
                              (assoc question :index (inc idx))) questions)]
  questions-index
       ))
