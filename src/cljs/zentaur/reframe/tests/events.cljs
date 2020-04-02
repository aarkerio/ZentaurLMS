(ns zentaur.reframe.tests.events
  (:require [ajax.core :as ajax]
            [cljs.spec.alpha :as s]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.tests.db :as zdb]
            [zentaur.reframe.tests.libs :as libs]))

;; -- Check Interceptor (edit for subway)  ----------
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (re-frame/after (partial check-and-throw :zentaur.reframe.tests.db/db)))  ;; PARTIAL: a way to currying

;; We now create the interceptor chain shared by all event handlers which manipulate todos.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def todo-interceptors [check-spec-interceptor])

;;;;;;;;    CO-EFFECT HANDLERS (with Ajax!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx         ;; part of the re-frame API
 :initialise-db                ;; event id being handled
 ;; the event handler (function) being registered
 (fn [{:keys [db]} _]                       ;; take 2 values from coeffects. Ignore event vector itself.
   {:db zdb/default-db}))   ;; all hail the new state to be put in app-db

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

;;; ###########################################
(defn update-ids
  "Convert ids to integers"
  [data]
  (map #(update % :id (fn [k] (js/parseInt k))) data))

(def trim-event
  (re-frame.core/->interceptor
    :id      :trim-event
    :before  (fn [context]
               (.log js/console (str ">>> CTX >>>>> " context ))
               (let [trim-fn (fn [event] (-> event rest vec))]
                 (update-in context [:coeffects :event] trim-fn)))))

(defn vector-to-ordered-idxmap
  "Convert vector od maps to an indexed map"
  [rows]
  (let [indexed (reduce #(assoc %1 (keyword (:id %2)) %2) {} rows)]
     (into (sorted-map-by (fn [key1 key2]
                            (compare
                             (get-in indexed [key1 :ordnen])
                             (get-in indexed [key2 :ordnen]))))
      indexed)
    ))

(def reorder-questions
  (re-frame/->interceptor
   :id      :reorder-questions
   :after   (fn [context]
              (let [app-db    (-> context :effects :db)
                    questions (:questions app-db)
                    qordered  (vector-to-ordered-idxmap questions)]
                (assoc-in context [:effects :db :questions] qordered)))))

(def reorder-after-questions-interceptor (re-frame/after (partial reorder-questions)))

(re-frame/reg-event-db
 :process-test-response
  [trim-event]
  (fn [db [{:keys [data errors]}]]
    (.log js/console (str ">>> DATA process-test-response  >>>>> " data ))
    (let [test          (:test_by_uurlid data)
          questions     (:questions test)
          ques-answers  (map #(update % :answers vector-to-ordered-idxmap) questions)
          questions-idx (vector-to-ordered-idxmap ques-answers)
          subjects      (update-ids (:subjects test))
          only-test     (dissoc test :subjects :questions)
          _             (.log js/console (str ">>> subjects >>>>> " subjects))
          _             (.log js/console (str ">>> questions >>>>> " questions-idx))
          _             (.log js/console (str ">>> TEST >>>>> " only-test))
          ]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI element
         (assoc :test      only-test)
         (assoc :subjects  subjects)
         (assoc :questions questions-idx)))))

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :test-load
  (fn                      ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [uurlid  (.-value (gdom/getElement "uurlid"))
          query   (gstring/format "{test_by_uurlid(uurlid: \"%s\", archived: false) { uurlid title description tags subject subject_id created_at user_id
                                    subjects {id subject} questions { id question qtype hint points user_id explanation fulfill ordnen answers {id answer ordnen correct question_id }}}}"
                                  uurlid)]
          ;; perform a query, with the response sent to the callback event provided
          (re-frame/dispatch [::re-graph/query query {} [:process-test-response]]))))

(def reorder-after-questions
  (re-frame/->interceptor
   :id      :reorder-questions
   :after   (fn [context]
              (let [app-db    (-> context :effects :db)
                    questions (:questions app-db)
                    qordered  (fn [rows] (into (sorted-map-by (fn [key1 key2]
                                                               (compare
                                                                (get-in rows [key1 :ordnen])
                                                                (get-in rows [key2 :ordnen]))))
                                              rows))]
                (update-in context [:effects :db :questions] qordered)))))

(re-frame/reg-event-db
 :process-create-question
 [reorder-after-questions]
 (fn
   [db [_ response]]                 ;; destructure the response from the event vector
   (let [question       (-> response :data :create_question)
         qkeyword       (keyword (:id question))
         ques-answers   (assoc question :answers {})
         final-question (assoc {} qkeyword ques-answers)]
     (-> db
         (assoc  :loading?  false)     ;; take away that "Loading ..." UI
         (update :qform not)           ;; hide new question form
         (update-in [:questions] conj final-question)))))

(re-frame/reg-event-fx
  :create-question
  (fn                    ;; <-- the handler function
    [cfx _]             ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    ;; question hint explanation qtype test-id user-id active
    (let [values        (libs/str-to-int (second _) :qtype :test-id :user-id)
          _             (.log js/console (str ">>> VALUES AFTER  >>>>> " values ))
          {:keys [question hint explanation qtype points uurlid user-id]} values
          mutation      (gstring/format "mutation { create_question(question: \"%s\", hint: \"%s\", explanation: \"%s\",
                                         qtype: %i, points: %i, uurlid: \"%s\", user_id: %i) { id question qtype hint explanation user_id ordnen points fulfill }}"
                                        question hint explanation qtype points uurlid user-id)]
           (.log js/console (str ">>> MUTATTION  >>>>> " mutation ))
      ;; perform a query, with the response sent to the callback event provided
      (re-frame/dispatch [::re-graph/mutate
                          mutation                           ;; graphql query
                          {:some "Pumas prros!! variable"}   ;; arguments map
                          [:process-create-question]]))))

(re-frame/reg-event-db
 :process-create-answer
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (let [answer      (:create_answer (second (first response)))
         _           (.log js/console (str ">>> ANSWER QQ >>>>> " answer))
         question-id (keyword (str (:question_id answer)))
         post-resp   (assoc {} (keyword (:id answer)) answer)
         _           (.log js/console (str ">>> QUESTION ID >>>>>  post-resp: " post-resp "  >>>> question-id: " question-id))]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI
         (update-in [:questions question-id :answers] conj post-resp)))))

(re-frame/reg-event-fx
  :create-answer
  (fn                    ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    (let [{:keys [question-id correct answer]} (second _)
          question-id-int (js/parseInt question-id)
          mutation    (gstring/format "mutation { create_answer( question_id: %i, correct: %s, answer:\"%s\")
                                      { id question_id answer correct ordnen }}"
                                      question-id-int correct answer)]
      (.log js/console (str ">>> CREATE ANSWER MUTATION >>>>> " mutation ))
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-answer]]))))

(re-frame/reg-event-db
 :process-delete-question
 []
 (fn
   [db [_ data]]
   (.log js/console (str ">>> Data  VVV >>>>> " data ))
   (let [question-id (-> data :data :delete_question :id)] ;; Datein Komm zurück
     (.log js/console (str ">>> QUESTION >>>>> " question-id ))
     (-> db
         (update-in [:questions] dissoc (keyword question-id))
         (update :qcounter dec)
         (update  :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-question            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id question-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> dispatch-id   OOO>>>>> " dispatch-id "  >>>> " question-id))
   (when (js/confirm "Frage löschen?")
     (let [uurlid    (.-value (gdom/getElement "uurlid"))
           mutation  (gstring/format "mutation { delete_question( question_id: %i, uurlid: \"%s\" ) { id }}"
                                     question-id uurlid)]
       (re-frame/dispatch [::re-graph/mutate
                           mutation                           ;; graphql mutation
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-delete-question]])))))

(re-frame/reg-event-db
 :process-after-delete-answer
 []
 (fn
   [db [_ response]]
   (let [_           (.log js/console (str ">>> RESPONSE AFTER DELETE ANSWER  >>>>> " response ))
         answer      (-> response :data :delete_answer)
         question-id (keyword (str (:question_id answer)))
         answer-id   (keyword (:id answer))]
     (.log js/console (str ">>> answer-id >>>>> " answer-id " >>>> question-id  >>>" question-id))
     (-> db
         (update-in [:questions question-id :answers] dissoc answer-id)
         (update :loading? not)))))

(re-frame/reg-event-fx        ;; <-- note the `-fx` extension
 :delete-answer               ;; <-- the event id
 (fn                           ;; <-- the handler function
   [cofx [_ data]]            ;; <-- 1st argument is coeffect, from which we extract db
   (when (js/confirm "Delete answer?")
     (let [{:keys [answer-id question-id]} data
           answer-id-int  (js/parseInt answer-id)
           mutation       (gstring/format "mutation { delete_answer( answer_id: %i, question_id: %i ) { id question_id }}"
                                          answer-id-int question-id)]
       (re-frame/dispatch [::re-graph/mutate
                           mutation                           ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-delete-answer]])))))

(re-frame/reg-event-db
 :process-after-update-question
 []
 (fn
   [db [_ response]]
   (.log js/console (str ">>> UPP XXXXX response response >>>>> " response))
   (let [question     (-> response :data :update_question)
         qkeyword     (keyword (:id question))
         _            (.log js/console (str ">>> question UPDATED >>>>> " question " >> >  >  " qkeyword))]
       (-> db
           (update-in [:questions qkeyword] conj question)
           (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-question           ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [id question hint explanation qtype points quest_update uurlid]} updates
          mutation  (gstring/format "mutation { update_question( id: %i, question: \"%s\",
                                      hint: \"%s\", explanation: \"%s\", qtype: %i, points: %i, quest_update: %s, uurlid: \"%s\")
                                     { id question hint explanation qtype points ordnen fulfill user_id }}"
                                    id question hint explanation qtype points quest_update uurlid)]
      (.log js/console (str ">>> UQ MUTATION >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation                                  ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-update-question]]))))
(re-frame/reg-event-db
 :process-after-update-fulfill
 []
 (fn
   [db [_ response]]
   (let [question  (-> response :data :update_fulfill)
         qkeyword  (keyword (:id question))
         fulfill   (:fulfill question)]
     (-> db
         (assoc-in [:questions qkeyword :fulfill] fulfill)
         (update :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-fulfill           ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [id fulfill]} updates
          mutation  (gstring/format "mutation { update_fulfill( id: %i, fulfill: \"%s\")
                                     { id fulfill }}"
                                    id fulfill)]
      (.log js/console (str ">>> MUTATION  >>>>> " mutation))
      (re-frame/dispatch [::re-graph/mutate
                          mutation                                  ;; graphql query
                          {:some "Pumas campeón prros!! variable"}   ;; arguments map
                          [:process-after-update-fulfill]]))))

(re-frame/reg-event-db
 :process-after-update-answer
 []
 (fn [db [_ response]]
   (let [answer           (-> response :data :update_answer)
         answer-keyword   (keyword (:id answer))
         question-keyword (keyword (str (:question_id answer)))]
       (-> db
          (update-in [:questions question-keyword :answers answer-keyword] conj answer)
          (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-answer             ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]        ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [answer correct answer_id]} updates
          mutation  (gstring/format "mutation { update_answer( answer: \"%s\", correct: %s, id: %i)
                                    { id answer correct question_id }}"
                                  answer correct answer_id)]
       (.log js/console (str ">>> MUTATION UPDATE ANSWER >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation                                  ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-update-answer]]))))

(re-frame/reg-event-db
 :process-after-update-test
 []
 (fn [db [_ response]]
   (.log js/console (str ">>> VALUE process-after-update-test >>>>> " response ))
   (let [test (-> response :data :update_test)]
   (-> db
       (assoc :test test)
       (update :loading?  not)
       (update :testform  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-test               ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]        ;; <-- 1st argument is coeffect, from which we extract db
    (let [_  (.log js/console (str ">>> VALUES UPDATES >>>>> " updates ))
          {:keys [title description tags subject_id uurlid]} updates
          mutation  (gstring/format "mutation { update_test( title: \"%s\", description: \"%s\", tags: \"%s\", subject_id: %i, uurlid: \"%s\")
                                    { uurlid title description subject_id subject tags created_at }}"
                                  title description tags subject_id uurlid)]
       (.log js/console (str ">>> MUTATION UPDATE TEST >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation                           ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-update-test]]))))


(re-frame/reg-event-db
 :process-after-reorder-question
 []
 (fn [db [_ response]]
   (let [questions     (-> response :data :reorder_question :questions)
         ques-answers  (map #(update % :answers vector-to-ordered-idxmap) questions)
         idx-questions (vector-to-ordered-idxmap ques-answers)]
   (-> db
       (assoc-in [:questions] idx-questions)
       (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :reorder-question          ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [_       (.log js/console (str ">>> VALUES OOO UPDATES >>>>> " updates))
          {:keys [uurlid ordnen direction]} updates
          mutation (gstring/format "mutation { reorder_question(uurlid: \"%s\", ordnen: %i, direction: \"%s\")
                                    { uurlid title questions { id question hint explanation qtype points ordnen fulfill
                                                               answers { id answer correct ordnen question_id } }}}"
                                   uurlid ordnen direction)]
         (.log js/console (str ">>> GRAPHQL Mutation >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation             ;; graphql query
                           {}                   ;; arguments map
                           [:process-after-reorder-question]]))))


(re-frame/reg-event-db
 :process-after-reorder-answer
 []
 (fn [db [_ response]]
   (let [data      (-> response :data :reorder_answer)
         answers   (vector-to-ordered-idxmap (:answers data))
         q-keyword (keyword (:id data))]
   (-> db
       (assoc-in [:questions q-keyword :answers] answers)
       (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :reorder-answer            ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [ordnen question-id direction]} updates
          mutation (gstring/format "mutation { reorder_answer(ordnen: %i, question_id: %i, direction: \"%s\")
                                    { id question hint qtype fulfill answers { id answer correct ordnen question_id }}}"
                                  ordnen question-id direction)]
       (re-frame/dispatch [::re-graph/mutate
                           mutation                                   ;; graphql query
                           {}   ;; arguments map
                           [:process-after-reorder-answer]]))))
