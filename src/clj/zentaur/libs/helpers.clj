(ns zentaur.libs.helpers
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [java-time :as time]
            [ring.util.codec :as c]))

(def msg-erfolg "Veränderung wurden erfolgreich gespeichert")
(def msg-fehler "Etwas ging schief")

(defn sanitize [string]
  (c/url-encode string))

(defmulti paginate
  "Paginate the incoming collection/length"
  (fn [coll? _ _] (sequential? coll?)))

(defn copy-file
  "Copy a file"
  [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn update-booleans
  "Change true/false string for booleans"
  [mymap keys-vector]
  (reduce #(assoc %1 %2  (if (= (%1 %2) "true") true false)) mymap keys-vector))

(defn map-to-query-string
  "Convert a map to a string"
  [m]
  (string/join " " (map (fn [[k v]] (str (name k) " " v)) m)))

(defmethod paginate true [coll count-per-page page]
  (paginate (count coll) count-per-page page))

(defmethod paginate :default [length count-per-page page]
  (let [pages (+ (int (/ length count-per-page))
                 (if (zero? (mod length count-per-page))
                   0
                   1))
        page (if (and (string? page)(not= page ""))
               (Integer/parseInt page))
        page (cond
               (nil? page) 1 (or (neg? page) (zero? page)) 1
               (> page pages) pages
               :else page)
        next (+ page 1)
        prev (- page 1)]
    (let [prev (if (or (neg? prev) (zero? prev)) nil prev)]
      {:pages pages
       :page page
       :next-seq (range (inc page) (inc pages))
       :prev-seq (reverse (range 1 (if (nil? prev) 1
                                       (inc prev))))
       :next (if (> next pages) nil next)
       :prev prev})))

;; USE :

;; (= (paginate (range 101) 10 5)
;;    {:prev-seq (4 3 2 1), :next-seq (6 7 8 9 10 11), :pages 11, :page 5, :next 6, :prev 4}
