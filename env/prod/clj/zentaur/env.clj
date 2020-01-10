(ns zentaur.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[Production zentaur started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[Production zentaur has shut down successfully]=-"))
   :middleware identity})
