(ns zentaur.factories.question-factory
  (:require [factory-time.core :as ft]
            [zentaur.factories.shared :as sh]))

(ft/deffactory :question {:subject_id 1 :level_id 1 :lang_id 1 :user_id 1 :question (sh/rand-str 17) :qtype 1 :hint "Hint" :points 1
                       :origin 0 :explanation "Explanation" :fulfill "fulfill" :reviewed_lang false :reviewed_fact false :reviewed_cr false })

(ft/deffactory :second_question {:subject_id 1}
  :extends-factory :question
  :generators {:question    (fn [n] (sh/rand-str 5)) ; n starts at 1 and will increase by 1 every time build is called
               :explanation (fn [n] (sh/random-email))
               :hint        (fn [n] (sh/rand-str 9))
               :fulfill     (fn [n] (sh/rand-str 9))
               })
