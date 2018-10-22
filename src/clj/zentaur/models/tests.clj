(ns ^:test-model zentaur.models.tests
  (:require [cheshire.core :as ches]
            [clj-time.local :as l]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as helpers]
            [zentaur.models.validations.validations-test :as val-test]))

(defn get-tests [user-id]
  (db/get-tests { :user-id user-id }))

(defn get-one-test [user-id id]
  (db/get-one-test {:user-id user-id :id id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)
        errors      (-> full-params (val-test/validate-test))]
    (if (= errors nil)
      (db/create-minimal-test! full-params)
      {:flash errors})))

(defn- ^:private get-last-ordnen
  [table id]
  (case table
    "answers"   (db/get-last-ordnen-answer {:question-id id})
    "questions" (db/get-last-ordnen-questions {:test-id id})))

(defn- ^:private link-test-question!
  [last-question test-id]
  (let [question-id (get-in (first last-question) [:id])
        next-ordnen (or (:ordnen (get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question-id question-id :test-id test-id :ordnen (inc next-ordnen)})))

(defn create-question! [params]
  (let [full-params (-> params
                        (update :qtype   #(Integer/parseInt %))
                        (update :test-id #(Integer/parseInt %)))
        errors      (val-test/validate-question full-params)]
    (log/info (str ">>> FULLL   PARAM >>>>> " full-params))
    (if (= errors nil)
      (as-> full-params v
        (db/create-question! v)
        (link-test-question! v (:test-id full-params))
        (db/get-last-question {:test-id (:test-id full-params)}))
      {:flash errors :ok false})))

(defn update-question! [params]
  (log/info (str ">>> UPDATE QUESTION PARAM >>>>> " params))
  (let [full-params (dissoc params :active)]
    (db/update-question! full-params)
    (db/get-question {:id (:id params)})))

(defn- ^:private key-answer
  [answer]
  (assoc answer :key (str "keyed-" (:id answer))))

(defn create-answer! [params]
  (let [question-id  (:question-id params)
        next-ordnen  (or (:ordnen (get-last-ordnen "answers" question-id)) 0)
        full-params  (assoc params :ordnen (inc next-ordnen))
        errors       (val-test/validate-answer full-params)]
    (if (= errors nil)
      (as-> full-params v
        (db/create-answer! v)
        (db/get-last-answer {:question-id question-id})
        (key-answer v))
      {:flash errors :ok false})))

(defn- ^:private get-answers [question]
  (let [answers          (db/get-answers {:question-id (:id question)})
        keys-answers     (map #(assoc % :key (str "keyed-" (:id %))) answers)
        question-updated (update question :created_at #(helpers/format-time %))]
    (assoc question-updated :answers keys-answers)))

(defn- ^:private get-questions
  "get and convert to map keyed"
  [test-id]
  (let [questions  (db/get-questions { :test-id test-id })
        index-seq  (map #(% :id) questions)]
        (->> questions
             (map get-answers)
             (zipmap index-seq)  ;; add the index
             )))

(defn get-test-nodes [test-id user-id]
  (let [test         (db/get-one-test { :id test-id :user-id user-id })
        test-updated (update test :created_at #(helpers/format-time %))
        questions    (get-questions test-id)]
     (ches/generate-string (assoc test-updated :questions questions))))

(defn destroy [params]
  (db/delete-test! params))

(defn admin-get-tests [user-id]
  (db/admin-get-tests))

(defn remove-question [params]
  (log/info (str ">>>  remove-question >>>>> " params))
  (let [test-id     (Integer/parseInt (:test-id params))
        question-id (:question-id params)]
    (db/remove-question! {:test-id test-id :question-id question-id})))

