(ns zentaur.users
  (:require [goog.dom :as gdom]
            [goog.events :as events]
            [cljs.loader :as loader])
  (:import [goog.events EventType]))

(enable-console-print!)

(println "I'm foo!")

(events/listen (gdom/getElement "button") EventType.CLICK
  (fn [e]
    (loader/load :bar
      (fn []
        ((resolve 'bar.core/woz))))))
