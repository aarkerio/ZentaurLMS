(ns zentaur.models.tests
  (:require [zentaur.db.core :as db]
            [zentaur.env :as env]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [slugify.core :refer [slugify]]
            [clj-time.local :as l]))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def test-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn validate-test [params]
  (first
    (st/validate params test-schema)))

(def comment-schema
  [[:comment st/required st/string]
   [:test_id st/required st/integer]])

(defn validate-comment [params]
  (first
    (st/validate params comment-schema)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;          ACTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-tests [user-id]
  (db/get-tests { :user-id user-id }))

(defn get-one-test [user-id id]
  (db/get-one-test {:user-id user-id :id id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn save-test! [params]
  (if-let [errors (validate-test params)]
      (db/create-test! params)))

(defn destroy [params]
  (do
    (db/delete-test! params)))


;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;
(defn admin-get-tests [user-id]
    (db/admin-get-tests))

