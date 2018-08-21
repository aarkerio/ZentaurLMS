(ns zentaur.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [clojure.tools.logging :as log]
            [mount.core :refer [args defstate]]))

(log/info (str ">>>  PROPS >>>>> " (source/from-system-props)))

(log/info (str ">>> ARGS >>>>> " (args)))

(log/info (str ">>> ENVS >>>>> " (source/from-env)))

(defstate env :start (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)]) )

(log/info (str ">>> LOAD CONFIG >>>>> " (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)])))
