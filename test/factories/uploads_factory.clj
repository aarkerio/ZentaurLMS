(ns upload-factory
  (:require [factory-time.core :refer :all]
            [zentaur.models.uploads :as uploads]
            [zentaur.models.users :as users]
            [clj-time.local :as l]))

(deffactory :upload {:filename "asdasdasd.pdf" :hashvar "XXX676tyty" :active  true :user_id  1 :tags "history greece"
                     :created_at (l/local-now) :done false})

(deffactory :child {:age 12}
                   :extends-factory :upload
                   :generators {:annoying (fn [n] (even? n)} ; n starts at 1 and will increase by 1 every time build is called
                   :create! uploads/upload-file)

(defn load-user-db []
    (users/create {:fname "Manuel" :lname "Montoya" :uname "mmontoya" :email "some@gloo.com" :admin false  :password "s0m3p4ssw0rd"}))

(defn load-upload-db []
  (let [_       (load-user-db)
        user    (users/get-last)
        user-id (:id user)]
    (uploads/upload-file {:params {:userfile {:filename "some-user-file.pdf" :tempfile "some-user-file.pdf"} :tags "some tag"}} user-id)
