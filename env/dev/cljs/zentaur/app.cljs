(ns ^:figwheel-no-load zentaur.app
  (:require [zentaur.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(defn main []
   (println " >>>++++  I am in MAIN function!!")
  (.log js/console " >>>>>  I am in MAIN FUNCTION!!!!!")
  (core/init))

(main)
