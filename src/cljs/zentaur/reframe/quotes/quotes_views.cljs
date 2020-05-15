(ns zentaur.reframe.tests.quotes-views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.forms.blocks :as blk]
            [zentaur.reframe.tests.libs :as zlib]))

(defn new-quote
  []
  (let [quote       (r/atom "")
        author     (r/atom "")]
    (fn []
      [:div.div-new-answer
       [:input {:type        "text"
                :maxLength   180
                :size        80
                :value       @quote
                :on-change   #(reset! quote (-> % .-target .-value))}]
       [:input {:type        "text"
                  :maxLength   180
                  :size        80
                  :value       @author
                  :on-change   #(reset! author (-> % .-target .-value))}]
       [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Zitate hinzufügen"
                    :on-click #(do (rf/dispatch [:create-quote {:author @author
                                                                :quote @quote}])
                                   (reset! quote "")
                                   (reset! author ""))}]])))

(defn edit-quote [{:keys [id quote author]}]
  (let [aquote   (r/atom quote)
        aauthor  (r/atom author)]
    (fn []
      [:div.edit_question
       [:div "Question: " [:br]
        [:input {:type      "text"
                 :value     @aquote
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aquote (-> % .-target .-value))}]]
       [:div "Hint: " [:br]
        [:input {:type      "text"
                 :value     @aauthor
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aauthor (-> % .-target .-value))}]]
       [:div [:input.btn {:type  "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                          :on-click #(rf/dispatch [:update-quote {:id     id
                                                                  :quote  @aquote
                                                                  :author @aauthor}])}]]])))
(defn quote-item
  "Display any type of question"
  [{:keys [id author quote] :as q}]
  (let [editing-quote (r/atom false)]
    (fn []
      [:td
        (if @editing-quote
          [:img.img-float-right {:title    "Zite abbrechen"
                                 :alt      "Zite abbrechen"
                                 :src      "/img/icon_cancel.png"
                                 :on-click #(swap! editing-quote not)}]
          [:img.img-float-right {:title    "Zite bearbeiten"
                                 :alt      "Zita bearbeiten"
                                 :src      "/img/icon_edit.png"
                                 :on-click #(swap! editing-quote not)}])]  ;; editing ends
     [:td {:colspan 2}
      [:div quote]
      [:div author]
      (when @editing-quote
        [edit-quote q])]
     [:td
       [:img {:src     "/img/icon_delete.png"
              :title   "Frage löschen"
              :alt     "Frage löschen"
              :on-click #(rf/dispatch [:delete-quote id])}]])))

(defn display-quotes-list
  []
  (let [quotes (rf/subscribe [:quotes])]
    (fn []
      [:table.some-table-class
       [:thead
        [:tr
         [:th "Edit"]
         [:th "Quote"]
         [:th "Author"]
         [:th "Delete"]]]
         [:tbody
          (doall (for [{:keys [idx quote]} (zlib/indexado @quotes)]
                   ^{:key (hash quote)}
                   [quote-item quote]))]])))

(defn quotes-app
  []
  [:div
   [new-quote]
   [display-quotes-list]])

