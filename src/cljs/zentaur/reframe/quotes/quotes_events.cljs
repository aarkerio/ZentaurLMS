(ns zentaur.reframe.quotes.quotes-events
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]
            [com.rpl.specter :as spct]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.libs.commons :as cms]
            [zentaur.reframe.tests.db :as zdb]))

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
(def quote-interceptors [check-spec-interceptor])

(re-frame/reg-event-db
 :load-quotes-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-quotes (:load_quotes data)
          _          (.log js/console (str ">>> QUOTES PRE-RESPONSE >>>>> " pre-quotes))
          quotes     (cms/vector-to-ordered-idxmap (:quotes pre-quotes))
          _          (.log js/console (str ">>> QUTES RESPONSE  QUOTES >>>>> " quotes))]
         (assoc db :quotes quotes))))

({:6 {:id 6, :quote "The time you enjoy wasting is not wasted time", :author "B. Rusell"}}
 {:5 {:id 5, :quote "There is much pleasure to be gained from useless knowledge.", :author "B. Rusell"}}
 {:3 {:id 3, :quote "Seriousness is the only refuge of the shallow.", :author "Oscar Wilde"}})

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :load-quotes
  (fn                      ;; <-- the handler function
    [cfx [_ updates]]     ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [query "{load_quotes {quotes {id quote author}}}"]
      (.log js/console (str ">>> QUEERY  >>>>> " query ))
      (re-frame/dispatch [::re-graph/query query {} [:load-quotes-response]]))))

(re-frame/reg-event-db
 :process-create-quote
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (let [pre-quote  (:create_quote (second (first response)))
         quote      (assoc {} (keyword (str (:id pre-quote))) pre-quote)]
         (update-in db [:quotes] conj quote))))

(re-frame/reg-event-fx
  :create-quote
  (fn                    ;; <-- the handler function
    [cfx _]            ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (.log js/console (str ">>>  und ebenfalls _ " (second _)))
    (let [updates (second _)
          {:keys [author quote]} updates
          mutation    (gstring/format "mutation { create_quote(author: \"%s\", quote: \"%s\")
                                      { id quote author }}"
                                      author quote)]
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-quote]]))))

(re-frame/reg-event-db
 :process-after-update-quote
 []
 (fn [db [_ response]]
   (.log js/console (str ">>> RESPONSE  >>>>> " response))
   (let [pre-quote     (-> response :data :update_quote)
         quote-keyword (keyword (str (:id pre-quote)))
         quote         (assoc {} quote-keyword pre-quote)]
     (.log js/console (str ">>> UQ ***** >>>>> " quote ))
     (-> db
         ;; (update :a (fn [v] (into (if v v []) [4 5])))
         (update :quotes (fn [v] (let [f (into {} v)] (update f quote-keyword (fn [k] pre-quote)))))
         (update :loading? not)))))

(re-frame/reg-event-fx
  :update-quote
  (fn
    [cofx [_ updates]]
    (let [{:keys [id quote author]} updates
          mutation  (gstring/format "mutation { update_quote( quote: \"%s\", author: \"%s\", id: %i)
                                    { id quote author }}"
                                    quote author id)]
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
   (let [quote-id (-> data :data :delete_quote :id)] ;; Datein Komm zurück
     (.log js/console (str ">>> QUESTION >>>>> " quote-id))
     (-> db
         (update-in [:quotes] dissoc (keyword quote-id))
         (update  :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-quote            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id quote-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (.log js/console (str ">>> quote-id   OOO>>>>>  >>>> " quote-id))
   (when (js/confirm "Delete Quote?")
     (let [mutation  (gstring/format "mutation { delete_question( question_id: %i ) { id }}"
                                     quote-id)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-delete-quote]])))))
