(ns zentaur.reframe.tests.comments
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.libs :as zlib]))

(defn single-comment [comment]
  (.log js/console (str ">>> single-comment XXXX >>>>> " comment ))
  (let [le-text (:comment comment)
        counter (:counter comment)]
    [:div.div-simple-separator
     [:div (str counter ").- " le-text)]
     [:div (:username comment)]
     [:div (:created_at comment)]]))

(defn display-comments []
  (let [comments (rf/subscribe [:comments])]
    (fn []
      [:div
       (for [c (map-indexed (fn [idx comment] (assoc comment :counter (inc idx))) @comments)]
                ^{:key (hash (:counter c))}
                [single-comment c])])))

(defn comment-form [user-id]
  (let [post-id (.-value (gdom/getElement "post-id"))
        comment (r/atom "")]
    (fn []
       [:div.div-simple-separator
        [:div [:textarea {:value @comment :on-change  #(reset! comment (-> % .-target .-value))
                    :placeholder "Write your comment" :title "Comment" :cols 90  :rows 4}]]
        [:div [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                     :on-click #(do (rf/dispatch [:save-blog-comment {:post-id post-id
                                                                      :user-id user-id
                                                                      :comment @comment}])
                                    (reset! comment ""))
                           }]]])))

(defn comments-root-app
  []
  (let [user-id (.-value (gdom/getElement "user-id"))
        _  (.log js/console (str ">>> user-id >>>>> " user-id " >>> empty " (empty? user-id)  ))
       comments (if (empty? user-id) "" [comment-form user-id])]
    [:div
     [display-comments]
     comments]))
