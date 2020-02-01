(ns zentaur.hiccup.layouts.basic-layout
  (:require [hiccup.form :as f]
            [hiccup.page :refer [html5 include-css include-js]]
            [zentaur.hiccup.helpers-view :as helpers]))

(defn application [content]
  (html5 [:head
          [:title (str ":: Zentaur :: Easy Quizz Tests for you " (:title content))]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/styles.css")]
         [:body
          (when-let [flash (:flash content)]
            (helpers/display-flash flash))
          [:div {:class "blog-header"}
           [:div {:class "blog-title" :id "blogtitle"} "Zentaur"]
           [:div {:class "container"}  (:contents content)]
           [:div {:class "blog-footer" :id "footer"}
            [:p "Chipotle Software &copy; 2018-2020. MIT License."]]
           (include-js "/cljs-out/dev-main.js")
           [:div {:id "root-app"} ""]]]))
