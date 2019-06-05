(ns zentaur.controllers.base-controller
  (:require [zentaur.models.posts :as modposts]
            [zentaur.libs.helpers :as h]
            [selmer.parser :as parser]
            [ring.util.http-response :as resp]))

(defn json-response [file map]
  (let [json-file (str "json/" file ".json")]
    (parser/render-file json-file map)))

(defn set-vars [request]
  (let [csrf-field (:anti-forgery-token request)
        flash      (:flash request)
        identity   (:identity request)]
    {:identity identity :flash flash :csrf-field csrf-field}))

