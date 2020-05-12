(ns zentaur.reframe.quotes.quotes-events
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.tests.db :as zdb]
            [zentaur.reframe.tests.libs :as libs]))


; -- First Interceptor ----
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (re-frame/after (partial check-and-throw :zentaur.reframe.tests.db/db)))  ;; PARTIAL: a way to currying

;; We now create the interceptor chain shared by all event handlers which manipulate todos.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def test-interceptors [check-spec-interceptor])

;;;;;;;;    BLOG COMMENTS  SECTION  ;;;;;;;;

(re-frame/reg-event-db
 :load-quotes-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-comments (:load_quotes data)
          _            (.log js/console (str ">>> comments PRE-RESPONSE >>>>> " data))
          comments     (:quotes pre-comments)
          _            (.log js/console (str ">>> comments RESPONSE >>>>> " comments))]
         (assoc db :quotes quotes))))

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :load-quotes
  (fn                      ;; <-- the handler function
    [cfx [_ updates]]     ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [query   (gstring/format "{load_quotes {quotes {comment username created_at}}}"
                                  post-id)]
      (.log js/console (str ">>> QUEERY  >>>>> " query ))
      (re-frame/dispatch [::re-graph/query query {} [:load-quotes-response]]))))

(re-frame/reg-event-db
 :process-save-quote
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (let [comment     (:create_quote (second (first response)))
         _           (.log js/console (str ">>> comment QQ >>>>> " comment))]
         (update-in db [:comments] conj comment))))

(re-frame/reg-event-fx
  :save-quote
  (fn                    ;; <-- the handler function
    [cfx _]            ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    (let [updates (second _)
          {:keys [post-id comment user-id]} updates
          mutation    (gstring/format "mutation { create_comment( post_id: %i, comment: \"%s\", user_id: %i)
                                      { username comment created_at }}"
                                      post-id comment user-id)]
      (.log js/console (str ">>> CREATE ANSWER MUTATION >>>>> " mutation ))
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-save-quote]]))))

