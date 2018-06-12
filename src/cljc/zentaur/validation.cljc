(ns blog.validation
  (:require [struct.core :as st]
            [clojure.string :as str]
            ))

(defn username?
  [username]
  (and (not (str/blank? username))
       (re-matches #"^[A-Za-z][a-zA-Z_0-9]{2,40}" username)))


(defn password?
  [password]
  (and (not (str/blank? password))
       (re-matches #".{8,256}" password)))


(defn url?
  [url]
  (or (str/blank? url)
      (re-matches #"^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" url)))


(defn email?
  [email]
  (and (not (str/blank? email))
       (re-matches #"(([^<>()\[\]\\.,;:\s@\"]+(\.[^<>()\[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))" email)))

(defn about?
  [about]
  (or (str/blank? about)
      (and (>= (count about) 1)
           (<= (count about) 350))))


(defn fullname?
  [fullname]
  (or (str/blank? fullname)
      (and (>= (count fullname) 1)
           (<= (count fullname) 60))))

(defn any-pred
  [& preds]
  (complement (apply every-pred (map complement preds))))

(defn username-or-email?
  [x]
  ((any-pred username? email?) x))

(defn ?
  [x]
  (if x true false))
