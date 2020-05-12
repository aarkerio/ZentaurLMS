(ns zentaur.reframe.tests.quotes-views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.forms.blocks :as blk]
            [zentaur.reframe.tests.libs :as zlib]))



(defn listing-quotes []
  (let [subject-id  (r/atom 1)
        level-id    (r/atom 1)
        lang-id     (r/atom 1)]
    [:table.some-table-class
     [:thead
      [:tr
       [:th "Select"]
       [:th "Question"]
       [:th "Explanation"]]]
     [:tbody
      (for [q @(rf/subscribe [:questions])]
        ^{:key (:id q)} [:tr
                         [:td [:input {:type "checkbox" :title "Select" :on-change #(rf/dispatch [:add-question {:question_id (:id q)}])}]]
                         [:td (:question q)]
                         [:td (:explanation q)]])]]))

(defn quotes-app
  []
  (let [dsfds "dsfdsfdsf"]
    [:div
     [new-quote]
     [listing-quotes]
      [:p "Ziehen Sie."]]))


