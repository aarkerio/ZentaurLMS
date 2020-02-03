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

;;; ###########################################7

(re-frame/reg-event-db
  :process-test-response
  (fn [db [_ {:keys [data errors] :as payload}]]
    (.log js/console (str ">>> DATA DATA  >>>>> " payload ))
    (let [full-data   (:questions_by_test data)
          questions   (:questions full-data)
          _           (.log js/console (str ">>> questions >>>>> " questions ))
          test        (:test full-data)]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI element
         (assoc :test      (js->clj test :keywordize-keys true))
         (assoc :questions (js->clj questions :keywordize-keys true))))))

;; "{ questions_by_test(id: %i) { test {id title description tags created_at}
;;                                            questions { id question qtype explanation hint ordnen
;;                                            answers { id answer correct } }}}"
;;;;;;;;    CO-EFFECT HANDLERS (with Ajax!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :request-test
  (fn                      ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [db            (:db cfx)
          pre-test-id   (.-value (gdom/getElement "test-id"))
          test-id       (js/parseInt pre-test-id)
          query         (gstring/format "{ test_by_id(id: %i, archived: false) { title description subjects {id subject} } }"
                                        test-id)]
          ;; perform a query, with the response sent to the callback event provided
          (re-frame/dispatch [::re-graph/query
                              query                              ;; graphql query
                              {:team "Pumas prros!!!"}           ;; arguments map
                              [:process-test-response]])         ;; callback event when response is recieved
          )))

;;##########################################333

;; AJAX handlers
(re-frame/reg-event-db
 :process-new-question
 []
 (fn
   [db [_ response]]                 ;; destructure the response from the event vector
   (let [submap        (get-in db [:questions])]
     (-> db
         (assoc  :loading?  false)     ;; take away that "Loading ..." UI
         (update :qform not)           ;; hide new question form
         (update-in [:questions] conj response)))))

;; -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)

;; (re-frame/reg-event-fx      ;; <-- note the `-fx` extension
;;  :create-question           ;; <-- the event id
;;  (fn                         ;; <-- our handler function
;;    [cofx [dispatch-name question]]      ;; <-- 1st argument is coeffect, from which we extract db
;;    (let [test-id    (.-value (gdom/getElement "test-id"))
;;          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
;;      ;; we return a map of (side) effects
;;      {:http-xhrio {:method          :post
;;                    :uri             "/api/createquestion"
;;                    :format          (ajax/json-request-format)
;;                    :params          question
;;                    :headers         {"x-csrf-token" csrf-field}
;;                    :response-format (ajax/json-response-format {:keywords? true})
;;                    :on-success      [:process-new-question]
;;                    :on-failure      [:bad-response]}})))

;; -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)
(re-frame/reg-event-fx
  :create-question
  (fn                      ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    ;; question hint explanation qtype test-id user-id active
    (let [values        (second _)
          question      (:question values)
          hint          (:hint values)
          explanation   (:explanation values)
          qtype         (:qtype values)
          user-id       (:user-id values)
          pre-test-id   (:test-id values)
          test-id       (js/parseInt pre-test-id)
          mutation      (gstring/format "mutation { add_question(question: \"%s\", hint: \"%s\", explanation: \"%s\",
                                         qtype: %i, test_id: %i, user_id: %i) { id question qtype hint explanation}}"
                                        question hint explanation qtype test-id user-id)]
      ;; perform a query, with the response sent to the callback event provided
      (re-frame/dispatch [::re-graph/mutate
                          mutation                           ;; graphql query
                          {:some "Pumas prros!! variable"}   ;; arguments map
                          [:process-question-response]]))))


(re-frame/reg-event-db
 :process-after-delete-question
 []
 (fn
   [db [_ question-id]]
   (let [submap  (get-in db [:questions])]
     (-> db
         (update-in [:questions] dissoc (keyword (str question-id)))
         (update  :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-question            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id question-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> dispatch-id   OOO>>>>> " dispatch-id "  >>>> " question-id))
   (when (js/confirm "Delete question?")
     (let [db               (:db cofx)
           question-id-int  (js/parseInt question-id)
           test-id          (.-value (gdom/getElement "test-id"))
           test-id-int      (js/parseInt test-id)
           csrf-field       (.-value (gdom/getElement "__anti-forgery-token"))]
       ;; we return a map of (side) effects
       {:http-xhrio {:method          :delete
                     :uri             "/admin/tests/deletequestion"
                     :format          (ajax/json-request-format)
                     :params          {:question-id question-id-int :test-id test-id-int}
                     :headers         {"x-csrf-token" csrf-field}
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [:process-after-delete-question question-id]
                     :on-failure      [:bad-response]}}))))

(re-frame/reg-event-db
 :process-new-answer
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (.log js/console (str ">>> New answer response from Luminus >>>>> " response))
   (.log js/console (str ">>> Full DB >>>>> " db))
   (let [answer      (second (first response))
         _           (.log js/console (str ">>> WWQQQQQQQ  ANSWER >>>>> " answer))
         qid         (:question_id answer)
         _           (.log js/console (str ">>> QID >>>>> " qid))
         question-id (keyword (str qid))
         _           (.log js/console (str ">>> VALUE KEYWORD question-id >>>>> " question-id ))
         _           (.log js/console (str ">>> QUESTION ID >>>>>  qid: " qid "   question-id: " question-id))]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI
         (update-in [:questions question-id :answers] conj response)))))

(re-frame/reg-event-fx
 :create-answer
 (fn
   [cfx [_ answer]]      ;; <-- 1st argument is coeffect, from which we extract db
   (let [csrf-field  (.-value (gdom/getElement "__anti-forgery-token"))
         _           (.log js/console (str ">>> answer AT create-answer >>>>> " answer))]
     ;; we return a map of (side) effects
     {:http-xhrio {:method          :post
                   :uri             "/api/createanswer"
                   :format          (ajax/json-request-format)
                   :params          answer
                   :headers         {"x-csrf-token" csrf-field}
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:process-new-answer]
                   :on-failure      [:bad-response]}})))

(re-frame/reg-event-db
 :process-after-delete-answer
 []
 (fn
   [db [_ response]]
   (let [_           (.log js/console (str ">>> RESPONSE AFTER DELETE ANSWER  >>>>> " response ))
         answer      (:response response)
         question-id (keyword (str (:question-id answer)))
         answer-id   (keyword (str (:answer-id answer)))]
     (-> db
         (update-in [:questions question-id :answers] dissoc answer-id)
         (update :loading? not)))))

(re-frame/reg-event-fx        ;; <-- note the `-fx` extension
 :delete-answer               ;; <-- the event id
 (fn                           ;; <-- the handler function
   [cofx [_ data]]       ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> Delete  answer data >>>>> " data))
   (when (js/confirm "Delete answer?")
     (let [db           (:db cofx)
           answer-id    (:answer-id data)
           question-id  (:question-id data)
           csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
       ;; we return a map of  v(side) effects
       {:http-xhrio {:method          :delete
                     :uri             "/admin/tests/deleteanswer"
                     :format          (ajax/json-request-format)
                     :params          {:answer-id answer-id :question-id question-id}
                     :headers         {"x-csrf-token" csrf-field}
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [:process-after-delete-answer]
                     :on-failure      [:bad-response]}}))))

(re-frame/reg-event-db
 :process-after-update-question
 []
 (fn
   [db [_ response]]
   (let [qkeyword  (keyword (str (:id response)))]
     (-> db
         (update-in [:questions qkeyword] conj response)
         (update :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-question           ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ question]]      ;; <-- 1st argument is coeffect, from which we extract db
    (let [db         (:db cofx)
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
      ;; we return a map of (side) effects
      {:http-xhrio {:method          :post
                    :uri             "/admin/tests/updatequestion"
                    :format          (ajax/json-request-format)
                    :params          question
                    :headers         {"x-csrf-token" csrf-field}
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:process-after-update-question]
                    :on-failure      [:bad-response]}})))

;; ### UPDATE ANSWER
(re-frame/reg-event-db
 :process-after-update-answer
 []
 (fn [db [_ response]]
   (let [answer-keyword    (keyword (str (:id response)))
         question-keyword  (keyword (str (:question_id response)))]
       (-> db
          (update-in [:questions question-keyword :answers answer-keyword] conj response)
          (update :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-answer             ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ answer]]        ;; <-- 1st argument is coeffect, from which we extract db
    (let [db         (:db cofx)
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
      ;; we return a map of (side) effects
      {:http-xhrio {:method          :post
                    :uri             "/admin/tests/updateanswer"
                    :format          (ajax/json-request-format)
                    :params          answer
                    :headers         {"x-csrf-token" csrf-field}
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:process-after-update-answer]
                    :on-failure      [:bad-response]}})))

;; ### UPDATE ANSWER
(re-frame/reg-event-db
 :process-after-update-test
 []
 (fn [db [_ response]]
   (-> db
       (assoc :test response)
       (update :loading?  not)
       (update :testform  not))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
  :update-test               ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ updates]]        ;; <-- 1st argument is coeffect, from which we extract db
    (let [db         (:db cofx)
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
      ;; we return a map of (side) effects
      {:http-xhrio {:method          :post
                    :uri             "/admin/tests/updatetest"
                    :format          (ajax/json-request-format)
                    :params          updates
                    :headers         {"x-csrf-token" csrf-field}
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:process-after-update-test]
                    :on-failure      [:bad-response]}})))
