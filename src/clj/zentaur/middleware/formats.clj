(ns zentaur.middleware.formats
  (:require
    [cognitect.transit :as transit]
    [luminus-transit.time :as time]
    [muuntaja.core :as m]))   ;; Clojure library for fast http api format negotiation, encoding and decoding.

(def instance
  (m/create
    (-> m/default-options
        (update-in
          [:formats "application/transit+json" :decoder-opts]
          (partial merge time/time-deserialization-handlers))
        (update-in
          [:formats "application/transit+json" :encoder-opts]
          (partial merge time/time-serialization-handlers)))))
