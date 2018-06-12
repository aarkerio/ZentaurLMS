(ns ^:figwheel-no-load blog.app
  (:require [blog.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(defn main []
   (println " >>>++++  I am in MAIN function!!")
  (.log js/console " >>>>>  I am in MAIN FUNCTION!!!!!")
  (core/init))

(main)
