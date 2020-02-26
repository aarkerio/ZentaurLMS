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

(defn order-questions
  "helper to reorder"
  [db]
  (let [questions (:questions db)]
    (into (sorted-map-by
           (fn [key1 key2]
             (compare (:ordnen (get questions key1))
                      (:ordnen (get questions key2)))))
          questions)))

(def reorder-event
  (re-frame/->interceptor
   :id      :reorder-event
   :after   (fn [context]
              (let [ordered-questions (order-questions (-> context  :effects :db)) ]
                (assoc-in context [:effects :db :questions] ordered-questions)))))

;; (def reorder-after-interceptor (re-frame/after (partial order-questions)))

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

(defn vector-to-idxmap
  "Convert vector od maps to an indexed map"
  [rows]
  (into {} (map-indexed (fn [idx row] {(keyword (:id row)) row}) rows)))

(re-frame/reg-event-db
 :process-test-response
  [trim-event]
  (fn [db [ {:keys [data errors] :as payload}]]
    (.log js/console (str ">>> DATA process-test-response  >>>>> " data ))
    (let [test          (:test_by_uurlid data)
          questions     (:questions test)
          ques-answers  (map #(update % :answers vector-to-idxmap) questions)
          questions-idx (vector-to-idxmap ques-answers)
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
          query   (gstring/format "{test_by_uurlid(uurlid: \"%s\", archived: false) { uurlid title description tags subject subject_id created_at
                                    subjects {id subject} questions { id question qtype hint points explanation fulfill ordnen answers {id answer ordnen correct question_id }}}}"
                                  uurlid)]
          ;; perform a query, with the response sent to the callback event provided
          (re-frame/dispatch [::re-graph/query query {} [:process-test-response]]))))

(re-frame/reg-event-db
 :process-create-question
 []
 (fn
   [db [_ response]]                 ;; destructure the response from the event vector
   (.log js/console (str ">>> respoNSE AFTER NEW question >>>>> " response ))
   (let [question       (-> response :data :create_question)
         qkeyword       (keyword (:id question))
         ques-answers   (assoc question :answers {})
         final-question (assoc {} qkeyword ques-answers)
         _              (.log js/console (str ">>> final-question >>>>> " final-question ))]
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
                                         qtype: %i, points: %i, uurlid: \"%s\", user_id: %i) { id question qtype hint explanation points }}"
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
   (let [question   (-> response :data :update_question)
         qkeyword   (keyword (:id question))
             _   (.log js/console (str ">>> question llll >>>>> " question " >> >  >  " qkeyword))]
     (-> db
         (update-in [:questions qkeyword] conj question)
         (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-question           ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [id question hint explanation qtype points]} updates
          mutation  (gstring/format "mutation { update_question( id: %i, question: \"%s\",
                                      hint: \"%s\", explanation: \"%s\", qtype: %i, points: %i)
                                     { id question hint explanation qtype points ordnen fulfill }}"
                                    id question hint explanation qtype points)]
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
         _         (.log js/console (str ">>> VALUE QQUESTION FULFILL >>>>> " question ))
         qkeyword  (keyword (:id question))
         fulfill   (:fulfill question)]
     (-> db
         (update-in [:questions qkeyword :fulfill] conj fulfill)
         (update :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-fulfill           ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [{:keys [id fulfill]} updates
          mutation  (gstring/format "mutation { update_fulfill( id: %i, fulfill: \"%s\")
                                     { id fulfill }}"
                                    id fulfill)]
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
   (.log js/console (str ">>> VALUE process-after-update-test >>>>> " response ))
   (let [test (-> response :data :update_test)]
   (-> db
       (assoc :test test)
       (update :loading? not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :reorder-question          ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]       ;; <-- 1st argument is coeffect, from which we extract db
    (let [uurlid  (.-value (gdom/getElement "uurlid"))
          _       (.log js/console (str ">>> VALUES OOO UPDATES >>>>> " updates "  uurlid >>>> " uurlid))
          {:keys [question-id direction]} updates
          question-id-int (js/parseInt question-id)
          mutation (gstring/format "mutation { reorder_question(uurlid: \"%s\", question_id: %i, direction: \"%s\")
                                    { uurlid title questions { id question hint explanation qtype points ordnen fulfill }}}"
                                  uurlid question-id-int direction)]
       (.log js/console (str ">>> MUTATION UPDATE ORDER >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation                           ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-reorder-question]]))))
