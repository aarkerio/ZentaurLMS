(ns zentaur.febe.shared
  (:require [struct.core :as st]))

(defn asterisks-to-spaces
  "Replace sub-strings surrounded by asterisks for spaces"
  [text]
  (clojure.string/replace text #"\*(.*?)\*" #(clojure.string/join (take (count (% 1)) (repeat "_")))))

;; (Desparrama) Expand keywords and values, short notation as in ES6
;; macro wrapped in a :clj reader conditional to avoid some weird behavior of the CLJS compiler
;; where it would define it as a function when compiling
#?(:clj
   (defmacro desp [& xs]
     (cons 'hash-map
           (interleave (map (comp keyword name) xs) xs))))
