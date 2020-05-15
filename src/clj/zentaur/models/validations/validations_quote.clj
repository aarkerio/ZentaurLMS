(ns zentaur.models.validations.validations-quote
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [struct.core :as st]))
;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def quote-schema
  [[:author st/required st/string
    {:author "Author must contain at least 4 characters"
     :validate #(> (count %) 3)}]
   [:quote st/required st/string
    {:quote "the quote must contain at least 6 characters"
     :validate #(> (count %) 5)}]])

(defn validate-quote [params]
  (first
    (st/validate params quote-schema)))

