(ns zentaur.middleware.error-page
  (:require [clojure.tools.logging :as log]
            [zentaur.controllers.company-controller :as ccon]))

(defn strutu []
"<body>
                  <h1>Something very bad has happened!</h1>
                  <p>We've dispatched a team of highly trained gnomes to take care of the problem.</p>
                </body>"
  )

(defn handle-template-parsing-error [ex]
  (let [{:keys [type error-template] :as data} (ex-data ex)] ;; (ex-data) Returns exception data (a map) if ex is an IExceptionInfo. Otherwise returns nil.
    (log/info (str ">>> TYPEEEEEE >>>>> " type))
    (if-not (= "selmer/validation-error" type)
      {:status  500
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body    (strutu)}
      (throw ex))))


(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        {:status 400 :body "Invalid data"}))))

(defn wrap-error-page [handler]
  (log/info (str ">>> handler HANDLERRRRR >>>>> " handler))
  (fn [req]
    (try
      (handler req)
      (log/info (str ">>>  .REQQQQQQQQQQQQQQQQQQQQQQ >>>>> " req))
      (catch Throwable t
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body "<body>
                  <h1>Something very bad has happened!</h1>
                  <p>We've dispatched a team of highly trained gnomes to take care of the problem.</p>
                </body>"}
        ))))

(defn wrap-error-page-backup
  "Dev middleware for rendering an error page"
  [handler]
  (log/info (str ">>> handler HANDLERRRRR >>>>> " handler))
  (fn
    ([request]
     (try
       (log/info (str ">>> middleware.error-page one arity >>>>> " request))
       (handler request)
       (catch clojure.lang.ExceptionInfo ex
         (handle-template-parsing-error ex))))
    ([request respond raise]
     (try
       (log/info (str ">>> middleware.error-page multi MULTI arity >>>>> " request))
       (handler request respond raise)
       (catch clojure.lang.ExceptionInfo ex
         (respond (handle-template-parsing-error ex)))))))
