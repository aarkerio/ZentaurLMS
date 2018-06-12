(ns blog.models.posts-test
  (:require [clojure.spec :as s]
            [clojure.test :as t]
            [clojure.spec.test :as stest]))

(t/deftest basic-tests
  (t/testing "it says hello to everyone"
    (t/is (= (with-out-str (sut/-main)) "Hello, World!\n"))))

;; Generator
(def peg? #{:y :g :r :c :w :b})
(s/def ::code (s/coll-of peg? :min-count 4 :max-count 6))
(s/fdef score
        :args (s/cat :secret ::code :guess ::code))


;; Generator
(defrecord User [userName userId email active?])

;; recall that a helper function is automatically generated
;; for us

(-> User "reiddraper" 15 "reid@example.com" true)

;; #user.User{:user-name "reiddraper",
;;            :user-id 15,
;;            :email "reid@example.com",
;;            :active? true}



