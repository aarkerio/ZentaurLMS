(ns zentaur.reframe.tests.events
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.libs.commons :as cms]
            [zentaur.reframe.tests.db :as zdb]))

;; -- Check Spec Interceptor  ----------
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (re-frame/after (partial check-and-throw :zentaur.reframe.tests.db/db)))  ;; PARTIAL: a way to currying

;; We now create the interceptor chain shared by all event handlers which manipulate todos.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def test-interceptors [check-spec-interceptor])

(re-frame/reg-event-fx
 :initialise-db
 (fn [{:keys [db]} _]
   {:db zdb/default-db}))

(re-frame/reg-event-db
 :bad-response
 (fn
   [db [_ response]]
   (.log js/console (str ">>> Fheler aus ajax antwort : >>>>> " response "   " _))))

(re-frame/reg-event-db
 :toggle-qform     ;; versteckt / zeigt neues Frageformular
 (fn [db _]
   (update db :qform not)))

(re-frame/reg-event-db
 :toggle-testform     ;; versteckt / zeigt bearbeit testformular
 (fn [db _]
   (update db :testform not)))

(re-frame/reg-event-db
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

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :test-load
  (fn [cfx _]
    (let [uurlid  (.-value (gdom/getElement "uurlid"))
          query   (gstring/format "{test_by_uurlid(uurlid: \"%s\", archived: false) { uurlid title description tags subject subject_id level level_id created_at user_id
                                    subjects {id subject} levels {id level}
                                    questions { id question qtype hint points user_id explanation fulfill ordnen answers {id answer ordnen correct question_id }}}}"
                                  uurlid)]
          (re-frame/dispatch [::re-graph/query query {} [:test-load-process]]))))

(re-frame/reg-event-db
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

(re-frame/reg-event-fx
  :create-question
  (fn [cfx _]
    (let [values        (cms/str-to-int (second _) :qtype :test-id :user-id)
          {:keys [question hint explanation qtype points uurlid user-id]} values
          mutation      (gstring/format "mutation { create_question(question: \"%s\", hint: \"%s\", explanation: \"%s\",
                                         qtype: %i, points: %i, uurlid: \"%s\", user_id: %i) { id question qtype hint explanation user_id ordnen points fulfill }}"
                                        question hint explanation qtype points uurlid user-id)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-question]]))))

(re-frame/reg-event-db
 :process-create-answer
 (fn [db [_ response]]
   (let [answer      (:create_answer (second (first response)))
         question-id (:question_id answer)
         post-resp   (assoc {} (:id answer) answer)]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI
         (update-in [:questions question-id :answers] conj post-resp)))))

(re-frame/reg-event-fx
  :create-answer
  (fn [cfx _]
    (let [{:keys [question-id correct answer]} (second _)
          question-id-int (js/parseInt question-id)
          mutation    (gstring/format "mutation { create_answer( question_id: %i, correct: %s, answer:\"%s\")
                                      { id question_id answer correct ordnen }}"
                                      question-id-int correct answer)]
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-answer]]))))

(re-frame/reg-event-db
 :process-delete-question
 []
 (fn [db [_ data]]
   (let [question-id (-> data :data :delete_question :id)]
     (-> db
         (update-in [:questions] dissoc question-id)
         (update :qcounter dec)
         (update  :loading? not)))))

(re-frame/reg-event-fx
 :delete-question
 (fn [cofx [dispatch-id question-id uurlid]]
   (when (js/confirm "Frage löschen?")
     (let [mutation  (gstring/format "mutation { delete_question( question_id: %i, uurlid: \"%s\" ) { id }}"
                                     question-id uurlid)]
       (re-frame/dispatch [::re-graph/mutate mutation  {} [:process-delete-question]])))))

(re-frame/reg-event-db
 :process-after-delete-answer
 []
 (fn [db [_ response]]
    (let [answer      (-> response :data :delete_answer)
          question-id (:question_id answer)
          answer-id   (:id answer)]
     (-> db
         (update-in [:questions question-id :answers] dissoc answer-id)
         (update :loading? not)))))

(re-frame/reg-event-fx
 :delete-answer
 (fn [cofx [_ data]]
   (when (js/confirm "Delete answer?")
     (let [{:keys [answer-id question-id]} data
           answer-id-int  (js/parseInt answer-id)
           mutation       (gstring/format "mutation { delete_answer( answer_id: %i, question_id: %i ) { id question_id }}"
                                          answer-id-int question-id)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-after-delete-answer]])))))

(re-frame/reg-event-db
 :process-after-update-question
 []
 (fn
   [db [_ response]]
   (let [question     (-> response :data :update_question)
         qkeyword     (:id question)]
       (-> db
           (update-in [:questions qkeyword] conj question)
           (update :loading? not)))))

(re-frame/reg-event-fx
  :update-question
  (fn [cofx [_ updates]]
    (let [{:keys [id question hint explanation qtype points quest_update uurlid]} updates
          mutation  (gstring/format "mutation { update_question( id: %i, question: \"%s\",
                                      hint: \"%s\", explanation: \"%s\", qtype: %i, points: %i, quest_update: %s, uurlid: \"%s\")
                                     { id question hint explanation qtype points ordnen fulfill user_id }}"
                                    id question hint explanation qtype points quest_update uurlid)]
      (re-frame/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-question]]))))

(re-frame/reg-event-db
 :process-after-update-fulfill
 []
 (fn [db [_ response]]
   (let [question  (-> response :data :update_fulfill)
         qkeyword  (:id question)
         fulfill   (:fulfill question)]
     (-> db
         (assoc-in [:questions qkeyword :fulfill] fulfill)
         (update :loading?  not)))))

(re-frame/reg-event-fx
  :update-fulfill
  (fn [cofx [_ updates]]
    (let [{:keys [id fulfill]} updates
          mutation  (gstring/format "mutation { update_fulfill( id: %i, fulfill: \"%s\")
                                     { id fulfill }}"
                                    id fulfill)]
      (re-frame/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-fulfill]]))))

(re-frame/reg-event-db
 :process-after-update-answer
 []
 (fn [db [_ response]]
   (let [answer           (-> response :data :update_quote)
         answer-keyword   (:id answer)
         question-keyword (:question_id answer)]
       (-> db
          (update-in [:questions question-keyword :answers answer-keyword] conj answer)
          (update :loading? not)))))

(re-frame/reg-event-fx
  :update-answer
  (fn [cofx [_ updates]]
    (let [{:keys [answer_id answer correct]} updates
          mutation  (gstring/format "mutation { update_answer( answer: \"%s\", correct: %s, id: %i)
                                    { id answer correct question_id }}"
                                  answer correct answer_id)]
       (re-frame/dispatch [::re-graph/mutate mutation {:some "Pumas campeón prros!! variable"} [:process-after-update-answer]]))))

(re-frame/reg-event-db
 :process-after-update-test
 []
 (fn [db [_ response]]
   (let [test (-> response :data :update_test)]
   (-> db
       (assoc :test test)
       (update :loading?  not)
       (update :testform  not)))))

(re-frame/reg-event-fx
  :update-test
  (fn
    [cofx [_ updates]]
    (let [{:keys [title description tags subject_id level_id uurlid]} updates
          mutation  (gstring/format "mutation { update_test( title: \"%s\", description: \"%s\", tags: \"%s\", subject_id: %i, level_id: %i, uurlid: \"%s\")
                                    { uurlid title description subject_id level_id subject level tags created_at }}"
                                  title description tags subject_id level_id uurlid)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-after-update-test]]))))

(re-frame/reg-event-db
 :process-after-reorder-question
 []
 (fn [db [_ response]]
   (let [questions     (-> response :data :reorder_question :questions)]
   (-> db
       (assoc-in [:questions] questions)
       (update :loading? not)))))

(re-frame/reg-event-fx
  :reorder-question
  (fn [cofx [_ updates]]
    (let [{:keys [uurlid ordnen direction]} updates
          mutation (gstring/format "mutation { reorder_question(uurlid: \"%s\", ordnen: %i, direction: \"%s\")
                                    { uurlid title questions { id question hint explanation qtype points ordnen fulfill
                                                               answers { id answer correct ordnen question_id } }}}"
                                   uurlid ordnen direction)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-after-reorder-question]]))))

(re-frame/reg-event-db
 :process-after-reorder-answer
 []
 (fn [db [_ response]]
   (let [data      (-> response :data :reorder_answer)
         answers   (cms/vector-to-ordered-idxmap (:answers data))
         q-keyword (:id data)]
   (-> db
       (assoc-in [:questions q-keyword :answers] answers)
       (update :loading? not)))))

(re-frame/reg-event-fx
  :reorder-answer
  (fn [cofx [_ updates]]
    (let [{:keys [ordnen question-id direction]} updates
          mutation (gstring/format "mutation { reorder_answer(ordnen: %i, question_id: %i, direction: \"%s\")
                                    { id question hint qtype fulfill answers { id answer correct ordnen question_id }}}"
                                  ordnen question-id direction)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-after-reorder-answer]]))))

;;;;;;;;;;;;;;;;;;;  SEARCH SCREEN QUESTIONS  ;;;;;;;;;;;;;;;;
(re-frame/reg-event-db
 :process-load-search
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [post-data  (:load_search data)
          subjects   (:subjects post-data)
          levels     (:levels post-data)
          langs      (:langs post-data)
          _          (.log js/console (str ">>> SUBJECTS >>>>> " subjects))
          _          (.log js/console (str ">>> LEVELS >>>>> " levels))
          _          (.log js/console (str ">>> LANGS >>>>> " langs))
          ]
     (-> db
         (assoc :subjects subjects)
         (assoc :levels   levels)
         (assoc :langs    langs)))))

(re-frame/reg-event-fx
  :load-search
  (fn
    [cfx [_ _]]
    (let [query (gstring/format "{load_search {uurlid title subjects {id subject} levels {id level} langs {id lang}}}")]
      (re-frame/dispatch [::re-graph/query query {} [:process-load-search]]))))

(re-frame/reg-event-db
 :add-search-elm
  []
  (fn [db [_ updates]]
    (let [ksection  (first (first updates))  ;; key section
          vsection  (get updates ksection)   ;; value section
          elm       (str ksection "_" vsection)
          checkbox  (gdom/getElement elm)
          checked   (.. checkbox -checked)]
   (if checked
     (update-in db [:search-terms ksection] conj vsection)
     (update-in db [:search-terms ksection] (fn [all] (remove #(when (= % vsection) %) all)))))))

(re-frame/reg-event-db
 :search-question-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [questions     (-> data :search_questions :questions)]
         (assoc db :questions  questions))))

(re-frame/reg-event-fx
  :search-questions
  (fn [cfx [_ updates]]
    (let [{:keys [search-text]} updates
          search-terms (-> cfx :db :search-terms)
          _ (.log js/console (str ">>> search-terms >>>>> " search-terms ))
          subjects (str/join " " (get search-terms "subjects"))
          levels   (str/join " " (get search-terms "levels"))
          langs    (str/join " " (get search-terms "langs"))
          _  (.log js/console (str ">>> SQQQQQ >>>>> " updates " >> " subjects " >>> levels >> " levels "  langs >> " langs))
          query      (gstring/format "{search_fullq(subjects: \"%s\", levels: \"%s\", langs: \"%s\", terms: \"%s\")
                                      { uurlid title questions { id question qtype }}}"
                                     subjects levels langs search-text)]
      (.log js/console (str ">>> QUERRRY  >>>>> " query ))
          ;; perform a query, with the response sent to the callback event provided
          (re-frame/dispatch [::re-graph/query query {} [:search-question-response]]))))

;;;;;;;;    BLOG COMMENTS  SECTION  ;;;;;;;;
(re-frame/reg-event-db
 :load-comments-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-comments (:load_comments data)
          comments     (:comments pre-comments)]
      (assoc db :comments comments))))

(re-frame/reg-event-fx
  :load-comments
  (fn [cfx [_ updates]]
    (let [post-id (.-value (gdom/getElement "post-id"))
          query   (gstring/format "{load_comments(post_id: %i) {comments {comment username created_at}}}"
                                  post-id)]
      (re-frame/dispatch [::re-graph/query query {} [:load-comments-response]]))))

(re-frame/reg-event-db
 :process-save-blog-comment
 (fn [db [_ response]]
   (let [comment     (:create_comment (second (first response)))]
         (update-in db [:comments] conj comment))))

(re-frame/reg-event-fx
  :save-blog-comment
  (fn [cfx _]
    (let [updates (second _)
          {:keys [post-id comment user-id]} updates
          mutation    (gstring/format "mutation { create_comment( post_id: %i, comment: \"%s\", user_id: %i)
                                      { username comment created_at }}"
                                      post-id comment user-id)]
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-save-blog-comment]]))))
