(ns zentaur.factories.shared
 (:require [factory-time.core :refer :all]))

(def sites ["net" "com" "mx" "com.mx" "org" "travel"])

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

(defn random-email
  ([]  (random-email 8))
  ([n] (str (rand-str n) "@" (fixed-length-password n) n "." (rand-nth sites))))
