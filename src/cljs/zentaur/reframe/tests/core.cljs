(ns zentaur.reframe.tests.core
  (:require [cljs.core.async :refer [<! chan close!]]
            [cljs-http.client :as http]
            [cljs.loader :as loader]
            [goog.dom :as gdom]
            [goog.events :as events]
            [reagent.core :as r]
            [re-frame.core :as re-frame]
            [zentaur.reframe.tests.events]    ;; These two are only required to make the compiler
            [zentaur.reframe.tests.subs]      ;; my subscriptions
            [zentaur.reframe.tests.views :as zviews])
  (:require-macros [cljs.core.async.macros :as m :refer [go]])
  (:import [goog History]
           [goog.history EventType]))

;; Put an initial value into app-db.
;; The event handler for `:initialise-db` can be found in `events.cljs`
;; Using the sync version of dispatch means that value is in
;; place before we go onto the next step.
;; (reframe/dispatch-sync [:initialise-db])

;; (def history
;;   (doto (History.)
;;     (events/listen EventType.NAVIGATE
;;                    (fn [event] (secretary/dispatch! (.-token event))))
;;     (.setEnabled true)))

(defn ^:export main
  []
  (when-let [hform (gdom/getElement "test-root-app")]
    (re-frame/dispatch-sync [:request-test])  ;; <--- boot process is started. Synchronously initialised *before*
    (r/render [zviews/todo-app]
              (.getElementById js/document "test-root-app"))))
(main)

