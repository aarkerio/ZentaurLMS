(ns zentaur.middleware.error-page
  (:require [clojure.tools.logging :as log]
            [zentaur.hiccup.layouts.error-layout :as el] ))

(defn handle-template-parsing-error [ex]
  (let [{:keys [type error-template] :as data} (ex-data ex)]
      {:status  500
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (el/application data)}
      (throw ex)))

(defn wrap-error-page
  "Dev middleware for rendering an error page "
  [handler]
  (fn
    ([request]
     (try
       (handler request)
       (catch clojure.lang.ExceptionInfo ex
         (handle-template-parsing-error ex))))
    ([request respond raise]
     (try
       (handler request respond raise)
       (catch clojure.lang.ExceptionInfo ex
         (respond (handle-template-parsing-error ex)))))))
