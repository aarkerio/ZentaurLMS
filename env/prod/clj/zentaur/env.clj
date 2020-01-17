(ns zentaur.env
  (:require [clojure.tools.logging :as log]))

(System/setProperty "tika.config" "tika-config.xml")
(System/setProperty "lein.profile" "uberjar")

(def secret-salt {:salt "1fcg54arw3dc1ea6"})

(def defaults
  {:init
   (fn []
     (log/info "\n-=[Production zentaur started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[Production zentaur has shut down successfully]=-"))
   :middleware identity})
