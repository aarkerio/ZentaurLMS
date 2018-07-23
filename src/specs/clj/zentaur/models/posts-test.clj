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

;; Generators
(defrecord User [userName userId email active?])

(defrecord Post [title slug body active user_id discution tags created_at])

;; recall that a helper function is automatically generated
;; for us

(-> User "reiddraper" 15 "reid@example.com" true)

(s/conform even? 1000)

;; #user.User{:user-name "reiddraper",
;;            :user-id 15,
;;            :email "reid@example.com",
;;            :active? true}

(def one-post (s/keys :req [::first-name ::last-name ::email]
                        :opt [::updated_at]))

(s/valid? zenraur::post one-post)

RSpec.describe Post, type: :model do
 it "is valid with valid attributes"
   expect(Post.new(title: 'Anything')).to be_valid

  it "is not valid without a title"
  it "is not valid without a description"

