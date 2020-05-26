(ns zentaur.reframe.search.search-views
  (:require [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.libs.commons :as cms]))

(defn questions-selector []
  (let [search-text  (r/atom "")]
    [:div
     [:h2 "Select options"]
     [:div.scheckbox-container
      [:label {:for "subject_id"} " Select subject:"]
      (for [row-subject @(rf/subscribe [:loaded-subjects])]
        ^{:key (:id row-subject)} [:div.scheckbox (:subject row-subject) "  "
                                   [:input {:type      "checkbox"
                                            :title     (:subject row-subject)
                                            :id        (str "subjects_" (:id row-subject))
                                            :on-change  #(rf/dispatch [:add-search-elm {"subjects" (:id row-subject)}])}]])]
     [:div.scheckbox-container
      [:label {:for "level_id"} " Select level:"]
      (for [row-level @(rf/subscribe [:loaded-levels])]
        ^{:key (:id row-level)} [:div.scheckbox (:level row-level) "  "
                                 [:input {:type      "checkbox"
                                          :title     (:level row-level)
                                          :id        (str "levels_" (:id row-level))
                                          :on-change #(rf/dispatch [:add-search-elm {"levels" (:id row-level)}])}]])]

     [:div.scheckbox-container
      [:label {:for "lang_id"} " Select lang:"]
      (for [row-lang @(rf/subscribe [:loaded-langs])]
        ^{:key (:id row-lang)} [:div.scheckbox (:lang row-lang) "  "
                                [:input {:type      "checkbox"
                                         :title     (:lang row-lang)
                                         :id        (str "langs_" (:id row-lang))
                                         :on-change #(rf/dispatch [:add-search-elm {"langs" (:id row-lang)}])}]])]]))

(defn terms-input-text []
  (let [search-text  (r/atom "")]
    (fn []
     [:div "Terms: " [:br]
      [:input {:type "text" :value @search-text :maxLength 180 :size 100 :on-change #(reset! search-text (-> % .-target .-value))}]
      [:input.btn {:class "btn btn-outline-primary-green" :type "button" :value "Search"
                   :on-click #(rf/dispatch [:search-questions {:search-text @search-text
                                                               :offset 0
                                                               :limit 50}])}]])))

(defn show-selected-questions []
  (let [selected-qstios (rf/subscribe [:selected-qstios])]
    (fn []
      (when (> (count @selected-qstios) 0)
        [:div {:style "padding:20 px; margin:15px; border: 1px dotted gray;"} (str "You have selected: " (count @selected-qstios) " questions.") [:br]
         [:form {:action "/vclass/tests/build" :method "post" :class "css-class-form"}
          [:input.btn {:class "btn btn-outline-primary-green" :type "submit" :value "Create Test"}]]]))))

(defn offered-questions []
  (let [subject-id  (r/atom 1)
        level-id    (r/atom 1)
        lang-id     (r/atom 1)]
    [:table.some-table-class
     [:thead
      [:tr
       [:th "Select"]
       [:th "Question"]
       [:th "Type"]]]
     [:tbody
      (for [q @(rf/subscribe [:searched-qstios])]
        ^{:key (hash (:id q))} [:tr
                         [:td [:input {:type "checkbox" :title "Select" :id (str "qst_" (:id q)) :on-change #(rf/dispatch [:add-question {:question_id (:id q)}])}]]
                         [:td (:question q)]
                         [:td (:qtype q)]])]]))

(defn search-app
  []
    [:div
     [questions-selector]
     [terms-input-text]
     [show-selected-questions]
     [offered-questions]])
