(ns zentaur.controllers.tests-controller-test
  "Integration tests with HTTP calls"
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [net.cgrand.enlive-html :as html]
            [ring.mock.request :as mock]
            [zentaur.db.core :refer [*db*] :as db]
            [zentaur.handler :as zh]
            [zentaur.config-test :as ct]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'zentaur.config/env
                 #'zentaur.handler/app-routes
                 #'zentaur.db.core/*db*)
    (f)))

;; (defn another-fixture [f]
;;         (create-db-table)
;;         (f)
;;   (drop-db-table))

;; (use-fixtures
;;   :once
;;   (fn [f]
;;     (mount/start
;;      #'zentaur.config/env
;;      #'zentaur.db.core/*db*)
;;     (migrations/migrate ["migrate"] (select-keys env [:database-url]))
;;     (f)))


(defn get-session
  "Given a response, grab out just the key=value of the ring session"
  [resp]
  (let [headers         (:headers resp)
        cookies         (get headers "Set-Cookie")
        session-cookies (first (filter #(.startsWith % "ring-session") cookies))
        session-pair    (first (clojure.string/split session-cookies #";"))]
    session-pair))

(defn get-csrf-field
  "Given an HTML response, parse the body for an anti-forgery input field"
  [resp]
  (-> (html/select (html/html-snippet (:body resp)) [:input#__anti-forgery-token])
      first
      (get-in [:attrs :value])))

(defn get-login-session!
  "Fetch a login page and return the associated session and csrf token"
  []
  (let [resp ((zh/app) (mock/request :get "/login"))]
    {:session (get-session resp)
     :csrf (get-csrf-field resp)}))

(defn login!
  "Login a user given a username and password"
  [username password]
  (let [{:keys [csrf session]} (get-login-session!)
        _                      (log/info (str ">>> session  >>>>> " session "  und csrf >>>>>> " csrf))
        req                    (-> (mock/request :post "/login")
                                   (assoc :headers {"cookie" session})
                                   (assoc :params {:username username
                                                   :password password})
                                   (assoc :form-params {"__anti-forgery-token" csrf}))]
    ((zh/app) req)
    session))

(deftest ^:integration get-test-nodes
  (testing "JSON response for the API"
    (let [session    (login! "admin@example.com" "password")
          _          (log/info (str ">>> ** RESPONSE ** >>>>> " (prn-str session)))
          response   (-> ((zh/app) (mock/request :get "/admin/tests"))
                         (assoc :headers {"cookie" session}))
          body  (:body response)
          ]
      (log/info (str ">>> ***** RETURN >>>>> "  response))
      (is (= (:msg body) true))
      )))

;; (deftest ^:integration a-test
;;    (testing "Test POST request to /api/v1/check returns expected response"
;;      (let [response ((zh/app) (mock/request :post "/api/v1/check" {"str1" "cedewaraaossoqqyt" "str2" "codewars"}))
;;            body     (:body response)]
;;        (is (= (:status response) 200))
;;        (is (= (:msg body) true)))))

;; (run-tests)  ;; run tests in this NS

;; (defn create-question
;;   "POST /admin/tests/createquestion. JSON response."
;;   [request]
;;   (let [params       (:params request)
;;         user-id      (-> request :identity :id)
;;         new-params   (assoc params :user-id user-id :active true)
;;         response     (model-test/create-question! new-params)]
;;     (response/ok (ches/encode response non-ascii))))
