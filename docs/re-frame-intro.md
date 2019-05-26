
# Introduction to Re-frame

You already know CLJS and Reagent.

Domino steps:

1) Events: all starts with a Dispatch:

     ̣`(reframe/dispatch-sync [:request-test])`

2) Handler effect, handlers change state, that's their job:

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
                         :on-success      [:process-test-response]   ;; <<<----- reg-event-db
                         :on-failure      [:bad-response]}
            :db (assoc db :loading? true)})))


3) Effect Handlers, event DB:

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


4) Subscriptions

    (reframe/reg-sub
    :test
    (fn [db]
    (:test db)))

    (reframe/reg-sub
    :questions
    (fn [db]
    (get-in db [:questions])))

5) View

     (defn questions-list
       []
       (let [counter (atom 0)]
         (fn []
           [:section {:key (str "question-list-key-" @counter) :id (str "question-list-key-" @counter)}
            (for [question @(re-frame/subscribe [:questions])]
              [question-item (second (assoc-in question [1 :key] (swap! counter inc)))])])))


