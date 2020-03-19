(ns zentaur.factories.test-factory
 (:require [factory-time.core :refer :all]))

(defn fixed-length-password
  ([] (fixed-length-password 8))
  ([n]
   (let [chars-between #(map char (range (int %1) (inc (int %2))))
         chars (concat (chars-between \0 \9)
                       (chars-between \a \z)
                       (chars-between \A \Z)
                       [\_])
         password (take n (repeatedly #(rand-nth chars)))]
     (reduce str password))))

(defn rand-str
  ([] (rand-str 8))
  ([len]
   (clojure.string/lower-case (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))))

(def sites ["net" "com" "mx" "com.mx" "org" "travel"])

(deffactory :user {:fname "Pedro" :lname "Quentin" :uname "77samdddl" :email "77samu@gmail.com" :prepassword (fixed-length-password) :role_id "1"})

(deffactory :admin {:fname "Ludwig" :preadmin "1"}
  :extends-factory :user
  :generators {:uname (fn [n] (rand-str 5)) ; n starts at 1 and will increase by 1 every time build is called
               :email (fn [n] (str (rand-str 5) "@" (fixed-length-password 5) n "." (rand-nth sites)))
               :fname (fn [n] (rand-str 9))
               :lname (fn [n] (rand-str 9))
               })

(deffactory :teacher {:fname "Ludwig"}
  :extends-factory :user
  :generators {:annoying (fn [n] (even? n))})

(deffactory :test {:title "Test title" :hint "Some hint" :tags "tags" :user_id 1 :subject_id "3"})
