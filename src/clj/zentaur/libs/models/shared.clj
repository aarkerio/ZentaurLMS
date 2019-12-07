(ns zentaur.libs.models.shared
  (:require [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn get-last-id [table]
  (db/clj-generic-last-id {:table-name table}))


(defmacro with-resources [[var expr & other :as resources]
                          body cleanup-block
                          [error-name error-block :as error-handler]]
  (if (empty? resources)
    `(try ~body
          (catch Throwable e#
            (let [~error-name e#] ~error-block))
          (finally ~cleanup-block))
    `(try
       (let ~[var expr]
         (with-resources ~other ~body ~cleanup-block ~error-handler))
       (catch Throwable e#
         (let ~(vec (interleave (take-nth 2 resources)
                                (repeat nil)))
           ~cleanup-block
           (let [~error-name e#] ~error-block))))))

