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
    (let [pre-quotes (:load_quotes data)
          _          (.log js/console (str ">>> comments PRE-RESPONSE >>>>> " pre-quotes))
          quotes     (:quotes pre-quotes)
          _          (.log js/console (str ">>> comments RESPONSE  QUOTES >>>>> " quotes))]
         (assoc db :quotes quotes))))

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :load-quotes
  (fn                      ;; <-- the handler function
    [cfx [_ updates]]     ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [query  (gstring/format "{load_quotes {quotes {id quote author}}}")]
      (.log js/console (str ">>> QUEERY  >>>>> " query ))
      (re-frame/dispatch [::re-graph/query query {} [:load-quotes-response]]))))

(re-frame/reg-event-db
 :process-create-quote
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (let [comment     (:create_quote (second (first response)))
         _           (.log js/console (str ">>> comment QQ >>>>> " comment))]
         (update-in db [:comments] conj comment))))

(re-frame/reg-event-fx
  :create-quote
  (fn                    ;; <-- the handler function
    [cfx _]            ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    (let [updates (second _)
          {:keys [post-id comment user-id]} updates
          mutation    (gstring/format "mutation { create_comment( post_id: %i, comment: \"%s\", user_id: %i)
                                      { username comment created_at }}"
                                      post-id comment user-id)]
      (.log js/console (str ">>> CREATE ANSWER MUTATION >>>>> " mutation ))
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-quote]]))))

(re-frame/reg-event-db
 :process-after-update-answer
 []
 (fn [db [_ response]]
   (let [answer           (-> response :data :update_answer)
         answer-keyword   (keyword (:id answer))
         question-keyword (keyword (str (:question_id answer)))]
       (-> db
          (update-in [:questions question-keyword :answers answer-keyword] conj answer)
          (update :loading? not)))))

(re-frame/reg-event-fx
  :update-quote
  (fn
    [cofx [_ updates]]
    (let [{:keys [answer correct answer_id]} updates
          mutation  (gstring/format "mutation { update_answer( answer: \"%s\", correct: %s, id: %i)
                                    { id answer correct question_id }}"
                                  answer correct answer_id)]
       (.log js/console (str ">>> MUTATION UPDATE ANSWER >>>>> " mutation ))
       (re-frame/dispatch [::re-graph/mutate
                           mutation                                  ;; graphql query
                           {:some "Pumas campeón prros!! variable"}   ;; arguments map
                           [:process-after-update-quote]]))))

(re-frame/reg-event-db
 :process-delete-quote
 []
 (fn
   [db [_ data]]
   (.log js/console (str ">>> Data  VVV >>>>> " data ))
   (let [question-id (-> data :data :delete_quote :id)] ;; Datein Komm zurück
     (.log js/console (str ">>> QUESTION >>>>> " question-id ))
     (-> db
         (update-in [:questions] dissoc (keyword question-id))
         (update :qcounter dec)
         (update  :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-quote            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id quote-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> quote-id   OOO>>>>>  >>>> " quote-id))
   (when (js/confirm "Frage löschen?")
     (let [mutation  (gstring/format "mutation { delete_question( question_id: %i ) { id }}"
                                     quote-id)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-delete-quote]])))))
