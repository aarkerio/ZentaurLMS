(ns zentaur.handler-test
  (:require [clojure.test :as ct]   ;; [deftest testing is run-tests]
            [muuntaja.core :as m]
            [mount.core :as mount]
            [ring.mock.request :refer :all]
            [zentaur.handler :refer :all]
            [zentaur.middleware.formats :as formats]))

;; (defn parse-json [body]
;;   (m/decode formats/instance "application/json" body))

;; (ct/use-fixtures
;;   :once
;;   (fn [f]
;;     (mount/start #'zentaur.config/env
;;                  #'zentaur.handler/app)
;;     (f)))

;; (ct/deftest test-app
;;   (ct/testing "main route"
;;     (let [response (app (request :get "/"))]
;;       (ct/is (= 200 (:status response)))))

;;   (ct/testing "not-found route"
;;     (let [response (app (request :get "/invalid"))]
;;       (ct/is (= 404 (:status response))))))
