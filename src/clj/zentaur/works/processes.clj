(ns zentaur.works.processes
  (:require [clojure.tools.logging :as log]
   ;; [farmhand.core :as farmhand]
            ))

;; STEP 3: Jobs are regular ol' Clojure functions:
;; (defn my-long-running-function
;;   [a b]
;;   (println "starting long-running function")
;;   (Thread/sleep 20000)
;;   (println "exiting long-running function")
;;   {:farmhand/result (* a b)})

;; ;; STEP 4: Queue that job! It will be processed by the running Farmhand server.
;; (farmhand/enqueue {:fn-var #'my-long-running-function
;;                    :args [1 2]})

