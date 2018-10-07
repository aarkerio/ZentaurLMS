(ns zentaur.events
  (:require [ajax.core :as ajax]
            [cljs.spec.alpha :as s]
            [day8.re-frame.async-flow-fx]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [re-frame.core :as reframe]    ;; [reg-event-db reg-event-fx inject-cofx path after]]
            [zentaur.db    :as zdb]))

;; -- Interceptors --------------------------------------------------------------
;;
;; Interceptors are a more advanced topic. So, we're plunging into the deep
;; end here.
;;
;; In re-frame, the forwards Interceptors sweep progressively creates the coeffects (inputs to the event handler),
;; while the backwards sweep processes the effects (outputs from the event handler).
;; There is a tutorial on Interceptors in re-frame's `/docs`, but to get
;; you going fast, here's a very high level description ...
;;
;; Every event handler can be "wrapped" in a chain of interceptors. A
;; "chain of interceptors" is actually just a "vector of interceptors". Each
;; of these interceptors can have a `:before` function and an `:after` function.
;; Each interceptor wraps around the "handler", so that its `:before`
;; is called before the event handler runs, and its `:after` runs after
;; the event handler has run.
;;
;; Interceptors with a `:before` action, can be used to "inject" values
;; into what will become the `coeffects` parameter of an event handler.
;; That's a way of giving an event handler access to certain resources,
;; like values in LocalStore.
;;
;; Interceptors with an `:after` action, can, among other things,
;; process the effects produced by the event handler. One could
;; check if the new value for `app-db` correctly matches a Spec.
;;

;; -- First Interceptor ------------------------------------------------------
;;
;; Event handlers change state, that's their job. But what happens if there's
;; a bug in the event handler and it corrupts application state in some subtle way?
;; Next, we create an interceptor called `check-spec-interceptor`.
;; Later, we use this interceptor in the interceptor chain of all event handlers.
;; When included in the interceptor chain of an event handler, this interceptor
;; runs `check-and-throw` `after` the event handler has finished, checking
;; the value for `app-db` against a spec.
;; If the event handler corrupted the value for `app-db` an exception will be
;; thrown. This helps us detect event handler bugs early.
;; Because all state is held in `app-db`, we are effectively validating the
;; ENTIRE state of the application after each event handler runs.  All of it.

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (reframe/after (partial check-and-throw :zentaur.db/db)))  ;; PARTIAL: (def hundred-times (partial * 100))

;; -- Second Interceptor -----------------------------------------------------
;;
;; Part of the Zentaur challenge is to store todos in local storage.
;; Next, we define an interceptor to help with this challenge.
;; This interceptor runs `after` an event handler, and it stores the
;; current todos into local storage.
;; Later, we include this interceptor into the interceptor chain
;; of all event handlers which modify todos. In this way, we ensure that
;; every change to todos is written to local storage.
(def ->local-store (reframe/after zdb/todos->local-store))

;; -- Interceptor Chain ------------------------------------------------------
;;
;; Each event handler can have its own chain of interceptors.
;; We now create the interceptor chain shared by all event handlers which manipulate todos.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def todo-interceptors [check-spec-interceptor    ;; ensure the spec is still valid  (after)
                        (reframe/path :todos)     ;; the 1st param given to handler will be the value from this path within db
                        ->local-store])           ;; write todos to localstore  (after)

;; -- Helpers -----------------------------------------------------------------

(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [todos]
  ((fnil inc 0) (last (keys todos))))

;; -- Event Handlers ----------------------------------------------------------

;; usage:  (dispatch [:initialise-db])
;;
;; This event is dispatched in the app's `main` (core.cljs).
;; It establishes initial application state in `app-db`.
;; That means merging:
;;   1. Any todos stored in LocalStore (from the last session of this app)
;;   2. Default initial values
;;
;; Advanced topic:  we inject the todos currently stored in LocalStore
;; into the first, coeffect parameter via use of the interceptor
;;    `(inject-cofx :local-store-todos)`
;;
;; To fully understand this advanced topic, you'll have to read the tutorials
;; and look at the bottom of `db.cljs` for the `:local-store-todos` cofx
;; registration.
(reframe/reg-event-fx         ;; part of the re-frame API
 :initialise-db              ;; event id being handled

  ;; the interceptor chain (a vector of 2 interceptors in this case)
 [(reframe/inject-cofx :local-store-todos)        ;; gets todos from localstore, and puts value into coeffects arg
  check-spec-interceptor                          ;; after event handler runs, check app-db for correctness. Does it still match Spec?
  (.log js/console (str ">>> >>>I'm the initial Interceptor!!! "))]

  ;; the event handler (function) being registered
  (fn [{:keys [db local-store-todos]} _]                       ;; take 2 values from coeffects. Ignore event vector itself.
    {:db (assoc zdb/default-db :todos local-store-todos)}))   ;; all hail the new state to be put in app-db

;; usage:  (dispatch [:set-showing  :active])
;; This event is dispatched when the user clicks on one of the 3
;; filter buttons at the bottom of the display.
(reframe/reg-event-db      ;; part of the re-frame API
  :set-showing             ;; event-id

  ;; only one interceptor
  [check-spec-interceptor]       ;; after event handler runs, check app-db for correctness. Does it still match Spec?

  ;; handler
  (fn [db [_ new-filter-kw]]     ;; new-filter-kw is one of :all, :active or :done
    (assoc db :showing new-filter-kw)))

;; NOTE: below is a rewrite of the event handler (above) using a `path` Interceptor
;; You'll find it illuminating to compare this rewrite with the original.
;;
;; A `path` interceptor has BOTH a before and after action.
;; When you create one, you supply "a path" into `app-db`, like:
;; [:a :b 1]
;; The job of "before" is to replace the app-db with the value
;; of `app-db` at the nominated path. And, then, "after" to
;; take the event handler returned value and place it back into
;; app-db at the nominated path.  So the event handler works
;; with a particular, narrower path within app-db, not all of it.
;;
;; So, `path` operates a little like `update-in`
;;
#_(reframe/reg-event-db
  :set-showing

  ;; this now a chain of 2 interceptors. Note use of `path`
  [check-spec-interceptor (reframe/path :showing)]

  ;; The event handler
  ;; Because of the `path` interceptor above, the 1st parameter to
  ;; the handler below won't be the entire 'db', and instead will
  ;; be the value at the path `[:showing]` within db.
  ;; Equally the value returned will be the new value for that path
  ;; within app-db.
  (fn [old-showing-value [_ new-showing-value]]
    new-showing-value))                  ;; return new state for the path

;; ######  reg-event-db, delivers ONLY the coeffect db (partial of the current state of the world) to the event handler

;; usage:  (dispatch [:add-todo  "a description string"])
(reframe/reg-event-db                     ;; given the text, create a new todo
  :add-todo

  ;; Use the standard interceptors, defined above, which we
  ;; use for all todos-modifying event handlers. Looks after
  ;; writing todos to LocalStore, etc.
  todo-interceptors

  ;; The event handler function.
  ;; The "path" interceptor in `todo-interceptors` means 1st parameter is the
  ;; value at `:todos` path within `db`, rather than the full `db`.
  ;; And, further, it means the event handler returns just the value to be
  ;; put into the `[:todos]` path, and not the entire `db`.
  ;; So, againt, a path interceptor acts like clojure's `update-in`
  (fn [todos [_ text]]
    (let [id (allocate-next-id todos)]
      (assoc todos id {:id id :title text :done false}))))

(reframe/reg-event-db
  :toggle-done
  todo-interceptors
  (fn [todos [_ id]]
    (update-in todos [id :done] not)))

(reframe/reg-event-db
  :toggle-question
  (fn [questions [_ id]]
    (update-in questions [id :done] not)))

(reframe/reg-event-db
  :save
  todo-interceptors
  (fn [todos [_ id title]]
    (assoc-in todos [id :title] title)))

(reframe/reg-event-db
  :delete-todo
  todo-interceptors
  (fn [todos [_ id]]
    (dissoc todos id)))

(reframe/reg-event-db
  :clear-completed
  todo-interceptors  ;; function
  (fn [todos _]
    (let [done-ids (->> (vals todos)         ;; which todos have a :done of true
                        (filter :done)
                        (map :id))]
      (reduce dissoc todos done-ids))))      ;; delete todos which are done

(reframe/reg-event-db
  :complete-all-toggle
  todo-interceptors
  (fn [todos _]
    (let [new-done (not-every? :done (vals todos))]   ;; work out: toggle true or false?
      (reduce #(assoc-in %1 [%2 :done] new-done)
              todos
              (keys todos)))))

(reframe/reg-event-db
 :toggle-qform
 (fn [db _]
   (update db :qform not)))

;; My new event handler
(reframe/reg-event-db
 :count-update
 todo-interceptors
 (fn [db [_ on-change]]                ;; First argument: coeffects map which contains the current state of the world (including app state)
   (update db :count on-change)))     ;; Second argument the event to handle

(defn re-order [my-map]
  (into {} (sort-by (comp :ordnen val) my-map)))

;; AJAX handlers
(reframe/reg-event-db
  :process-response
  (fn
    [db [_ response]]               ;; destructure the response from the event vector
    (.log js/console (str ">>> QS >>>>> " (:questions response)))
    (-> db
        (assoc :loading?  false)     ;; take away that "Loading ..." UI
        (assoc :test      (js->clj response))
        (assoc :questions (js->clj (:questions response))))))

(reframe/reg-event-db
 :bad-response
 (fn
   [db [_ response]]
   (.log js/console (str ">>> ERROR in ajax response: >>>>> " response "   " _))))

;; reg-event-fx == event handler's coeffects

(reframe/reg-event-fx    ;; <-- note the `-fx` extension
  :request-test          ;; <-- the event id
  (fn                     ;; <-- the handler function
    [cfx _]              ;; <-- 1st argument is coeffect, from which we extract db
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
                    :on-success      [:process-response]
                    :on-failure      [:bad-response]}
       :db (assoc db :loading? true)})))

;; AJAX handlers
(reframe/reg-event-db
 :process-new-question
 (fn
   [db [_ response]]               ;; destructure the response from the event vector
   (.log js/console (str ">>> New question response >>>>> " response))
   (-> db
       (assoc  :loading?  false)     ;; take away that "Loading ..." UI
       (update :qform not)
       (assoc-in [:questions (:id response)] response))))

;; -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)

(reframe/reg-event-fx        ;; <-- note the `-fx` extension
 :create-question           ;; <-- the event id
 (fn                         ;; <-- the handler function
   [cofx [_ question]]      ;; <-- 1st argument is coeffect, from which we extract db
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

(reframe/reg-event-db
 :process-after-delete-question
 (fn
   [db [_ question-id]]
   (-> db
       (update-in [:questions] dissoc (keyword (str question-id)))
       (update  :loading?  not))))

(reframe/reg-event-fx        ;; <-- note the `-fx` extension
 :delete-question           ;; <-- the event id
 (fn                         ;; <-- the handler function
   [cofx [_ question-id]]      ;; <-- 1st argument is coeffect, from which we extract db
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

(reframe/reg-event-fx        ;; <-- note the `-fx` extension
  :update-questions          ;; <-- the event id
  (fn                         ;; <-- the handler function
    [cofx [_ question]]      ;; <-- 1st argument is coeffect, from which we extract db
    (.log js/console (str ">>>   _____________  ___  >>>>>   " _))
    (.log js/console (str ">>>   QUUUUUUUUUUUUUESTION    >>>>>   " question))
    (let [db         (:db cofx)
          test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]

      ;; we return a map of (side) effects
      {:http-xhrio {:method          :post
                    :uri             "/admin/tests/updatequestion"
                    :format          (ajax/json-request-format)
                    :params          question
                    :headers         {"x-csrf-token" csrf-field}
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:process-response]
                    :on-failure      [:bad-response]}
       :db (update db :qform not)})))

(defn boot-flow
  []
  {:first-dispatch [:do-X]              ;; what event kicks things off ?
   :rules [                             ;; a set of rules describing the required flow
           {:when :seen? :events :success-X  :dispatch [:do-Y]}
           {:when :seen? :events :success-Y  :dispatch [:do-Z]}
           {:when :seen? :events :success-Z  :halt? true}
           {:when :seen-any-of? :events [:fail-X :fail-Y :fail-Z] :dispatch  [:app-failed-state] :halt? true}]})

(reframe/reg-event-fx            ;; note the -fx == coeffects world
  :boot                          ;; usage:  (dispatch [:boot])  See step 3
  (fn [_ _]
    {:db (-> {}                  ;;  do whatever synchronous work needs to be done
            "task1-fn"             ;; ?? set state to show "loading" twirly for user??
            "task2-fn")            ;; ?? do some other simple initialising of state
     :async-flow  (boot-flow)})) ;; kick off the async process
