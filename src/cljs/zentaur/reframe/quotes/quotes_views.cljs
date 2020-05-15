(ns zentaur.reframe.quotes.quotes-views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.febe.pagination :as pag]
            [zentaur.reframe.tests.forms.blocks :as blk]
            [zentaur.reframe.tests.libs :as zlib]))

(defn new-quote
  []
  (let [quote    (r/atom "")
        author   (r/atom "")]
    (fn []
      [:div {:id "hidden-form"}
       [:input {:type        "text"
                :maxLength   179
                :size        90
                :placeholder "Quote"
                :value       @quote
                :on-change   #(reset! quote (-> % .-target .-value))}]
       [:input {:type        "text"
                :maxLength   80
                :size        60
                :placeholder "Author"
                :value       @author
                :on-change   #(reset! author (-> % .-target .-value))}]
       [:br]
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
        [:input {:type      "text"
                 :value     @aquote
                 :maxLength 180
                 :size      120
                 :on-change #(reset! aquote (-> % .-target .-value))}]
        [:input {:type      "text"
                 :value     @aauthor
                 :maxLength 80
                 :size      60
                 :on-change #(reset! aauthor (-> % .-target .-value))}]
       [:div [:input.btn {:type  "button" :class "btn btn btn-outline-primary-green" :value "Update"
                          :on-click #(rf/dispatch [:update-quote {:id     id
                                                                  :quote  @aquote
                                                                  :author @aauthor}])}]]])))
(defn quote-item
  "Display any type of question"
  [{:keys [id author quote] :as q}]
  (.log js/console (str ">>> VALUE qqqqqq>>>>> " q ))
  (let [editing-quote (r/atom false)]
    (fn []
      [:tr
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
       [:td {:colSpan 2}
        (if @editing-quote
          [edit-quote q]
          [:div  [:span.bold-font quote]  (str  "          "    author)])]
       [:td
        [:img {:src     "/img/icon_delete.png"
               :title   "Frage löschen"
               :alt     "Frage löschen"
               :on-click #(rf/dispatch [:delete-quote id])}]]])))

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
          (doall (for [q @quotes]
                   ^{:key (hash q)}
                   [quote-item (second (first q))]))]])))

(defn pagination
  ""
  []
  (let [total          100
        items-per-page 5
        max-links      3
        page           1]
  [:div
   (pag/html-paginator {:records total :items-per-page items-per-page :max-links max-links :current page :biased :left :location "/admin/quotes"})]) )

(defn quotes-app
  []
  [:div
   [new-quote]
   [display-quotes-list]
   [pagination]])

