(ns zentaur.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [zentaur.dev-middleware :refer [wrap-dev]]))

(System/setProperty "tika.config" "tika-config.xml")
(System/setProperty "lein.profile" "dev")

(def secret-salt {:salt "1f1c42342bdc1ea6"})

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[*** erfolgreich mit dem Entwicklungsprofil gestartet *** ]=-"))
   :stop
   (fn []
     (log/info "\n-=[zentaur wurde erfolgreich heruntergefahren]=-"))
   :middleware wrap-dev})  ;; <<--- load dev stuff like code reload
