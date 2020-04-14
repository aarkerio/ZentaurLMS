(ns zentaur.controllers.tests-controller-test
  "Integration tests with HTTP calls"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [net.cgrand.enlive-html :as html]
            [ring.mock.request :as mock]
            [zentaur.core :as zc]
            [zentaur.db.core :refer [*db*] :as db]
            [zentaur.handler :as zh]
            [zentaur.config-test :as ct]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'zentaur.config/env
                 #'zentaur.handler/app-routes
                 #'zentaur.routes.services.graphql/compiled-schema
                 #'zentaur.db.core/*db*)
    (f)))

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
        req                    (-> (mock/request :post "/login")
                                   (assoc :headers {"cookie" session})
                                   (assoc :params {:username username
                                                   :password password})
                                   (assoc :form-params {"__anti-forgery-token" csrf}))]
    ((zh/app) req)
    {:csrf csrf :session session}))

(defn test-post-json-map [req-type uri params]
  {:remote-addr "localhost"
   :headers {"host" "localhost"
             "content-type" "application/graphql"
             "accept" "application/json"}
   :server-port 3000
   :content-type "application/graphql"
   :uri uri
   :server-name "localhost"
   :query-string nil
   :body (java.io.ByteArrayInputStream. (.getBytes (json/write-str params)))
   :scheme :http
   :request-method req-type})

(deftest ^:integration get-test-nodes
  (testing "JSON response for the API"
    (let [query     (json/write-str  {:query "{ test_by_uurlid(uurlid: \"b4a98b64267d0c77be85\" archived: false) { uurlid title description } }" })
          response  (assoc ((zh/app) (mock/request :post "http://localhost:3000/api/graphql" query))
                           :headers {:content-type "application/graphql"})
          body  (slurp (:body response))
          _     (log/info (str ">>> response  >>>>> " response "  BODY >> " body))
          jso   (json/read-str body :key-fn keyword)
          title (-> jso :data :test_by_uurlid :title)]
      (is (> (count title) 5)))))

;; curl -X POST -H "Content-Type: application/graphql" --data '{ "query": "{ test_by_uurlid(uurlid: \"a315f83fb49423f7721e\" archived: false) { uurlid title description } }" }' http://localhost:3000/api/graphql

;; (deftest ^:integration a-test
;;    (testing "Test POST request to /api/v1/check returns expected response"
;;      (let [response ((zh/app) (mock/request :post "/api/v1/check" {"str1" "cedewaraaossoqqyt" "str2" "codewars"}))
;;            body     (:body response)]
;;        (is (= (:status response) 200))
;;        (is (= (:msg body) true)))))
;; (defn create-question
;;   "POST /admin/tests/createquestion. JSON response."
;;   [request]
;;   (let [params       (:params request)
;;         user-id      (-> request :identity :id)
;;         new-params   (assoc params :user-id user-id :active true)
;;         response     (model-test/create-question! new-params)]
;;     (response/ok (ches/encode response non-ascii))))


(run-tests)  ;; run tests in this NS
