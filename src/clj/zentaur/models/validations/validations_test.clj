(ns zentaur.models.validations.validations-test
  (:require [clj-time.local :as l]
            [clojure.tools.logging :as log]
            [struct.core :as st]))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS    NIL == all is fine!!
;;;;;;;;;;;;;;;;;;;;;

(def test-schema
  [[:user-id st/required st/integer]
   [:title
    st/required
    st/string
    {:title "Title field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-test [params]
  (first
    (st/validate params test-schema)))

(def question-schema
  [[:user-id st/required st/integer]
   [:qtype   st/required st/integer]
   [:active  st/required st/boolean]
   [:question
    st/required
    st/string
    {:title "Question field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-question [params]
  (first
    (st/validate params question-schema)))

(def answer-schema
  [[:question-id st/required st/integer]
   [:ordnen st/required st/integer]
   [:answer
    st/required
    st/string
    {:title "Answer field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-answer [params]
  (first
    (st/validate params answer-schema)))
