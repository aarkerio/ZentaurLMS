(ns zentaur.uploads
  (:require [goog.dom :as gdom]
            [goog.events :as events])
  (:import [goog.events EventType]))

(enable-console-print!)

(.log js/console "I am in upload.cljs  !")

(defn mount []
  (.log js/console (str ">>> VALUE >>>>> mount uploads !!!")))

(defn add-insert-json []
  (when-let [button (.getElementById js/document "insert-button")]
    (.log js/console (str button ">>>>>"))
    (.addEventListener (.getElementById js/document button) "event"
      (fn [evt]
       (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked:  " atxt)]
         (.alert js/window msg)
         (.preventDefault evt))))))

