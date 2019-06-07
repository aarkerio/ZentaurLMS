(ns zentaur.libs.helper
    (:require [goog.dom :as gdom]
              [goog.string :as gstr]
              [goog.events :as events]
              [goog.style :as style]))

(defn my-toggle [element-str]
  (let [div-message (gdom/getElement element-str)]
    (style/showElement div-message (not (style/isElementShown div-message)))))
