(ns person-factory
 (:require [factory-time.core :refer :all]))

(deffactory :person {:name "Billy Joe", :age 42})
(deffactory :child {:age 12}
  :extends-factory :person
  :generators {:annoying (fn [n] (even? n)} ; n starts at 1 and will increase by 1 every time build is called
  :create! save-child!)
