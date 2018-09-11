(ns zentaur.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[zentaur started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[zentaur has shut down successfully]=-"))
   :middleware identity})
