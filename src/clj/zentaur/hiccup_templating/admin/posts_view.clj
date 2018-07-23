(ns zentaur.hiccup_templating.admin.posts-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [zentaur.hiccup_templating.posts-view :as posts-view]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn index [posts]
  (let [formatted-posts (doall (for [post posts]
                                 (posts-view/format-post post)))]
    [:div {:id "cont"}
      [:div {:id "content"} formatted-posts]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

