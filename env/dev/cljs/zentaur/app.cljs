(ns ^:figwheel-no-load zentaur.app
  (:require [zentaur.core :as core]
            [cljs.spec.alpha :as s]
            [expound.alpha :as expound]
            [devtools.core :as devtools]))

(set! s/*explain-out* expound/printer)  ;; Human-optimized error messages for clojure.spec

(devtools/install!)     ;; we love https://github.com/binaryage/cljs-devtools
(enable-console-print!) ;; so that println writes to `console.log`

(core/init)


