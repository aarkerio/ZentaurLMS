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
        admin        (contains? user :preadmin)
        clean-user   (dissoc user :prepassword)]
    (log/info (str ">>> clean-user >>>>> " clean-user))
     (-> clean-user
       (assoc :password password :admin admin :active true :group_id 1)
       (db/create-user!))))

(defn get-user [user-id]
  (get @userstore user-id))

(defn get-user-by-email-and-password [email password]
  (let [password-derived (hashers/derive password env/secret-salt)
        trimmed_email    (clojure.string/trim email)]
    (assoc {} :user (db/get-user-login
                      { :password password-derived :email trimmed_email }))))

(defn get-users [active]
  (db/get-users {:active active}))

(defn get-roles []
  (db/get-roles))
