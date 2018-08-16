(ns zentaur.factory.user-factory
 (:require [factory-time.core :refer :all]))

(deffactory :person {:fname "Perro" :lname "Aguayo", :uname "perrin" :password "s0m3p4ss" :email "perrog@gmail.com" :admin true})

(deffactory :user {:fname "Gato"}
  :extends-factory :person
  :generators {:email (fn [n] (str "foo" n "")} ; n starts at 1 and will increase by 1 every time build is called
  :create! save-user!)

(deffactory :child {:age 12}
  :extends-factory :person
  :generators {:annoying (fn [n] (even? n)} ; n starts at 1 and will increase by 1 every time build is called
  :create! save-child!)
