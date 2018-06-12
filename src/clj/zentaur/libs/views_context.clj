(ns zentaur.libs.views_context
  (:require [ring.util.http-response :refer [content-type ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(declare ^:dynamic *app-context*)
(parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))
