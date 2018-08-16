(ns zentaur.factory.uploads-factory
  (:require [factory-time.core :refer :all]
            [zentaur.models.users :as users]
            [clj-time.local :as l]))

(deffactory :upload {:filename "asdasdasd.pdf" :hashvar "XXX676tyty" :active  true :user_id  1 :tags "history greece"
                     :created_at (l/local-now) :done false})

(deffactory :child {:age 12}
                   :extends-factory :upload
                   :generators {:annoying (fn [n] (even? n)} ; n starts at 1 and will increase by 1 every time build is called
                   :create! uploads/upload-file))
