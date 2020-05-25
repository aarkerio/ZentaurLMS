(ns zentaur.reframe.quotes.quotes-events
  (:require [cljs.spec.alpha :as s]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]))

; -- First Interceptor ----
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "Die Spezifikationsprüfung ist fehlgeschlagen: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec-interceptor (rf/after (partial check-and-throw :zentaur.reframe.tests.db/db))) ;; PARTIAL: a potential way to currying

;; We now create the interceptor chain shared by all event handlers which check the db.
;; A chain of interceptors is a vector of interceptors. Explanation of the `path` Interceptor is given further below.
(def reframe-chain-interceptors [check-spec-interceptor])

;; DEPRECATED
(def interceptor-reorder-after-quotes
  (rf/->interceptor
   :id      :reorder-after-quotes
   :after   (fn [context]
              (let [app-db    (-> context :effects :db)
                    quotes    (:quotes app-db)
                    qordered  (into (sorted-map-by (fn [key1 key2]
                                                     (compare
                                                      (get-in quotes [key2 :id])
                                                      (get-in quotes [key1 :id]))))
                                    quotes)]
                (assoc-in context [:effects :db :quotes] qordered)))))

(rf/reg-event-db
 :load-quotes-response
  [reframe-chain-interceptors]
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-quotes (:load_quotes data)
          _          (.log js/console (str ">>> QUOTES  PRE-RESPONSE >>>>> " pre-quotes))
          quotes     (reduce #(assoc %1 (:id %2) %2) {} (:quotes pre-quotes))
          _          (.log js/console (str ">>> QUOTES AFTER PROCESSED >>>>> " quotes))]
         (assoc db :quotes quotes))))

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(rf/reg-event-fx
  :load-quotes
  (fn                      ;; <-- the handler function
    [cfx [_ updates]]     ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [limit 10
          offset 0
          query (gstring/format "{load_quotes(offset: %i, limit: %i) {quotes {id quote author total}}}"
                                offset limit)]
      (rf/dispatch [::re-graph/query query {} [:load-quotes-response]]))))

(rf/reg-event-db
 :process-create-quote
 [reframe-chain-interceptors]
 (fn
   [db [_ response]]            ;; destructure the response from the event vector
   (let [pre-quote  (:create_quote (second (first response)))
         qkey       (:id pre-quote)
         quote      (assoc {} qkey pre-quote)]
     (.log js/console (str ">>> QUOTE PCQ   >>>>> " quote))
     (assoc-in db [:quotes qkey] pre-quote))))

(rf/reg-event-fx
  :create-quote
  (fn                    ;; <-- the handler function
    [cfx _]            ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [updates (second _)
          {:keys [author quote]} updates
          mutation (gstring/format "mutation { create_quote(author: \"%s\", quote: \"%s\")
                                      { id quote author total }}"
                                   author quote)]
      (if (and (> (count author) 6 ) (> (count quote) 6))
        (rf/dispatch [::re-graph/mutate mutation {} [:process-create-quote]])
        (js/alert "Ooooops! One field is too short")))))

(defn update-shit [keyword quote itm]
   (if (= keyword (first (first itm))) (hash-map keyword quote) itm))

(rf/reg-event-db
 :process-after-update-quote
 [reframe-chain-interceptors]
 (fn [db [_ response]]
   (let [pre-quote     (-> response :data :update_quote)
         _             (.log js/console (str ">>> PRE quote >>>>> " pre-quote))
         quote-keyword (:id pre-quote)]
     (-> db
         (update-in [:quotes quote-keyword] conj pre-quote)
         (update :loading? not)))))

(rf/reg-event-fx
  :update-quote
  (fn
    [cofx [_ updates]]
    (let [{:keys [id quote author]} updates
          mutation  (gstring/format "mutation { update_quote( quote: \"%s\", author: \"%s\", id: %i)
                                    { id quote author }}"
                                    quote author id)]
      (if (and (> (count author) 6 ) (> (count quote) 6))
       (rf/dispatch [::re-graph/mutate mutation {} [:process-after-update-quote]])
       (js/alert "Ooooops! One field is too short")))))

(rf/reg-event-db
 :process-delete-quote
 [reframe-chain-interceptors]
 (fn
   [db [_ data]]
   (.log js/console (str ">>> Data  delete quote >>>>> " data ))
   (let [quote-id (-> data :data :delete_quote :id)]
     (.log js/console (str ">>> QUESTION >>>>> quote-id >> " quote-id))
     (-> db
         (update-in [:quotes] dissoc quote-id)
         (update  :loading?  not)))))

(rf/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-quote            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id quote-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (when (js/confirm "Delete Quote?")
     (let [mutation (gstring/format "mutation { delete_quote( id: %i ) { id }}"
                                     quote-id)]
       (rf/dispatch [::re-graph/mutate mutation {} [:process-delete-quote]])))))
