(ns mjolnir.schemas.person
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn gen-uuid-str []
  (-> (java.util.UUID/randomUUID)
      str))

(def non-blank-str?
  (fn [s]
    (not (clojure.string/blank? s))))

(def str-with-gen (s/with-gen string?
                        #(gen/such-that non-blank-str?
                                        (gen/string-alphanumeric))))

(def email-regex #"^\S+@\S+\.\S+$")
(s/def ::email-formatter (s/and string? #(re-matches email-regex %)))

(s/def ::_id (s/with-gen string?
               #(gen/fmap (fn [s] (gen-uuid-str)) ;; discard gen string
                          (gen/string-alphanumeric))))
(s/def ::first-name str-with-gen)
(s/def ::last-name  str-with-gen)
(s/def ::email (s/with-gen ::email-formatter
                 #(gen/fmap (fn [[user host tld]] (str user "@" host "." tld))
                            (gen/tuple (gen/such-that non-blank-str? (gen/string-alphanumeric))
                                       (gen/such-that non-blank-str? (gen/string-alphanumeric))
                                       (gen/such-that non-blank-str? (gen/string-alphanumeric))))))
;;(s/def ::email string?)
(s/def ::phone int?)

(s/def ::person (s/keys :req [::_id ::first-name ::last-name ::email]
                        :opt [::phone]))

