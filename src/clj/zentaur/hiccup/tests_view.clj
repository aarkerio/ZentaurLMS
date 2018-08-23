(ns zentaur.hiccup.tests-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn format-test
  ([test] (format-test test true))
  ([test view]
   (let [div-blog   [:div {:class "blog-test"} [:h2 {:class "blog-test-title"} (:title test)]
                      [:p {:class "blog-test-meta"} (:created_at test) [:a {:href "#"} "Mark"]]
                      [:p {} (:body test)]]
         view-link  (cond view (conj div-blog [:p [:a {:href (str "/test/" (:id test))} "View"]]))]
     (if (= view true)
       view-link
       div-blog))))

(defn format-comment [comment]
    [:div {:class "user_comments"}
        [:div {:style "font-size:8pt;"} (:created_at comment)]
        [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
        [:div {:class "font"} (:comment comment)]])

(defn index [tests]
  (let [formatted-tests (doall (for [test tests]
                                 (format-test test)))]
    [:div {:id "cont"}
      [:div {:id "content"} formatted-tests]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

(defn comment-form [base id]
  (log/info (str ">>> BSEEEE >>>>> " base))
  (when-let [email (-> base :identity :email)]
            (f/form-to [:test ""]
                (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
                (f/hidden-field { :value id} "test_id")
                [:div (f/text-area {} "msgtextarea")]
                (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Anmeldung"))))

(defn show [test base comments]
  (let [formatted-test (format-test test false)
        formatted-comments (for [comment comments]
                             (format-comment comment))
        comment-form (comment-form base (:id test))]
    [:div {:id "cont"}
     [:div {:id "content"} formatted-test]
     [:div {:id "comments"} formatted-comments]
     [:div {:id "comment-form"} comment-form]]))

(defn hello []
  [:div {:class "well"}
   [:h1 {:class "text-info"} "Hello Hiccup"]
   [:div {:class "row"}
    [:div {:class "col-lg-2"}
     (f/label "name" "Name:")]
    [:div {:class "col-lg-4"}
     (f/text-field {:class "form-control" :ng-model "yourName" :placeholder "Enter a name here"} "your-name")]]
   [:hr]
   [:h1 {:class "text-success"} "Hello {{yourName}}!"]])

