(ns zentaur.env
  (:require [clojure.tools.logging :as log]
            [zentaur.test-middleware :refer [wrap-test]]))

(System/setProperty "tika.config" "tika-config.xml")
(System/setProperty "lein.profile" "dev")

(def secret-salt {:salt "1f1c42342bdc1ea6"})

(def defaults
  {:init
   (fn []
     (log/info "\n-=[*** erfolgreich mit dem Entwicklungsprofil gestartet *** ]=-"))
   :stop
   (fn []
     (log/info "\n-=[zentaur wurde erfolgreich heruntergefahren]=-"))
   :middleware wrap-test})  ;; <<--- load dev stuff like code reload
