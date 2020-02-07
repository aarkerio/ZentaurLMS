(ns zentaur.libs.models.shared
  (:require [clojure.string :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db])
   (:import (java.text Normalizer)))

(defn get-last-id
  "Get the last id for any table"
  [table]
  (db/clj-generic-last-id {:table-name table}))

(defn get-last-ordnen
  "Get last ordnen for a table with that column"
  [table id]
  (case table
    "answers"   (db/get-last-ordnen-answer {:question-id id})
    "questions" (db/get-last-ordnen-questions {:test-id id})))

(defn- ^:private normalize
  "Normalize string"
  [^CharSequence string]
  (let [normalized (Normalizer/normalize string java.text.Normalizer$Form/NFD)]
    (s/replace normalized #"\p{InCombiningDiacriticalMarks}+" "")))

(defn slugify
  "Returns a slugified string."
  [^CharSequence string]
  (let [normalized (normalize string)
        under_norm (s/replace normalized " " "_")]
    (s/lower-case under_norm)))

(defn str-to-int
  "Convert some keys in map to integer"
  [coll & int-keys]
  (let [listed (set int-keys)]
    (reduce-kv #(assoc %1 %2 (if (contains? listed %2) (Integer/parseInt %3) %3)) {} coll)))
