(ns ^:test-model zentaur.libs.specs.shared
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::name string?)
(spec/def ::id int?)
(spec/def ::age int?)
(spec/def ::skills list?)

(spec/def ::new-test (spec/keys :req [::name ::age ::id]
                                :opt [::skills]))

(defn validate-test [params]
  (spec/valid? ::developer params))

(defn my-inc [x]
  (inc x))

(spec/fdef my-inc
      :args (spec/cat :x number?)
      :ret number?)
