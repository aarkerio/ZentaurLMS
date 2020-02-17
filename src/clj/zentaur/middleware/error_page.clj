(ns zentaur.middleware.error-page
  (:require [clojure.tools.logging :as log]
            [zentaur.controllers.company-controller :as ccon]))

(defn wrap-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception ex
           ;; (ccon/display-error
            {:status 500 :title "500 - Exception caught"}
           ;; )
           ))))
