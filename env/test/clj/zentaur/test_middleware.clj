(ns zentaur.test-middleware
  (:require [zentaur.middleware.error-page :refer [wrap-error-page]]))

(defn wrap-test [handler]
  (wrap-error-page handler))
