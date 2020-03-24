(ns zentaur.libs.models.shared
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
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

(defn copy-file
  "Copy a file"
  [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn update-booleans
  "Change true/false string for booleans"
  [mymap keys-vector]
  (reduce #(assoc %1 %2  (if (= (%1 %2) "true") true false)) mymap keys-vector))

(defn- ^:private normalize
  "Normalize string (remove non-estandard characters)"
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

(defn gen-uuid
  "Generate a unique id for a record"
  []
  (let [uuid          (str (java.util.UUID/randomUUID))
        uuid-splitted (clojure.string/split uuid #"-")
        first-sec     (first uuid-splitted)
        last-sec      (last uuid-splitted)]
    (str first-sec last-sec)))

(defn checkboxboolean
  "Convert or add html form checkboxes values to booleans"
  [params & args]
  (let [new-values (reduce #(assoc %1 %2 (contains? params %2)) {} args)]
    (merge params new-values)))

(defn asterisks-to-spaces
  "Replace sub-strings surrounded by asterisks for spaces"
  [text]
  (clojure.string/replace text #"\*(.*?)\*" #(clojure.string/join (take (count (% 1)) (repeat "_")))))
