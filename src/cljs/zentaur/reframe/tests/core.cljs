(ns zentaur.reframe.tests.core
  (:require [cljs.core.async :refer [<! chan close!]]
            [cljs-http.client :as http]
            [cljs.loader :as loader]
            [goog.dom :as gdom]
            [goog.events :as events]
            [reagent.core :as r]
            [re-frame.core :as reframe]       ;; [dispatch dispatch-sync]]
            [secretary.core :as secretary]
            [zentaur.reframe.tests.events]    ;; These two are only required to make the compiler
            [zentaur.reframe.tests.subs]      ;; my subscriptions
            [zentaur.reframe.tests.views :as zviews])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [secretary.core :refer [defroute]])
  (:import [goog History]
           [goog.history EventType]))

;; Put an initial value into app-db.
;; The event handler for `:initialise-db` can be found in `events.cljs`
;; Using the sync version of dispatch means that value is in
;; place before we go onto the next step.
;; (reframe/dispatch-sync [:initialise-db])

;; -- Routes and History ------------------------------------------------------
;; Although we use the secretary library below, that's mostly a historical
;; accident. You might also consider using:
;;   - https://github.com/DomKM/silk
;;   - https://github.com/juxt/bidi
;; We don't have a strong opinion.
;;
(defroute "/" [] (reframe/dispatch [:set-showing :all]))
(defroute "/:filter" [filter] (reframe/dispatch [:set-showing (keyword filter)]))

(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn ^:export main
  []
  (when-let [hform (gdom/getElement "test-root-app")]
    (.log js/console (str ">>> ******************* HFORM >>>>> " (.stringify js/JSON hform)))
    (reframe/dispatch-sync [:request-test])  ;; <--- boot process is started. Synchronously initialised *before*
    (r/render [zviews/todo-app]
              (.getElementById js/document "test-root-app"))))
(main)

