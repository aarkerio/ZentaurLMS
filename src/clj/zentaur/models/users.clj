(ns zentaur.models.users
  (:require [buddy.hashers :as hashers]
            [clojure.tools.logging :as log]
            [struct.core :as st]
            [zentaur.db.core :as db]
            [zentaur.env :as env]
            [zentaur.models.validations.validations-user :as vu]))

(defn gen-uuid [] (java.util.UUID/randomUUID))

;;;;;;;;;;;;;;;;;;;;;;
;;    ACTIONS
;;;;;;;;;;;;;;;;;;;;;

(defn create-user [user]
  (let [prepassword  (:prepassword user)
        password     (hashers/derive prepassword env/secret-salt)
        uuid         (gen-uuid)
        role_id      (Integer/parseInt (:role_id user))
        admin        (contains? user :preadmin)
        clean-user   (dissoc user :prepassword :preadmin)
        final-data   (assoc clean-user :password password :admin admin :active true :role_id role_id :uuid uuid)
        _            (log/info (str ">>> final-data >>>>> " final-data))
        validation   (vu/validate-user final-data)]
    (if (nil? validation)
      (db/create-user! final-data)
      (str "Validation errors : " validation))))

(defn create [user]
  (let [email       (:email user)
        chk-user    (db/get-user {:id 0 :email email})]
     (log/info (str ">>> Whole User chk-user data: " chk-user))
     (if (nil? chk-user)
       (create-user user)
       (format "The email  %s already exists." email))))

(defn get-user-by-email-and-password
  [email password]
  (let [password-derived (hashers/derive password env/secret-salt)
        trimmed_email    (clojure.string/trim email)]
    (assoc {} :user (db/get-user-login
                     {:password password-derived :email trimmed_email}))))

(defn get-users [active]
  (db/get-users {:active active}))

(defn get-roles []
  (db/get-roles))

(defn destroy [id]
  "Not a real delete, just set it to inactive "
  (let [int-id (Integer/parseInt id)]
    (db/delete-user! {:id int-id})))
