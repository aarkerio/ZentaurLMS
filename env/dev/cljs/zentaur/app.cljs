(ns ^:figwheel-no-load zentaur.app
  (:require [zentaur.core :as core]
            [zentaur.tests.core :as tcore]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(defn main []
  (println " >>>++++  I am in MAIN app function in the launcher file PATH: env/dev/cljs/zentaur/app.cljs !!")
  ;; (.log js/console " >>>>>  I am in app MAIN FUNCTION!!!!!")
  (core/init))

(main)
