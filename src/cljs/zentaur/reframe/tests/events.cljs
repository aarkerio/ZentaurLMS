(ns zentaur.reframe.tests.events
  (:require [ajax.core :as ajax]
            [cljs.spec.alpha :as s]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.tests.db :as zdb]))

;; -- Check Interceptor (edit for subway) ------------------------------
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (re-frame/after (partial check-and-throw :zentaur.reframe.tests.db/db)))  ;; PARTIAL: (def hundred-times (partial * 100))

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

(def reorder-after-interceptor (re-frame/after (partial order-questions)))

;;;;;;;;;;;  REGISTER DB EVENT HANDLERS  ;;;;;;;;;;;;;;;;;;;;;;;;;;
(re-frame/reg-event-db
 :toggle-qform     ;; hidde/show forms
 (fn [db _]
   (update db :qform not)))

(re-frame/reg-event-db
  :process-test-response
  (fn [db [_ {:keys [data errors] :as payload}]]
    (let [full-data   (:questions_by_test data)
          questions   (:questions full-data)
          _           (.log js/console (str ">>> questions >>>>> " questions ))
          test        (:test full-data)]
     (-> db
         (assoc :loading?  false)     ;; take away that "Loading ..." UI element
         (assoc :test      (js->clj test :keywordize-keys true))
         (assoc :questions (js->clj questions :keywordize-keys true))))))

;;;;;;;;    CO-EFFECT HANDLERS (with Ajax!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect

(re-frame/reg-event-fx
  :request-test
  (fn                      ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [db            (:db cfx)
          pre-test-id   (.-value (gdom/getElement "test-id"))
          test-id       (js/parseInt pre-test-id)
          query         (gstring/format "{ questions_by_test(id: %i) { test {id title description tags created_at}
                                           questions { id question qtype explanation hint ordnen
                                           answers { id answer correct } }}}"
                                        test-id)]
          ;; perform a query, with the response sent to the callback event provided
          (re-frame/dispatch [::re-graph/query
                              query                              ;; graphql query
                              {:some "Pumas prros!! variable"}   ;; arguments map
                              [:process-test-response]])         ;; callback event when response is recieved
          )))

(re-frame/reg-event-db       ;; part of the re-frame API
 :initialise-db              ;; event id being handled
  ;; the interceptor chain (a vector of 2 interceptors in this case)
  ;; the event handler (function) being registered
 (fn [_ _]                       ;; take 2 values from coeffects. Ignore event vector itself.
   {:db (assoc zdb/default-db :questions (sorted-map) :question-counter 0)}))   ;; all hail the new state to be put in app-db

(defn bad-response
  "Generic response"
  [r]
  (prn "Bad response: >>> " r))

(re-frame/reg-event-fx
 :create-answer
 (fn
   [cfx [_ answer]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>>   NEW ANSWER  >>>>> " answer ))
   (let [csrf-field  (.-value (gdom/getElement "__anti-forgery-token"))]
     ;; we return a map of (side) effects
     {:http-xhrio {:method          :post
                   :uri             "/admin/tests/createanswer"
                   :format          (ajax/json-request-format)
                   :params          answer
                   :headers         {"x-csrf-token" csrf-field}
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:process-new-answer]
                   :on-failure      [:bad-response]}})))

;; AJAX handlers
(re-frame/reg-event-db
 :process-new-question
 [reorder-event]
 (fn
   [db [_ response]]               ;; destructure the response from the event vector
   (.log js/console (str ">>> New question response >>>>> " response))
   (-> db
       (assoc  :loading?  false)     ;; take away that "Loading ..." UI
       (update :qform not)           ;; hide new question form
       (assoc-in [:questions (:id response)] response))))

(re-frame/reg-event-db
 :process-question-response
 (fn [db [_ {:keys [data errors] :as payload}]]
   (let [question         (:add_question data)]
     (.log js/console (str ">>> process-question-response Dquestion OLE!! >>>>> " question ))
     (-> db
       ;;(update :question-counter 0)
       (update-in [:questions] conj question)))))

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
 [reorder-after-interceptor]
 (fn
   [db [_ question-id]]
   (.log js/console (str ">>> process-after-delete-question  >>>> " question-id))
   (-> db
       (update-in [:questions] dissoc (keyword (str question-id)))
       (update  :loading?  not))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-question            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id question-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> dispatch-id >>>>> " dispatch-id "  >>>> " question-id))
   (when (js/confirm "Delete question?")
     (let [db               (:db cofx)
           question-id-int  (js/parseInt question-id)
           test-id          (.-value (gdom/getElement "test-id"))
           test-id-int      (js/parseInt test-id)
           csrf-field       (.-value (gdom/getElement "__anti-forgery-token"))]
       ;; we return a map of (side) effects
       {:http-xhrio {:method          :post
                     :uri             "/admin/tests/deletequestion"
                     :format          (ajax/json-request-format)
                     :params          {:question-id question-id-int :test-id test-id-int}
                     :headers         {"x-csrf-token" csrf-field}
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      [:process-after-delete-question question-id]
                     :on-failure      [:bad-response]}}))))

(re-frame/reg-event-db
 :process-after-delete-answer
 [reorder-after-interceptor]
 (fn
   [db [_ question-id]]
   (-> db
       (update-in [:questions] dissoc (keyword (str question-id)))
       (update  :loading?  not))))

(re-frame/reg-event-fx        ;; <-- note the `-fx` extension
 :delete-answer               ;; <-- the event id
 (fn                           ;; <-- the handler function
   [cofx [_ answer-id]]       ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> Delete  answer-id >>>>> " answer-id))
   (when (js/confirm "Delete answer?")
    (let [db         (:db cofx)
          test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
        ;; we return a map of  v(side) effects
        {:http-xhrio {:method          :post
                      :uri             "/admin/tests/deletequestion"
                      :format          (ajax/json-request-format)
                      :params          {:answer-id answer-id}
                      :headers         {"x-csrf-token" csrf-field}
                      :response-format (ajax/json-response-format {:keywords? true})
                      :on-success      [:process-after-delete-answer answer-id]
                      :on-failure      [:bad-response]}}))))

(re-frame/reg-event-db
 :process-after-update-question
 [reorder-after-interceptor]
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
    (.log js/console (str ">>>   QUUUUUUUUUUUUUESTION    >>>>>   " question))
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
 [reorder-after-interceptor]
 (fn
   [db [_ response]]
   (let [qkeyword  (keyword (str (:id response)))]
     (.log js/console (str ">>> response answer >>>>> " response ))
     ; (-> db
     ;    (update-in [:questions qkeyword :answers] conj response)
     ;    (update :loading?  not))
     )))

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
                    :on-success      [:process-after-update-question]
                    :on-failure      [:bad-response]}})))


