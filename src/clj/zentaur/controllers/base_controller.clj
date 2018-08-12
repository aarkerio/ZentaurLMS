(ns zentaur.controllers.base-controller
  (:require [zentaur.models.posts :as modposts]
            [zentaur.libs.helpers :as h]
            [selmer.parser :as parser]
            [ring.util.http-response :as resp]))

(defn json-response [map]
  (let [reader (str "foo bar")]
    (parser/render-file "json/comment.json" map)))

(defn set-vars [request]
  (let [csrf-field (-> request :session :ring.middleware.anti-forgery/anti-forgery-token)
        flash    (:flash request)
        identity (:identity request)]
    {:identity identity :flash flash :csrf-field csrf-field}))

