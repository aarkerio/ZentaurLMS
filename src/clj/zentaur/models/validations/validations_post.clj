(ns zentaur.models.validations.validations-post
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [struct.core :as st]))
;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def post-schema
  [[:title st/required st/string
    {:title "title must contain at least 2 characters"
     :validate #(> (count %) 1)}]
   [:body st/required st/string
    {:body "the body must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn validate-post [params]
  (first
    (st/validate params post-schema)))

(def comment-schema
  [[:comment st/required st/string]
   [:post_id st/required st/integer]
   [:user_id st/required st/integer]])

(defn validate-comment [params]
  (first
    (st/validate params comment-schema)))

