(ns ^:figwheel-no-load blog.app
  (:require [blog.core :as core]))

(enable-console-print!)

(defn main []
  (core/init!))

