(ns blog.env
  (:require [clojure.tools.logging :as log]
            [blog.dev-middleware :refer [wrap-dev]]))

(def secret-salt {:salt "1f1c42342bdc1ea6"})

(def defaults
  {:init
   (fn []
     (log/info "\n-=[CMS Zentaur started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[CMS Zentaur has shut down successfully]=-"))
   :middleware wrap-dev})
