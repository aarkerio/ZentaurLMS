(ns zentaur.reframe.libs.db
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

;;;;;      SPECIFICATIONS SECTION    ;;;;;;;;;;;;;;;;;;;;
;; spec for question type
(s/def ::mapkey int?)
(s/def ::id int?)
(s/def ::question string?)
(s/def ::hint string?)
(s/def ::explanation string?)
(s/def ::qtype int?)
(s/def ::points int?)
(s/def ::qform boolean?)

(s/def ::questions (s/map-of ::mapkey (s/keys :req-un [::id ::question ::qtype ::points]
                                              :opt-un [::hint ::explanation])))  ;; :req-un and :opt-un for "required" and "optional" unqualified keys

(s/def ::test (s/and                                        ;; should use the :kind kw to s/map-of (not supported yet)
                 (s/map-of ::id ::question)                 ;; in this map, each todo is keyed by its :id
                 #(instance? PersistentTreeMap %)           ;; is a sorted-map (not just a map)
                 ))

;; spec for Quote type
(s/def ::id     (s/and int? pos?))
(s/def ::quote  (s/and string? #(>= (count %) 8)))
(s/def ::author (s/and string? #(>= (count %) 6)))
(s/def ::total  (s/and int? pos?))
(s/def ::quotes (s/and
                 (s/map-of ::mapkey (s/keys :req-un [::id
                                                     ::quote
                                                     ::author
                                                     ::total]))
                  #(instance? cljs.core/PersistentHashMap %)))

;; search-terms type
(s/def ::db (s/keys :req-un [::quotes ::questions]))

;; -- Default app-db Value  ---------------------------------------------------
;;
;; When the application first starts, this will be the value put in app-db
;; Unless, of course, there are todos in the LocalStore (see further below)
;; Look in:
;;   1.  `core.cljs` for  "(dispatch-sync [:initialise-db])"
;;   2.  `events.cljs` for the registration of :initialise-db handler
;;

(def default-db             ;; what gets put into app-db in the initial load.
  {:test            (hash-map)
   :questions       (hash-map)
   :loading?        false
   :qform           false
   :qcounter        0
   :testform        false
   :subjects        (vector)
   :levels          (vector)
   :comments        (vector)
   :quotes          (hash-map)
   :search-fields   (hash-map)    ;; initial loaded data: langs subjects and levels
   :selected-fields (hash-map)    ;; langs subjects and levels choosed by the user
   :searched-qstios (vector)      ;; Questions that matched the search options
   :selected-qstios (hash-map)    ;; selected elements to create the tests
   })
