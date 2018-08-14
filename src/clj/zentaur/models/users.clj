(ns zentaur.models.users
  (:require [zentaur.db.core :as db]
            [zentaur.env :as env]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [buddy.hashers :as hashers]
            [clj-time.local :as l]))

(def userstore (atom {}))

(defn uuid [] (java.util.UUID/randomUUID))

(def user-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn create-user! [user]
  (let [password (:password user)
        user-id  (uuid)]
     (-> user
       (assoc :id user-id :password-hash (hashers/encrypt password env/secret-salt))
       (dissoc :password)
       (->> (swap! userstore assoc user-id)))))

(defn create [user]
  (let [password (:prepassword user)]
     (-> user
       (assoc :password-hash (hashers/derive password env/secret-salt))
       (db/create-user!))))

(defn get-user [user-id]
  (get @userstore user-id))

(defn get-user-by-email-and-password [email password]
  (let [password-derived (hashers/derive password env/secret-salt) trimmed_email (clojure.string/trim email)]
    (assoc {} :user (db/get-user-login
                      { :password password-derived :email trimmed_email }))))

(defn get-users [active]
  (db/get-users {:active active}))


(def post-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])
