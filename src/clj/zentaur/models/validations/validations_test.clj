(ns zentaur.models.validations.validations-test
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [struct.core :as st]))

(s/def :test/title string?)
(s/def :test/tags string?)
(s/def :test/subject-id (s/and int? pos?))
(s/def :test/user-id (s/and int? pos?))

(s/def :test/new-test (s/keys :req [:test/title :test/subject-id :test/user-id]
                              :opt [:test/tags]))

(defn spec-minimal-test [params]
  (s/valid? :test/new-test params))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS    NIL == all is fine!!
;;;;;;;;;;;;;;;;;;;;;

(def test-schema
  [[:user-id st/required st/integer]
   [:subject-id st/required st/integer]
   [:title   st/required st/string
     {:title "Title field must contain at least 2 characters"
      :validate #(> (count %) 2)}]])

(defn validate-test [params]
  (first
    (st/validate params test-schema)))

(def on-create
  {:message "Necessary only when create"
   :optional true
   :state true ;; If the validator needs access to previously validated data
   :validate (fn [state v ref]
               (let [prev (get state ref)]
                 (= prev v)))})

(def question-schema
  [[:user-id  st/required st/integer]
   [:qtype    st/required st/integer]
   [:active   st/required st/boolean]
   [:question st/required st/string
    {:title "Question field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-question [params]
  (first
    (st/validate params question-schema)))

(def answer-schema
  [[:question-id st/required st/integer]
   [:ordnen      st/required st/integer]
   [:answer      st/required st/string
    {:title "Answer field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-answer [params]
  (first
    (st/validate params answer-schema)))
