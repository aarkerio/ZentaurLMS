(ns zentaur.hiccup.posts-view
  (:require [clojure.string :as cs]
            [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]
            [markdown.core :as md]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-post
  ([post] (format-post post true))
  ([post show]
   (let [body      (:body post)
         blog-text (if show (first (cs/split-lines body)) body) ;; show only the first paragraph (list) or all the text (show)
         div-blog  [:div {:class "blog-post"} [:h2 {:class "blog-post-title"} (:title post)]
                    [:div {:class "blog-post-meta"} (hv/format-date (:created_at post)) " " [:a {:href (str "/user/" (:uname post))} (:uname post)]]
                    [:div {:class "blog-body"} (md/md-to-html-string blog-text)]
                    [:div {:class "blog-tags"} "Tags: " (:tags post)]]]
     (if show (conj div-blog [:p [:a {:href (str "/posts/show/" (:id post))} "Read all"]]) div-blog))))

(defn format-comment [comment]
    [:div {:class "user_comments"}
        [:div {:style "font-size:8pt;"} (:created_at comment)]
        [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
        [:div {:class "font"} (:comment comment)]])

(defn index [csrf-field subjects levels langs identity]
  (let [items-per-page 5]
    [:div {:id "cont"}
     [:div
      [:h1 "Welcome to t-by-t!"]
      [:p "We are a developing Edu tech tools in the hope they will your daily tasks easier and faster."]]
     [:div.div-simple-separator
      [:form {:id "submit-comment-form" :action "/search" :method "post" :class "css-class-form"}
       (f/hidden-field {:value csrf-field} "__anti-forgery-token")
       [:div (f/text-field  {:maxlenght 90 :size 90 :placeholder "Search for something cool..."} "terms")]
       (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Search")]]
     (when identity
       [:div.create-test-form
        [:h2 "Generate a new test"]
        [:form {:id "submit-comment-form" :action "/vclass/tests/generate" :method "post" :class "css-class-form"}
         (f/hidden-field {:value csrf-field} "__anti-forgery-token")
         [:label {:for "level_id"} "Subject:"]
         [:div.div-separator
          [:select.form-control.mr-sm-2 {:name "subject_id"}
           (for [subject subjects]
             [:option {:value (:id subject)} (:subject subject)])
           ]]
         [:label {:for "level_id"} "Level:"]
         [:div.div-separator
          [:select.form-control.mr-sm-2 {:name "level_id"}
           (for [level levels]
             [:option {:value (:id level)} (:level level)])
           ]]
         [:label {:for "lang_id"} "Lang:"]
         [:div.div-separator
          [:select.form-control.mr-sm-2 {:name "lang_id"}
           (for [lang langs]
             [:option {:value (:id lang)} (:lang lang)])
           ]]
         [:label {:for "limit"} "Anzahl der Fragen:"]
         [:div.div-separator
           (f/drop-down {:class "form-control mr-sm-2"} "limit"
                        (for [n (range 1 11)]
                          [(str n) n]) 5)]
         (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0"} "Go!")]])]))

(defn listing [posts page]
  (let [items-per-page  5
        max-links       5
        total           (:total (first posts))
        formatted-posts (doall (for [post posts]
                                 (format-post post)))]
    [:div {:id "cont"}
     [:div {:id "content"} formatted-posts]
     [:div (hv/html-paginator {:records total :items-per-page items-per-page :max-links max-links
                               :current page :biased :left :location "/posts/listing"})]]))

(defn comment-form [base id]
  (when-let [email (-> base :identity :email)]
    [:div.div-simple-separator
     [:p "Add a new comment:"]
     [:form {:id "submit-comment-form" :action "/vclass/posts/comments" :method "post" :class "css-class-form"}
      (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
      (f/hidden-field {:value id} "post_id")
      [:div (f/text-area {:cols 90 :rows 5} "comment-textarea")]
      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]]))

(defn show [post base comments]
  (let [formatted-post (format-post post false)
        formatted-comments (for [comment comments]
                             (format-comment comment))
        comment-form (comment-form base (:id post))]
    [:div {:id "cont"}
     [:div [:a {:onclick "javascript:history.go(-1);"} [:img {:src "/img/icon_back.png":title "<< Go back" :alt "<< Go back"}]]]
     [:div {:id "content"} formatted-post]
     [:div {:id "comments"} formatted-comments]
     [:div {:id "comment-form"} comment-form]]))

(defn search [base results]
  (let [formatted-post "asdasdasd"]
    [:div {:id "cont"}
     [:div [:a {:onclick "javascript:history.go(-1);"} [:img {:src "/img/icon_back.png":title "<< Go back" :alt "<< Go back"}]]]
     [:div {:id "content"} formatted-post] ]))

