

curl -X POST -H "Content-Type: application/json" --data '{ "query": "{questions_by_test(id: 5) {id question qtype explanation}}" }' http://localhost:8888/graphql

curl -X POST -H "Content-Type: application/graphql" --data '{questions_by_test(id: 5) {id question qtype explanation}}' http://localhost:8888/graphql


{:identity
 {:id 2, :first_name "Manuel", :last_name "Montoya", :email "admin@example.com", :admin true}, :ssl-client-cert nil,
 :cookies {"_ga" {:value "GA1.1.1430590123.1509039336"}, "ring-session" {:value "613d8c50-94b9-4d39-9b46-934595c51c3e"}},
 :remote-addr "0:0:0:0:0:0:0:1",
 :params {:__anti-forgery-token "J9mT88Jx65YYXBwvQ7NDAeBkJvaiMMCw2m1dMe5VZpFrXHihhFQmHUjhVxLenGJig1y5qNv9lvskJImK",
          :userfile {:filename "america_first.jpg", :content-type "image/jpeg",
                     :tempfile #object[java.io.File 0x7aaea209 "/tmp/ring-multipart-1250863223928940546.tmp"],
                     :size 355969}},
 :flash nil,
 :handler-type :undertow,
 :route-handler #function[compojure.core/wrap-response/fn--14956], :route-params {},
 :headers
 {"origin" "http://localhost:3000",
  "host" "localhost:3000", "user-agent" "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36", "content-type" "multipart/form-data; boundary=----WebKitFormBoundaryYMgySMqeqtxnSFVg", "cookie" "_ga=GA1.1.1430590123.1509039336; ring-session=613d8c50-94b9-4d39-9b46-934595c51c3e", "content-length" "356351", "referer" "http://localhost:3000/about", "connection" "keep-alive", "upgrade-insecure-requests" "1", "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8", "accept-language" "en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7,es;q=0.6,fr;q=0.5,it;q=0.4,nl;q=0.3,pt;q=0.2,ru;q=0.1,en-CA;q=0.1", "accept-encoding" "gzip, deflate, br", "cache-control" "max-age=0"}, :server-port 3000, :content-length 356351, :form-params {}, :compojure/route [:post "/upload"], :session/key "613d8c50-94b9-4d39-9b46-934595c51c3e", :server-exchange #object[io.undertow.server.HttpServerExchange 0x7056c979 "HttpServerExchange{ POST /upload request {Accept=[text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8], Accept-Language=[en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7,es;q=0.6,fr;q=0.5,it;q=0.4,nl;q=0.3,pt;q=0.2,ru;q=0.1,en-CA;q=0.1], Cache-Control=[max-age=0], Accept-Encoding=[gzip, deflate, br],
Origin=[http://localhost:3000],
User-Agent=[Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36], Connection=[keep-alive], Content-Length=[356351], Content-Type=[multipart/form-data; boundary=----WebKitFormBoundaryYMgySMqeqtxnSFVg], Cookie=[_ga=GA1.1.1430590123.1509039336; ring-session=613d8c50-94b9-4d39-9b46-934595c51c3e], Referer=[http://localhost:3000/about], Upgrade-Insecure-Requests=[1], Host=[localhost:3000]} response {Server=[undertow]}}"], :query-params {}, :content-type "multipart/form-data; boundary=----WebKitFormBoundaryYMgySMqeqtxnSFVg", :path-info "/upload", :character-encoding "ISO-8859-1", :context "", :uri "/upload", :server-name "localhost", :query-string nil, :body #object[io.undertow.io.UndertowInputStream 0x35544917 "io.undertow.io.UndertowInputStream@35544917"], :multipart-params {"__anti-forgery-token" "J9mT88Jx65YYXBwvQ7NDAeBkJvaiMMCw2m1dMe5VZpFrXHihhFQmHUjhVxLenGJig1y5qNv9lvskJImK", "userfile" {:filename "america_first.jpg", :content-type "image/jpeg", :tempfile #object[java.io.File 0x7aaea209 "/tmp/ring-multipart-1250863223928940546.tmp"], :size 355969}}, :scheme :http, :request-method :post, :route-middleware #function[clojure.core/comp/fn--5529], :session {:ring.middleware.anti-forgery/anti-forgery-token "J9mT88Jx65YYXBwvQ7NDAeBkJvaiMMCw2m1dMe5VZpFrXHihhFQmHUjhVxLenGJig1y5qNv9lvskJImK", :identity {:id 2, :first_name "Manuel", :last_name "Montoya", :email "admin@example.com", :admin true}}}


{:64 {:explanation "s fadsf adsf dsfs",
      :ordnen 1,
      :reviewed_fact false,
      :question "dsfdsfadsfadsf",
      :hint "adsfdsa fadsf ",
      :qtype 1,
      :updated_at nil,
      :reviewed_cr false,
      :active true,
      :id 64,
      :answers [],
      :user_id 4,
      :created_at "2018-10-16 17:52:10",
      :reviewed_lang false},
 :70 {:explanation "j ghj ghjgh",
      :ordnen 2,
      :reviewed_fact false,
      :question "dfsgdfsgdfsgdfsgdfs",
      :user_id 4,
      :created_at "2018-10-18 19:28:11",
      :reviewed_lang false}}

{
  "data": {
    "questions_by_test": [
      {
        "id": 65,
        "question": "dsfadsfadsf EDITEDsdfdsgdfg  8888 dsdsgdf  PERRO",
        "qtype": 1,
        "explanation": "adsfadsfadsf EDITEDdfgdfg88888 df df df gdf  PERRO"
      },
      {
        "id": 66,
        "question": "Lorem Ipsum doloret amen",
        "qtype": 1,
        "explanation": "Lorem Ipsum doloret amen"
      },
      {
        "id": 67,
        "question": "Re-frame 0.8 introduced a subscription cache, meaning that multiple calls to subscribe with the same arguments will return the same identical reaction.",
        "qtype": 2,
        "explanation": "Re-frame 0.8 introduced a subscription cache, meaning that multiple calls to subscribe with the same arguments will return the same identical reaction."
      },
      {
        "id": 68,
        "question": "This tutorial explains the underlying reactive mechanism ",
        "qtype": 2,
        "explanation": "This tutorial explains the underlying reactive mechanism "
      },
      {
        "id": 69,
        "question": "Re-frame 0.8 introduced a subscription cache, meaning that multiple calls to subscribe with the same arguments will return the same identical reaction.",
        "qtype": 2,
        "explanation": "Re-frame 0.8 introduced a subscription cache, meaning that multiple calls to subscribe with the same arguments will return the same identical reaction."
      }
    ]
  }
}

