(ns zentaur.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [zentaur.core-test]))

(doo-tests 'zentaur.core-test)

