(ns blog.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [myapp.core-test]))

(doo-tests 'myapp.core-test)

