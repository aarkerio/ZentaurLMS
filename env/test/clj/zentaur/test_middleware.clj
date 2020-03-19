(ns zentaur.test-middleware
  (:require [zentaur.middleware.error-page :refer [wrap-exception]]))

(defn wrap-test [handler]
  (-> handler
      wrap-exception))
