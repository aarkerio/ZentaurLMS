(ns zentaur.factories.user-factory
  (:require [factory-time.core :refer :all]
            [talltale.core :as tac]
            [zentaur.factories.shared :as sh]))

(def person (tac/person))

(deffactory :user {:fname (:first-name person) :lname (:last-name person) :uname "77samdddl" :email (sh/random-email) :prepassword "s0m3p4ss" :role_id "1"})

(deffactory :admin {:fname "Ludwig" :preadmin "true"}
  :extends-factory :user
  :generators {:uname (fn [n] (sh/rand-str 5)) ; n starts at 1 and will increase by 1 every time build is called
               :email (sh/random-email)
               :fname (fn [n] (sh/rand-str 9))
               :lname (fn [n] (sh/rand-str 9))
               })

(deffactory :teacher {:fname "Ludwig"}
  :extends-factory :user
  :generators {:annoying (fn [n] (even? n))})
