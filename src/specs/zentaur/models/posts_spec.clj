(ns blog.models.posts-spec
  (:require [clojure.repl :as repl]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :as t]))


;; (t/run-tests 'your.namespace 'some.other.namespace)

(s/valid? #(> % 5) 10)

(t/is (= 5 (+ 2 2)))

(s/valid? nil? nil)

(s/def ::date inst?)

(gen/sample (s/gen string?))

(repl/doc ::date)

;;(s/def ::big-even (s/and int? even? #(> % 1000)))

;; (s/valid? ::big-even 10000)

;; (s/explain ::big-even 5)

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))


(stest/instrument `ranged-rand)


(stest/check `ranged-rand)

(s/valid? ::person
  {::first-name "Elon"
   ::last-name "Musk"
   ::email "elon@example.com"})

(s/assert)

(def recipe {::ingredients [1 :kg "aubergines"
                            20 :ml "soy_souce"]
             ::steps ["fry the aubergines"
                      "add the soy souce"]})

(s/valid? map? recipe)

(defn cook [recipe]
  ;;;
  )

(cook recipe)

