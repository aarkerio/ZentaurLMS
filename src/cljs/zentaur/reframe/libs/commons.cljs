(ns zentaur.reframe.libs.commons
  (:require [cljs.spec.alpha :as s]))

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

(defn str-to-int
  "Change str keys in map to integer"
  [coll & int-keys]
  (let [listed (set int-keys)]
    (reduce-kv #(assoc %1 %2 (if (contains? listed %2 ) (js/parseInt %3) %3)) {} coll)))

(defn index-questions [questions]
  (let  [questions-index  (map-indexed
                           (fn [idx question]
                             (assoc question :index (inc idx))) questions)]
    questions-index))

(defn index-vector
  "Convert vector od maps to an indexed map"
  [rows]
  (into {} (map-indexed (fn [idx row]  { (js/parseInt (:id row)) row}) rows)))

(defn asterisks-to-spaces
  "Replace sub-strings surrounded by asterisks for spaces"
  [text]
  (clojure.string/replace text #"\*(.*?)\*" #(clojure.string/join (take (count (% 1)) (repeat "_")))))

(defn indexado [coll]
  (map-indexed (fn [idx itm]
                 (assoc {} :idx (inc idx) :question (val itm))) coll))

(defn sanitize [string]
  (clojure.string/escape string {\< "&lt;", \> "&gt;", \& "&amp;", \( "&#40;", \) "&#41;", \" "&quot;"}))

(defn vector-to-ordered-idxmap
  "Convert vector of maps to an indexed map, exposing the makes the re-frame CRUD easier"
  [rows]
  (let [indexed (reduce #(assoc %1 (keyword (str (:id %2))) %2) {} rows)]
    (into (sorted-map-by (fn [key1 key2]
                           (compare
                            (get-in indexed [key1 :ordnen])
                            (get-in indexed [key2 :ordnen]))))
          indexed)))

(defn order-map [rows]
  (if rows
    (into (sorted-map-by (fn [key1 key2]
                         (compare
                          (get-in rows [key2 :id])
                          (get-in rows [key1 :id]))))
          rows)
    {}))
