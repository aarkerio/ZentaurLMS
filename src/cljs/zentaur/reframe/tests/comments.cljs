(ns zentaur.reframe.tests.comments
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.libs :as zlib]))

;; (defn format-comment [comment]
;;     [:div {:class "user_comments"}
;;         [:div {:style "font-size:8pt;"} (:created_at comment)]
;;         [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
;;         [:div {:class "font"} (:comment comment)]])


;; (defn comment-form [base id]
;;   (when-let [email (-> base :identity :email)]
;;     [:div.div-simple-separator
;;      [:p "Add a new comment:"]
;;      [:form {:id "submit-comment-form" :action "/vclass/posts/comments" :method "post" :class "css-class-form"}
;;       (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
;;       (f/hidden-field {:value id} "post_id")
;;       [:div (f/text-area {:cols 90 :rows 5} "comment-textarea")]
;;       (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]]))

(defn single-comment [comment]
  [:div.div-separator
   [:div (:comment comment)]
   [:div (:username comment)]
   [:div (:created_at comment)]])

(defn display-comments []
  (let [comments (rf/subscribe [:comments])]
    (fn []
      [:div
       (doall (for [{:keys [idx comment]} (zlib/indexado @comments)]
                ^{:key (hash comment)}
                [single-comment (assoc comments :counter idx)]))])))

(defn comment-form []
  (let [blog-id (.-value (gdom/getElement "blog-id"))
        comment (r/atom "")]
    (fn []
       [:div.div-separator
        [:textarea {:value @comment :on-change  #(reset! comment (-> % .-target .-value))
                    :placeholder "Cokment" :title "Comment" :cols 120  :rows 10}]]
        [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                     :on-click #(rf/dispatch [:save-blog-comment {:blog_id blog-id
                                                                  :comment @comment}])}])))

(defn comments-root-app
  []
  (let [dsfds "dsfdsfdsf"]
    [:div
     ;; [display-comments]
     ;; [comment-form]
     ]))
