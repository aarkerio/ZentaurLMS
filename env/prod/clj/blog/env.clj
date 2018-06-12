(ns blog.env
  (:require [clojure.tools.logging :as log]))

(def secret-salt {:salt "1ea6dge64FqTHCSE"})

(def defaults
  {:init
   (fn []
     (log/info "\n-=[Zentaur started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[Zentaur has shut down successfully]=-"))
   :middleware identity})
