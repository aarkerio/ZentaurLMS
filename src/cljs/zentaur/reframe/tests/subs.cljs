(ns zentaur.reframe.tests.subs ^{:doc "Re-frame Subscriptions"}
  (:require [re-frame.core :as re-frame]))  ;; [reg-sub subscribe]

;; Subscribers automatically subscribe data from the global state and re-render on change.
;;
;;  subscribe within a renderer view:    [:div  @(subscribe [:a])]  subscribe and dereference a subscription in one go.
;; -------------------------------------------------------------------------------------
;; Layer 2
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/SubscriptionInfographic.md
;;
;; Layer 2 query functions are "extractors". They take from `app-db`
;; and don't do any further computation on the extracted values. Any further
;; computation should happen in Layer 3.
;; Why?  It is an efficiency thing. Every Layer 2 subscription will rerun any time
;; that `app-db` changes (in any way). As a result, we want Layer 2 to be trivial.
;;
(re-frame/reg-sub
  :showing             ;; usage:   (subscribe [:showing])
  (fn [db _]            ;; db is the (map) value stored in the app-db atom
    (:showing db)))    ;; extract a value from the application state

;; Next, the registration of a similar handler is done in two steps.
;; First, we `defn` a pure handler function.  Then, we use `reg-sub` to register it.
;; Two steps. This is different to that first registration, above, which was done
;; in one step using an anonymous function.
;; -------------------------------------------------------------------------------------
;; Layer 3
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/SubscriptionInfographic.md
;;
;; A subscription handler is a function which is re-run when its input signals
;; change. Each time it is rerun, it produces a new output (return value).
;;
;; In the simple case, app-db is the only input signal, as was the case in the two
;; simple subscriptions above. But many subscriptions are not directly dependent on
;; app-db, and instead, depend on a value derived from app-db.
;;
;; Such handlers represent "intermediate nodes" in a signal graph.  New values emanate
;; from app-db, and flow out through a signal graph, into and out of these intermediate
;; nodes, before a leaf subscription delivers data into views which render data as hiccup.
;;
;; When writing and registering the handler for an intermediate node, you must nominate
;; one or more input signals (typically one or two).
;;
;; reg-sub allows you to supply:
;;
;;   1. a function which returns the input signals. It can return either a single signal or
;;      a vector of signals, or a map where the values are the signals.
;;
;;   2. a function which does the computation. It takes input values and produces a new
;;      derived value.
;;
;; In the two simple examples at the top, we only supplied the 2nd of these functions.
;; But now we are dealing with intermediate (layer 3) nodes, we'll need to provide both fns.
;;
;; -------------------------------------------------------------------------------------
;; Hey, wait on!!
;;
;; How did those two simple Layer 2 registrations at the top work?
;; We only supplied one function in those registrations, not two?
;; Very observant of you, I'm glad you asked.
;; When the signal-returning-fn is omitted, reg-sub provides a default,
;; and it looks like this:
;;    (fn [_ _]
;;       re-frame.db/app-db)
;; It returns one signal, and that signal is app-db itself.
;;
;; So the two simple registrations at the top didn't need to provide a signal-fn,
;; because they operated only on the value in app-db, supplied as 'db' in the 1st argument.
;;
;; So that, by the way, is why Layer 2 subscriptions always re-calculate when `app-db`
;; changes - `app-db` is literally their input signal.

;; -------------------------------------------------------------------------------------
;; SUGAR ?
;; Now for some syntactic sugar...
;; The purpose of the sugar is to remove boilerplate noise. To distill to the essential in 90% of cases.
;; Because it is so common to nominate 1 or more input signals, reg-sub provides some macro sugar so you can nominate a very minimal
;; vector of input signals. The 1st function is not needed. Here is the example above rewritten using the sugar.

;; My new subscription functions
(re-frame/reg-sub
 :test
 (fn [db]
   (:test db)))

(re-frame/reg-sub
 :questions
 (fn [db]
   (get-in db [:questions])))

(re-frame/reg-sub
 :subjects
 (fn [db]
   (get-in db [:subjects])))

(re-frame/reg-sub
 :qform
 (fn [db]
   (get-in db [:qform])))

(re-frame/reg-sub
 :toggle-testform
 (fn [db]
   (get-in db [:testform])))


