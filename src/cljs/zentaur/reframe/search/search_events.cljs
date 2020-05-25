(ns zentaur.reframe.search.search-events
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]))

(rf/reg-event-db
 :process-load-search
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [post-data  (:load_search data)
          subjects   (:subjects post-data)
          levels     (:levels post-data)
          langs      (:langs post-data)
          _          (.log js/console (str ">>> SUBJECTS >>>>> " subjects))
          _          (.log js/console (str ">>> LEVELS >>>>> " levels))
          _          (.log js/console (str ">>> LANGS >>>>> " langs))
          ]
     (-> db
         (assoc :subjects subjects)
         (assoc :levels   levels)
         (assoc :langs    langs)))))

(rf/reg-event-fx
  :load-search
  (fn
    [cfx [_ _]]
    (let [query (gstring/format "{load_search {uurlid title subjects {id subject} levels {id level} langs {id lang}}}")]
      (rf/dispatch [::re-graph/query query {} [:process-load-search]]))))

(rf/reg-event-db
 :add-search-elm
  []
  (fn [db [_ updates]]
    (let [ksection  (first (first updates))  ;; key section
          vsection  (get updates ksection)   ;; value section
          elm       (str ksection "_" vsection)
          checkbox  (gdom/getElement elm)
          checked   (.. checkbox -checked)]
   (if checked
     (update-in db [:search-terms ksection] conj vsection)
     (update-in db [:search-terms ksection] (fn [all] (remove #(when (= % vsection) %) all)))))))

(rf/reg-event-db
 :search-question-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [questions     (-> data :search_questions :questions)]
         (assoc db :questions  questions))))

(rf/reg-event-fx
  :search-questions
  (fn [cfx [_ updates]]
    (let [{:keys [search-text]} updates
          search-terms (-> cfx :db :search-terms)
          _            (.log js/console (str ">>> search-terms >>>>> " search-terms ))
          subjects     (str/join " " (get search-terms "subjects"))
          levels       (str/join " " (get search-terms "levels"))
          langs        (str/join " " (get search-terms "langs"))
          _            (.log js/console (str ">>> SQQQQQ >>>>> " updates " >> " subjects " >>> levels >> " levels "  langs >> " langs))
          query        (gstring/format "{search_fullq(subjects: \"%s\", levels: \"%s\", langs: \"%s\", terms: \"%s\")
                                        { uurlid title questions { id question qtype }}}"
                                       subjects levels langs search-text)]
      (.log js/console (str ">>> QUERRRY  >>>>> " query ))
      (rf/dispatch [::re-graph/query query {} [:search-question-response]]))))
