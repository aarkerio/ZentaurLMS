(ns zentaur.users
  (:require [goog.dom :as gdom]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defn add-listener [{:keys [elem event function] :or {event "click" function "send"}}]
  (.log js/console (str elem ">>>>> ADD LISTENER "))
  (.addEventListener (.getElementById js/document elem) event
    (fn [evt]
     (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked the elemeent:  " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn some-function [value]
  (.log js/console (str ">>> VALUE >>>>> " (.stringify js/JSON value))))

(defn mount []
  (let [elem (gdom/getElement "icon-add")]
    (.log js/console (str ">>> ELEM >>>>> " (.stringify js/JSON elem)))
  (events/listen elem EventType.CLICK
    (some-function " #####   >>>>>   events/listen  in users ns"))))

(defn mount-components []
  (when-let [content (js/document.getElementById "root-app")]
    (while (.hasChildNodes content)
      (.removeChild content (.-lastChild content)))
    (.appendChild content (js/document.createTextNode "Willkommen zu meim ekelhaft blog!!"))))

