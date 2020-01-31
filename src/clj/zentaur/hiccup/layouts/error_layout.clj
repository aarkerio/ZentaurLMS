(ns zentaur.hiccup.layouts.error-layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn application [content]
  (html5 [:head
          [:title "Something bad happened"]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/styles.css")]
         [:body
          [:div {:class "blog-header"}
           [:div {:class "blog-title" :id "blogtitle"} "Zentaur :: Error"]
           [:div {:class "container"}  (:message content)]
           [:div {:class "blog-footer" :id "footer"}
            [:p "Chipotle Software &copy; 2018-2020. MIT License."]]]]))
