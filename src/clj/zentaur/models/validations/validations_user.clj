(ns zentaur.models.validations.validations-user
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [struct.core :as st]))

;;;;;;;;;;;;;;;;;;;;;;
;;    USER VALIDATIONS    NIL == all is fine!!
;;;;;;;;;;;;;;;;;;;;;

(def user-schema
  [[:fname   st/required st/string]
   [:lname   st/required st/string]
   [:uuid    st/required st/uuid]
   [:email   st/required st/string]
   [:admin   st/required st/boolean]
   [:active  st/required st/boolean]
   [:role_id st/required st/integer]
   [:password st/required
    st/string
    {:password "message must contain at least 6 characters"
     :validate #(> (count %) 6)}]])

(defn validate-user [params]
  (first
    (st/validate params user-schema)))
