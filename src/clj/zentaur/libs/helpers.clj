(ns zentaur.libs.helpers
  (:require [clj-time.local :as l]
            [clj-time.format :as f]
            [ring.util.codec :as c]))

(def built-in-formatter (f/formatters :mysql))

(def custom-formatter (f/formatter "yyyyMMdd"))

(defn format-time
  ([] (format-time (l/local-now)))
  ([time]
     (f/unparse built-in-formatter time)))

(defn sanitize [string]
  (c/url-encode string))

(defmulti paginate
  "Paginate the incoming collection/length"
  (fn [coll? _ _] (sequential? coll?)))

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
