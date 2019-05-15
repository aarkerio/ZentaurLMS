(ns zentaur.reframe.tests.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as reframe]))

;; ####### CONTEXT
;;  {:coeffects {:event [:some-id :some-param]
;;             :db    <original contents of app-db>}
;;
;;   :effects   {:db    <new value for app-db>
;;              :dispatch  [:an-event-id :param1]}
;;
;;   :queue     <a collection of further interceptors>
;;   :stack     <a collection of interceptors already walked>}

;; -- Spec --------------------------------------------------------------------
;;
;; This is a clojure.spec specification for the value in app-db. It is like a
;; Schema. See: http://clojure.org/guides/spec
;;
;; The value in app-db should always match this spec. Only event handlers
;; can change the value in app-db so, after each event handler
;; has run, we re-check app-db for correctness (compliance with the Schema).
;;
;; How is this done? Look in events.cljs and you'll notice that all handlers
;; have an "after" interceptor which does the spec re-check.
;;
;; None of this is strictly necessary. It could be omitted. But we find it
;; good practice.

(s/def ::id int?)
(s/def ::title string?)
(s/def ::done boolean?)
(s/def ::todo (s/keys :req-un [::id ::title ::done]))       ;; :req-un different from :opt-un
;; My spec
(s/def ::question string?)
(s/def ::qtype int?)
(s/def ::question (s/keys :req-un [::question ::qtype ::id]))  ;; :req-un and :opt-un for "required" and "optional" unqualified keys

(s/def ::test (s/and                                        ;; should use the :kind kw to s/map-of (not supported yet)
                 (s/map-of ::id ::question)                 ;; in this map, each todo is keyed by its :id
                 #(instance? PersistentTreeMap %)           ;; is a sorted-map (not just a map)
                 ))

(s/def ::todos (s/and                                       ;; should use the :kind kw to s/map-of (not supported yet)
                 (s/map-of ::id ::todo)                     ;; in this map, each todo is keyed by its :id
                 #(instance? PersistentTreeMap %)           ;; is a sorted-map (not just a map)
                 ))
(s/def ::showing                                            ;; what todos are shown to the user?
  #{:all                                                    ;; all todos are shown
    :active                                                 ;; only todos whose :done is false
    :done                                                   ;; only todos whose :done is true
    })
(s/def ::count int?)
(s/def ::db (s/keys :req-un [::todos ::showing ::count]))

;; -- Default app-db Value  ---------------------------------------------------
;;
;; When the application first starts, this will be the value put in app-db
;; Unless, of course, there are todos in the LocalStore (see further below)
;; Look in:
;;   1.  `core.cljs` for  "(dispatch-sync [:initialise-db])"
;;   2.  `events.cljs` for the registration of :initialise-db handler
;;

(def default-db             ;; what gets put into app-db by default.
  {:todos     (sorted-map)  ;; an empty list of todos. Use the (int) :id as the key
   :showing   :all          ;; show all todos
   :count     2
   :qform     false
   :test      (sorted-map)
   :questions (sorted-map)})

;; -- Local Storage  ----------------------------------------------------------
;;
;; Part of the todomvc challenge is to store todos in LocalStorage, and
;; on app startup, reload the todos from when the program was last run.
;; But the challenge stipulates to NOT load the setting for the "showing"
;; filter. Just the todos.
;;

(def ls-key "todos-reframe")                         ;; localstore key

(defn todos->local-store
  "Puts todos into localStorage"
  [todos]
  (.setItem js/localStorage ls-key (str todos)))     ;; sorted-map written as an EDN map

;; -- cofx Registrations  -----------------------------------------------------

;; Use `reg-cofx` to register a "coeffect handler" which will inject the todos
;; stored in localstore.
;;  ##### Coeffects is the current state of the world, as data, as presented to an event handler.
;;
;; To see it used, look in `events.cljs` at the event handler for `:initialise-db`.
;; That event handler has the interceptor `(inject-cofx :local-store-todos)`
;; The function registered below will be used to fulfill that request.
;;
;; We must supply a `sorted-map` but in LocalStore it is stored as a `map`.
;;
(reframe/reg-cofx
  :local-store-todos
  (fn [cofx _]
      ;; put the localstore todos into the coeffect under :local-store-todos
      (assoc cofx :local-store-todos
             ;; read in todos from localstore, and process into a sorted map
             (into (sorted-map)
                   (some->> (.getItem js/localStorage ls-key)
                            (cljs.reader/read-string)    ;; EDN map -> map  == Reads data in the edn format
                            )))))
(reframe/reg-cofx
  :reorder-questions
  (fn [cofx _]
    (let [questions (-> cofx :db :questions)]
      ;; put the localstore todos into the coeffect under :local-store-todos
      (assoc cofx :questions
             (into (sorted-map-by
                    (fn [key1 key2]
                      (compare (:ordnen (get questions key1))
                               (:ordnen (get questions key2)))))
                   questions)))))

;; (reframe/reg-cofx    ;; <-- note the `-fx` extension
;;   :backup-request-test      ;; <-- the event id
;;   (fn                 ;; <-- the handler function
;;     [cofx _]         ;; <-- 1st argument is coeffect, from which we extract db

;;     ;; we return a map of (side) effects
;;     {:http-xhrio {:method          :post
;;                   :uri             "/admin/tests/load"
;;                   :format          (ajax/json-request-format)
;;                   :response-format (ajax/json-response-format {:keywords? true})
;;                   :on-success      [:process-response]
;;                   :on-failure      [:bad-response]}
;;      :db  (assoc cofx :loading? true)}))
