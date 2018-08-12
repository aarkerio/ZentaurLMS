(ns ^:figwheel-no-load zentaur.app
  (:require [zentaur.core :as core]))

(enable-console-print!)

(defn main []
  (core/init!))

