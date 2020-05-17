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

(defn vector-to-ordered-illldxmap
  "Convert vector of maps to an indexed map, exposing the makes the re-frame CRUD easier"
  [rows]
  (let [indexed (reduce #(assoc %1 (keyword (str (:id %2))) %2) {} rows)]
    (apply hash-map indexed)))

(defn vector-to-ordered-idxmap
  "Convert vector of maps to an indexed map, exposing the makes the re-frame CRUD easier"
  [rows]
  (let [indexed (reduce #(assoc %1 (keyword (str (:id %2))) %2) {} rows)]
    (into (sorted-map-by (fn [key1 key2]
                           (compare
                            (get-in indexed [key1 :id])
                            (get-in indexed [key2 :id]))))
          indexed)))

(re-frame/reg-event-db
 :load-quotes-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-quotes (:load_quotes data)
          _          (.log js/console (str ">>> QUOTES  PRE-RESPONSE >>>>> " pre-quotes))
          ;; quotes     (map #(assoc {} (keyword (str (:id %))) %) (:quotes pre-quotes))
          quotes     (vector-to-ordered-idxmap (:quotes pre-quotes))
          _          (.log js/console (str ">>> QUOTES AFTER PROCESSED >>>>> " quotes))]
         (assoc db :quotes quotes))))

;;;;;;;;    CO-EFFECT HANDLERS (with GraphQL!)  ;;;;;;;;;;;;;;;;;;
;; reg-event-fx == event handler's coeffects, fx == effect
(re-frame/reg-event-fx
  :load-quotes
  (fn                      ;; <-- the handler function
    [cfx [_ updates]]     ;; <-- 1st argument is coeffect, from which we extract db, "_" = event
    (let [query "{load_quotes(offset: 10, limit: 10) {quotes {id quote author}}}"]
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
          mutation (gstring/format "mutation { create_quote(author: \"%s\", quote: \"%s\")
                                      { id quote author }}"
                                   author quote)]
      (re-frame/dispatch [::re-graph/mutate mutation {} [:process-create-quote]]))))

(defn update-shit [keyword quote itm]
   (if (= keyword (first (first itm))) (hash-map keyword quote) itm))

(re-frame/reg-event-db
 :process-after-update-quote
 []
 (fn [db [_ response]]
   (.log js/console (str ">>> RESPONSE  >>>>> " response ))
   (let [pre-quote     (-> response :data :update_quote)
         quote-keyword (keyword (str (:id pre-quote)))
         new-quotes    (map (partial update-shit quote-keyword pre-quote) (:quotes db))]
     (.log js/console  (str ">>> UQ ***** >>>>> " pre-quote ))
     (.log js/console  (str ">>> NEW QUOTES ***** >>>>> " new-quotes ))
     (-> db
         (assoc :quotes new-quotes)
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
   (let [quote-id (-> data :data :delete_quote :id)
         kquo-id  (keyword (str quote-id))]
     (.log js/console (str ">>> QUESTION >>>>> " quote-id " KEYWORD >>> " kquo-id))
     (-> db
         (update-in [:quotes] dissoc kquo-id)
         (update  :loading?  not)))))

(re-frame/reg-event-fx       ;; <-- note the `-fx` extension
 :delete-quote            ;; <-- the event id
 (fn                          ;; <-- the handler function
   [cofx [dispatch-id quote-id]]      ;; <-- 1st argument is coeffect, from which we extract db
   (when (js/confirm "Delete Quote?")
     (let [mutation (gstring/format "mutation { delete_quote( id: %i ) { id }}"
                                     quote-id)]
       (re-frame/dispatch [::re-graph/mutate mutation {} [:process-delete-quote]])))))
