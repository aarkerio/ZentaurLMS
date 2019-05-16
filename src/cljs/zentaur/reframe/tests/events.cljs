(ns zentaur.reframe.tests.events
  (:require [ajax.core :as ajax]
            [cljs.spec.alpha :as s]
            [day8.re-frame.async-flow-fx]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [re-frame.core :as re-frame]
            [zentaur.reframe.tests.db :as zdb]))

;; -- Check Interceptor ------------------------------------------------------
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

;; My new event DB handlers
(re-frame/reg-event-db
 :process-test-response
 [reorder-event]
 (fn
   [db [_ response]]               ;; destructure the response from the event vector
   (.log js/console (str ">>> First Call >>>>> " (:questions response)))
   (-> db
       (assoc :loading?  false)     ;; take away that "Loading ..." UI
       (assoc :test      (js->clj response))
       (assoc :questions (js->clj (:questions response))))))

(re-frame/reg-event-db
 :bad-response
 (fn
   [db [_ response]]
   (.log js/console (str ">>> ERROR in ajax response: >>>>> " response "   " _))))

(re-frame/reg-event-db
 :process-new-answer
 (fn
   [db [_ response]]               ;; destructure the response from the event vector
   (.log js/console (str ">>> New answer response >>>>> " response))
   (let [qkeyword     (keyword (str (:question_id response)))
         _            (.log js/console (str ">>> qkeyword >>>>> " qkeyword))
         submap       (get-in db [:questions qkeyword :answers])
         _            (.log js/console (str ">>> SUBMAP >>>>> " submap))
         modified     (conj submap response)
         _            (.log js/console (str ">>> Modified >>>>> " modified))]
     (-> db
         (assoc  :loading?  false)     ;; take away that "Loading ..." UI
         (update-in [:questions qkeyword :answers] conj response)))))

;;;;;;;;    CO-EFFECT HANDLERS (with Ajax!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect

(re-frame/reg-event-fx    ;; <-- note the `-fx` extension
  :request-test           ;; <-- the event id
  (fn                      ;; <-- the handler function
    [cfx _]               ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [db         (:db cfx)
          test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]

      ;; we return a map of (side) effects
      {:http-xhrio {:method          :post
                    :uri             "/admin/tests/load"
                    :format          (ajax/json-request-format)
                    :params          {:test-id test-id}
                    :headers         {"x-csrf-token" csrf-field}
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:process-test-response]  ;; <<----
                    :on-failure      [:bad-response]}
       :db (assoc db :loading? true)})))

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

;; -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)

(re-frame/reg-event-fx      ;; <-- note the `-fx` extension
 :create-question           ;; <-- the event id
 (fn                         ;; <-- our handler function
   [cofx [dispatch-name question]]      ;; <-- 1st argument is coeffect, from which we extract db
   (let [db         (:db cofx)
         test-id    (.-value (gdom/getElement "test-id"))
         csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
     ;; we return a map of (side) effects
     {:http-xhrio {:method          :post
                   :uri             "/admin/tests/createquestion"
                   :format          (ajax/json-request-format)
                   :params          question
                   :headers         {"x-csrf-token" csrf-field}
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:process-new-question]
                   :on-failure      [:bad-response]}})))

(re-frame/reg-event-db
 :process-after-delete-question
 [reorder-after-interceptor]
 (fn
   [db [_ question-id]]
   (-> db
       (update-in [:questions] dissoc (keyword (str question-id)))
       (update  :loading?  not))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-question            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id question-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> dispatch-id >>>>> " dispatch-id))
   (when (js/confirm "Delete question?")
    (let [db         (:db cofx)
          test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
        ;; we return a map of (side) effects
        {:http-xhrio {:method          :post
                      :uri             "/admin/tests/deletequestion"
                      :format          (ajax/json-request-format)
                      :params          {:question-id question-id :test-id test-id}
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
 (fn                          ;; <-- the handler function
   [cofx [_ answer-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> Delete  answer-id >>>>> " answer-id))
   (when (js/confirm "Delete answer?")
    (let [db         (:db cofx)
          test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
        ;; we return a map of (side) effects
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

;; #############  FLOW (later)

(defn boot-flow
  []
  {:first-dispatch [:do-X]              ;; what event kicks things off ?
   :rules [                             ;; a set of rules describing the required flow
           {:when :seen? :events :success-X  :dispatch [:do-Y]}
           {:when :seen? :events :success-Y  :dispatch [:do-Z]}
           {:when :seen? :events :success-Z  :halt? true}
           {:when :seen-any-of? :events [:fail-X :fail-Y :fail-Z] :dispatch  [:app-failed-state] :halt? true}]})

(re-frame/reg-event-fx            ;; note the -fx == coeffects world
  :boot                          ;; usage:  (dispatch [:boot])  See step 3
  (fn [_ _]
    {:db (-> {}                  ;;  do whatever synchronous work needs to be done
            "task1-fn"             ;; ?? set state to show "loading" twirly for user??
            "task2-fn")            ;; ?? do some other simple initialising of state
     :async-flow  (boot-flow)})) ;; kick off the async process

