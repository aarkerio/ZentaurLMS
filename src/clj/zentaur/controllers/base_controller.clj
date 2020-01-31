(ns zentaur.controllers.base-controller
  (:require [ring.util.http-response :as response]
            [zentaur.libs.helpers :as h]
            [zentaur.models.posts :as modposts]))


(defn set-vars [request]
  (let [csrf-field (:anti-forgery-token request)
        flash      (:flash request)
        identity   (:identity request)]
    {:identity identity :flash flash :csrf-field csrf-field}))

(defn json-parser [content]
   (response/ok content))

(defn parser
  "Html parsing"
  [content]
  (response/content-type (response/ok content) "text/html; charset=utf-8"))
