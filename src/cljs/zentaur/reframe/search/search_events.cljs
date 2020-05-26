(ns zentaur.reframe.search.search-events
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]
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
         (assoc-in [:search-fields :subjects] subjects)
         (assoc-in [:search-fields :levels]   levels)
         (assoc-in [:search-fields :langs]    langs)))))

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
    (.log js/console (str ">>> UPDATES *** >>>>> " updates ))
    (let [ksection  (first (first updates))  ;; key section
          vsection  (get updates ksection)   ;; value section
          elm       (str ksection "_" vsection)
          checkbox  (gdom/getElement elm)
          checked   (.. checkbox -checked)]
   (if checked
     (update-in db [:selected-fields ksection] conj vsection)
     (update-in db [:selected-fields ksection] (fn [all] (remove #(when (= % vsection) %) all)))))))

(rf/reg-event-db
 :search-question-response
 []
  (fn [db [_ {:keys [data errors]}]]
    (let [questions     (-> data :search_fullq :questions)]
         (assoc db :searched-qstios questions))))

(rf/reg-event-fx
  :search-questions
  (fn [cfx [_ updates]]
    (let [{:keys [search-text offset limit]} updates
          selected-fields (-> cfx :db :selected-fields)
          _            (.log js/console (str ">>> selected-fields >>>>> " selected-fields ))
          subjects     (str/join " " (get selected-fields "subjects"))
          levels       (str/join " " (get selected-fields "levels"))
          langs        (str/join " " (get selected-fields "langs"))
          _            (.log js/console (str ">>> SQQQQQ >>>>> " updates " >> " subjects " >>> levels >> " levels "  langs >> " langs))
          query        (gstring/format "{search_fullq(subjects: \"%s\", levels: \"%s\", langs: \"%s\", terms: \"%s\", offset: %i, limit: %i)
                                        { questions { id question qtype }}}"
                                       subjects levels langs search-text offset limit)]
      (.log js/console (str ">>> QUERRRY  >>>>> " query ))
      (rf/dispatch [::re-graph/query query {} [:search-question-response]]))))


(rf/reg-event-db
 :add-question
  []
  (fn [db [_ question]]
    (.log js/console (str " >>>>> DB selected-qstios >>> "  (:selected-qstios db) " >>> QUESTION *** >>>>> " question ))
    (let [qid (:question_id question)
          checkbox  (gdom/getElement (str "qst_" qid))
          checked   (.. checkbox -checked)]
      (if checked
        (assoc-in  db [:selected-qstios qid] question)
        (update-in db [:selected-qstios] dissoc qid)))))
