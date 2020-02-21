(ns zentaur.controllers.base-controller
  (:require [clojure.string :as string]
            [ring.util.codec :as c]
            [ring.util.http-response :as response]
            [zentaur.models.posts :as modposts]))

(def msg-erfolg "Veränderung wurden erfolgreich gespeichert")
(def msg-fehler "Etwas ging schief")

(defn set-vars [request]
  (let [csrf-field (:anti-forgery-token request)
        flash      (:flash request)
        identity   (:identity request)]
    {:identity identity :flash flash :csrf-field csrf-field}))

(defn sanitize [string]
  (c/url-encode string))

(defn map-to-query-string
  "Convert a map to a string"
  [m]
  (string/join " " (map (fn [[k v]] (str (name k) " " v)) m)))

(defn json-parser [content]
   (response/ok content))

(defn parser
  "Html parsing"
  [content]
  (response/content-type (response/ok content) "text/html; charset=utf-8"))

