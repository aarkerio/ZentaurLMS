(ns blog.libs.helpers
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

