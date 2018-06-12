(ns blog.controllers.base-controller
  (:require [blog.models.posts :as modposts]
            [blog.libs.helpers :as h]
            [cognitect.transit :as t]
            [selmer.parser :as parser]
            [ring.util.http-response :as resp])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def out (ByteArrayOutputStream. 4096))
(def w (t/writer out :json))

(def in (ByteArrayInputStream. (.toByteArray out)))
(def r (t/reader in :json))

(defn json-response [map]
  (let [_ (t/write w {:body "asdasdasd"})
        in (ByteArrayInputStream. (.toByteArray out))
        reader (t/reader in :json)]
    (parser/render-file "json/comment.json" map)))

(defn set-vars [request]
  (let [csrf-field (-> request :session :ring.middleware.anti-forgery/anti-forgery-token)
        flash    (:flash request)
        identity (:identity request)]
    {:identity identity :flash flash :csrf-field csrf-field}))

