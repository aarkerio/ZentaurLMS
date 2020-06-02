(ns zentaur.factories.test-factory
  (:require [factory-time.core :refer :all]
            [zentaur.factories.shared :as sh]))

(def sites ["net" "com" "mx" "com.mx" "org" "travel"])

(deffactory :user {:fname "Pedro" :lname "Quentin" :uname "77samdddl" :email "77samu@gmail.com" :prepassword (fixed-length-password) :role_id "1"})

(deffactory :admin {:fname "Ludwig" :preadmin "1"}
  :extends-factory :user
  :generators {:uname (fn [n] (rand-str 5)) ; n starts at 1 and will increase by 1 every time build is called
               :email (fn [n] (str (rand-str 5) "@" (fixed-length-password 5) n "." (rand-nth sites)))
               :fname (fn [n] (rand-str 9))
               :lname (fn [n] (rand-str 9))
               })

(deffactory :teacher {:fname "Ludwig"}
  :extends-factory :user
  :generators {:annoying (fn [n] (even? n))})

(deffactory :test {:title "Test title" :hint "Some hint" :tags "tags" :user_id 1 :subject_id "3"})
