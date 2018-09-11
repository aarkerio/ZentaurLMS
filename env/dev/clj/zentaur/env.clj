(ns zentaur.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [zentaur.dev-middleware :refer [wrap-dev]]))

(def secret-salt {:salt "1f1c42342bdc1ea6"})

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[zentaur started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[zentaur has shut down successfully]=-"))
   :middleware wrap-dev})
