(ns zentaur.run-all
  "Runs all the tests"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [zentaur.controllers.tests-controller-test :as tct]
            [zentaur.models.tests-test :as zmt]
            [zentaur.models.users-test :as umt]))


(use-fixtures
  :once
  (fn [f]
    (mount/start #'zentaur.config/env
                 #'zentaur.handler/app-routes
                 #'zentaur.routes.services.graphql/compiled-schema
                 #'zentaur.db.core/*db*)
    (f)))

(defn run-all []
  (zmt/run-tests))
