

curl -X POST -H "Content-Type: application/json" --data '{ "query": "{questions_by_test(id: 5) {id question qtype explanation}}" }' http://localhost:8888/graphql

curl -X POST -H "Content-Type: application/graphql" --data '{questions_by_test(id: 5) {id question qtype explanation}}' http://localhost:8888/graphql


Ring defaults:
{:params {:urlencoded true, :multipart true, :nested true, :keywordize true},
 :cookies true,
 :session {:flash true, :cookie-attrs {:http-only true, :same-site :strict}},
 :security {:anti-forgery true, :xss-protection {:enable? true, :mode :block}, :frame-options :sameorigin, :content-type-options :nosniff},
 :static {:resources "public"},
 :responses {:not-modified-responses true, :absolute-redirects true, :content-types true, :default-charset "utf-8"}}

Request:
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
 :route-handler #function[compojure.core/wrap-response/fn--14956], :route-params {}}
