(ns zentaur.factories.test-factory
  (:require [factory-time.core :refer :all]
            [zentaur.factories.shared :as sh]))

(deffactory :test { :title "Test title" :hint "Some hint" :tags "tags" :subject_id 3 :level_id 1 :lang_id 1 })

(deffactory :dtest { :subject_id 3 }
  :extends-factory :test
  :generators {:title (fn [n] (sh/rand-str 15)) ; n starts at 1 and will increase by 1 every time build is called
               :hint (fn [n] (sh/rand-str 15))
               :tags (fn [n] (sh/rand-str 9))
               })

