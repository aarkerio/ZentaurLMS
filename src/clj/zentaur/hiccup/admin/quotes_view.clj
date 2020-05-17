(ns zentaur.hiccup.admin.quotes-view
  (:require [clojure.tools.logging :as log]))

(defn admin-listing
  "Admin posts index"
  []
  [:div
   [:h1 "Famous quotes"]
   [:div#quotes-root-app]])

