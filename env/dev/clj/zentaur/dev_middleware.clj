(ns zentaur.dev-middleware
  (:require [prone.middleware :refer [wrap-exceptions]]    ;; the Rails like screen
            [ring.middleware.reload :refer [wrap-reload]]
            [zentaur.middleware.error-page :refer [wrap-error-page wrap-exception-handling]]))

(defn wrap-dev [handler]
  (-> handler
      wrap-reload
      wrap-error-page
      wrap-exception-handling))
