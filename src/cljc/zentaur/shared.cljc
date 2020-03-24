(ns zentaur.shared
  (:require [struct.core :as st]))

(defn asterisks-to-spaces
  "Replace sub-strings surrounded by asterisks for spaces"
  [text]
  (clojure.string/replace text #"\*(.*?)\*" #(clojure.string/join (take (count (% 1)) (repeat "_")))))


