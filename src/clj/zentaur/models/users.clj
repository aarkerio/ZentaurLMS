(ns zentaur.models.users
  (:require [zentaur.db.core :as db]
            [zentaur.env :as env]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [buddy.hashers :as hashers]
            [clj-time.local :as l]))

(def userstore (atom {}))

(defn uuid [] (java.util.UUID/randomUUID))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;

(def user-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

;;;;;;;;;;;;;;;;;;;;;;
;;    ACTIONS
;;;;;;;;;;;;;;;;;;;;;

(defn create [user]
  (let [prepassword  (:prepassword user)
        password     (hashers/derive prepassword env/secret-salt)
        role_id      (Integer/parseInt (:role_id user))
        admin        (contains? user :preadmin)
        clean-user   (dissoc user :prepassword :preadmin)]
     (log/info (format ">>> whole data %s" (merge clean-user {:password password :admin admin :active true :role_id role_id})))
     (-> clean-user
       (assoc :password password :admin admin :active true :role_id role_id)
       (db/create-user!))))

(defn get-user-by-email-and-password [email password]
  (let [password-derived (hashers/derive password env/secret-salt)
        trimmed_email    (clojure.string/trim email)]
    (assoc {} :user (db/get-user-login
                      { :password password-derived :email trimmed_email }))))

(defn get-user [user-id]
  (get @userstore user-id))

(defn get-users [active]
  (db/get-users {:active active}))

(defn get-roles []
  (db/get-roles))
