(ns zentaur.reframe.tests.tests_events
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [zentaur.reframe.libs.commons :as cms]
            [zentaur.reframe.libs.db :as zdb]))

;; -- Check Spec Interceptor  ----------
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (rf/after (partial check-and-throw :zentaur.reframe.tests.db/db)))  ;; PARTIAL: a way to currying

;; We now create the interceptor chain shared by all event handlers which manipulate todos.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def test-interceptors [check-spec-interceptor])

(rf/reg-event-fx
 :initialise-db
 (fn [{:keys [db]} _]
   {:db zdb/default-db}))

(rf/reg-event-db
 :bad-response
 (fn
   [db [_ response]]
   (.log js/console (str ">>> Fheler aus ajax antwort : >>>>> " response "   " _))))

(rf/reg-event-db
 :toggle-qform     ;; versteckt / zeigt neues Frageformular
 (fn [db _]
   (update db :qform not)))

(rf/reg-event-db
 :toggle-testform     ;; versteckt / zeigt bearbeit testformular
 (fn [db _]
   (update db :testform not)))

(rf/reg-event-db
 :test-load-process
  []
  (fn [db [{:keys [data errors]}]]
    (let [test          (:test_by_uurlid data)
          questions     (:questions test)
          subjects      (:subjects test)
          levels        (:levels test)
          only-test     (dissoc test :subjects :levels :questions)
          _             (.log js/console (str ">>> LEVELS >>>>> " levels))
          _             (.log js/console (str ">>> questions >>>>> " questions))
          _             (.log js/console (str ">>> TEST >>>>> " only-test))
          ]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI element
         (assoc :test      only-test)
         (assoc :subjects  subjects)
         (assoc :levels    levels)
         (assoc :questions questions)))))

(rf/reg-event-fx
  :test-load
  (fn [cfx _]
    (let [uurlid  (.-value (gdom/getElement "uurlid"))
          query   (gstring/format "{test_by_uurlid(uurlid: \"%s\", archived: false) { uurlid title description tags subject subject_id level level_id created_at user_id
                                    subjects {id subject} levels {id level}
                                    questions { id question qtype hint points user_id explanation fulfill ordnen answers {id answer ordnen correct question_id }}}}"
                                  uurlid)]
          (rf/dispatch [::re-graph/query query {} [:test-load-process]]))))

(rf/reg-event-db
 :process-create-question
 []
 (fn [db [_ response]]
   (let [question       (-> response :data :create_question)
         qkeyword       (:id question)
         ques-answers   (assoc question :answers {})
         final-question (assoc {} qkeyword ques-answers)]
     (-> db
         (assoc  :loading?  false)     ;; take away that "Loading ..." UI
         (update :qform not)           ;; hide new question form
         (update-in [:questions] conj final-question)))))

(rf/reg-event-fx
  :create-question
  (fn [cfx _]
    (let [values        (cms/str-to-int (second _) :qtype :test-id :user-id)
          {:keys [question hint explanation qtype points uurlid user-id]} values
          mutation      (gstring/format "mutation { create_question(question: \"%s\", hint: \"%s\", explanation: \"%s\",
                                         qtype: %i, points: %i, uurlid: \"%s\", user_id: %i) { id question qtype hint explanation user_id ordnen points fulfill }}"
                                        question hint explanation qtype points uurlid user-id)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-create-question]]))))

(rf/reg-event-db
 :process-create-answer
 (fn [db [_ response]]
   (let [answer      (:create_answer (second (first response)))
         question-id (:question_id answer)
         post-resp   (assoc {} (:id answer) answer)]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI
         (update-in [:questions question-id :answers] conj post-resp)))))

(rf/reg-event-fx
  :create-answer
  (fn [cfx _]
    (let [{:keys [question-id correct answer]} (second _)
          question-id-int (js/parseInt question-id)
          mutation    (gstring/format "mutation { create_answer( question_id: %i, correct: %s, answer:\"%s\")
                                      { id question_id answer correct ordnen }}"
                                      question-id-int correct answer)]
      (rf/dispatch [::re-graph/mutate mutation {} [:process-create-answer]]))))

(rf/reg-event-db
 :process-delete-question
 []
 (fn [db [_ data]]
   (let [question-id (-> data :data :delete_question :id)]
     (-> db
         (update-in [:questions] dissoc question-id)
         (update :qcounter dec)
         (update  :loading? not)))))

(rf/reg-event-fx
 :delete-question
 (fn [cofx [dispatch-id question-id uurlid]]
   (when (js/confirm "Frage löschen?")
     (let [mutation  (gstring/format "mutation { delete_question( question_id: %i, uurlid: \"%s\" ) { id }}"
                                     question-id uurlid)]
       (rf/dispatch [::re-graph/mutate mutation  {} [:process-delete-question]])))))

(rf/reg-event-db
 :process-after-delete-answer
 []
 (fn [db [_ response]]
    (let [answer      (-> response :data :delete_answer)
          question-id (:question_id answer)
          answer-id   (:id answer)]
     (-> db
         (update-in [:questions question-id :answers] dissoc answer-id)
         (update :loading? not)))))

(rf/reg-event-fx
 :delete-answer
 (fn [cofx [_ data]]
   (when (js/confirm "Delete answer?")
     (let [{:keys [answer-id question-id]} data
           answer-id-int  (js/parseInt answer-id)
           mutation       (gstring/format "mutation { delete_answer( answer_id: %i, question_id: %i ) { id question_id }}"
                                          answer-id-int question-id)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-after-delete-answer]])))))

(rf/reg-event-db
 :process-after-update-question
 []
 (fn
   [db [_ response]]
   (let [question     (-> response :data :update_question)
         qkeyword     (:id question)]
       (-> db
           (update-in [:questions qkeyword] conj question)
           (update :loading? not)))))

(rf/reg-event-fx
  :update-question
  (fn [cofx [_ updates]]
    (let [{:keys [id question hint explanation qtype points quest_update uurlid]} updates
          mutation  (gstring/format "mutation { update_question( id: %i, question: \"%s\",
                                      hint: \"%s\", explanation: \"%s\", qtype: %i, points: %i, quest_update: %s, uurlid: \"%s\")
                                     { id question hint explanation qtype points ordnen fulfill user_id }}"
                                    id question hint explanation qtype points quest_update uurlid)]
      (rf/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-question]]))))

(rf/reg-event-db
 :process-after-update-fulfill
 []
 (fn [db [_ response]]
   (let [question  (-> response :data :update_fulfill)
         qkeyword  (:id question)
         fulfill   (:fulfill question)]
     (-> db
         (assoc-in [:questions qkeyword :fulfill] fulfill)
         (update :loading?  not)))))

(rf/reg-event-fx
  :update-fulfill
  (fn [cofx [_ updates]]
    (let [{:keys [id fulfill]} updates
          mutation  (gstring/format "mutation { update_fulfill( id: %i, fulfill: \"%s\")
                                     { id fulfill }}"
                                    id fulfill)]
      (rf/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-fulfill]]))))

(rf/reg-event-db
 :process-after-update-answer
 []
 (fn [db [_ response]]
   (let [answer           (-> response :data :update_quote)
         answer-keyword   (:id answer)
         question-keyword (:question_id answer)]
       (-> db
          (update-in [:questions question-keyword :answers answer-keyword] conj answer)
          (update :loading? not)))))

(rf/reg-event-fx
  :update-answer
  (fn [cofx [_ updates]]
    (let [{:keys [answer_id answer correct]} updates
          mutation  (gstring/format "mutation { update_answer( answer: \"%s\", correct: %s, id: %i)
                                    { id answer correct question_id }}"
                                  answer correct answer_id)]
       (rf/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-answer]]))))

(rf/reg-event-db
 :process-after-update-test
 []
 (fn [db [_ response]]
   (let [test (-> response :data :update_test)]
   (-> db
       (assoc :test test)
       (update :loading?  not)
       (update :testform  not)))))

(rf/reg-event-fx
  :update-test
  (fn
    [cofx [_ updates]]
    (let [{:keys [title description tags subject_id level_id uurlid]} updates
          mutation  (gstring/format "mutation { update_test( title: \"%s\", description: \"%s\", tags: \"%s\", subject_id: %i, level_id: %i, uurlid: \"%s\")
                                    { uurlid title description subject_id level_id subject level tags created_at }}"
                                  title description tags subject_id level_id uurlid)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-after-update-test]]))))

(rf/reg-event-db
 :process-after-reorder-question
 []
 (fn [db [_ response]]
   (let [questions     (-> response :data :reorder_question :questions)]
   (-> db
       (assoc-in [:questions] questions)
       (update :loading? not)))))

(rf/reg-event-fx
  :reorder-question
  (fn [cofx [_ updates]]
    (let [{:keys [uurlid ordnen direction]} updates
          mutation (gstring/format "mutation { reorder_question(uurlid: \"%s\", ordnen: %i, direction: \"%s\")
                                    { uurlid title questions { id question hint explanation qtype points ordnen fulfill
                                                               answers { id answer correct ordnen question_id } }}}"
                                   uurlid ordnen direction)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-after-reorder-question]]))))

(rf/reg-event-db
 :process-after-reorder-answer
 []
 (fn [db [_ response]]
   (let [data      (-> response :data :reorder_answer)
         answers   (cms/vector-to-ordered-idxmap (:answers data))
         q-keyword (:id data)]
   (-> db
       (assoc-in [:questions q-keyword :answers] answers)
       (update :loading? not)))))

(rf/reg-event-fx
  :reorder-answer
  (fn [cofx [_ updates]]
    (let [{:keys [ordnen question-id direction]} updates
          mutation (gstring/format "mutation { reorder_answer(ordnen: %i, question_id: %i, direction: \"%s\")
                                    { id question hint qtype fulfill answers { id answer correct ordnen question_id }}}"
                                  ordnen question-id direction)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-after-reorder-answer]]))))

