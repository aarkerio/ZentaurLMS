(ns zentaur.reframe.tests.db
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

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

;; My spec
(s/def ::id int?)
(s/def ::question string?)
(s/def ::qtype int?)
(s/def ::questions (s/keys :req-un [::id ::question ::qtype]))  ;; :req-un and :opt-un for "required" and "optional" unqualified keys
(s/def ::question-counter int?)
(s/def ::qform boolean?)

(s/def ::test (s/and                                        ;; should use the :kind kw to s/map-of (not supported yet)
                 (s/map-of ::id ::question)                 ;; in this map, each todo is keyed by its :id
                 #(instance? PersistentTreeMap %)           ;; is a sorted-map (not just a map)
                 ))

(s/def ::db (s/keys :req-un [::questions]))

;; -- Default app-db Value  ---------------------------------------------------
;;
;; When the application first starts, this will be the value put in app-db
;; Unless, of course, there are todos in the LocalStore (see further below)
;; Look in:
;;   1.  `core.cljs` for  "(dispatch-sync [:initialise-db])"
;;   2.  `events.cljs` for the registration of :initialise-db handler
;;

(def default-db             ;; what gets put into app-db in the initial load.
  {:test          (sorted-map)
   :questions     (sorted-map)
   :loading?      false
   :qform         false
   :qcounter      0
   :testform      false
   :subjects      (vector)
   :levels        (vector)})

;; -- cofx Registrations  -----------------------------------------------------
;; Use `reg-cofx` to register a "coeffect handler" which will inject the todos
;; stored in localstore.
;;  ##### Coeffects is the current state of the world, as data, as presented to an event handler.
;;
;; To see it used, look in `events.cljs` at the event handler for `:request-test`.
;; That event handler has the interceptor `(inject-cofx :reorder-questions)`
;; The function registered below will be used to fulfill that request.
;;
;; We must supply a `sorted-map` but in LocalStore it is stored as a `map`.
;;
(rf/reg-cofx
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
