(ns zentaur.reframe.core
  (:require [cljs.loader :as loader]
            [goog.dom :as gdom]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [zentaur.reframe.comments.comments-views :as cviews]    ;; Blog Comments component
            [zentaur.reframe.libs.events :as events]
            [zentaur.reframe.libs.forms :as zfms]
            [zentaur.reframe.libs.subs :as mysubs]                  ;; Global subscriptions
            [zentaur.reframe.quotes.quotes-events :as qevents]      ;; Quotes component
            [zentaur.reframe.quotes.quotes-views :as qviews]
            [zentaur.reframe.search.search-events :as seaevents]
            [zentaur.reframe.search.search-views :as seaviews]
            [zentaur.reframe.tests.tests-events :as myevents]       ;; Tests component
            [zentaur.reframe.tests.test-views :as tviews]))

(rf/dispatch
  [::re-graph/init
    {:ws-url                  nil                                 ;; override the websocket url (defaults to /graphql-ws, nil to disable)
     :http-url                "http://localhost:3000/api/graphql" ;; override the http url (defaults to /graphql)
     :http-parameters         {:with-credentials? false           ;; any parameters to be merged with the request, see cljs-http for options
                               :oauth-token "ah4rdSecr3t"
                               :headers {"Content-Type" "application/graphql"}
                               }
     :ws-reconnect-timeout     nil                            ;; attempt reconnect n milliseconds after disconnect (default 5000, nil to disable)
     :resume-subscriptions?   false                           ;; start existing subscriptions again when websocket is reconnected after a disconnect
     :connection-init-payload {}                              ;; the payload to send in the connection_init message, sent when a websocket connection is made
  }])

;; Put an initial value into app-db.
;; The event handler for `:initialise-db` can be found in `events.cljs`
(rf/dispatch-sync [:initialise-db])

(defn ^:export main
  []
  (when-let [root-app (gdom/getElement "test-root-app")]
    (rf/dispatch-sync [:test-load])
    (rd/render [tviews/test-app] root-app))
  (when-let [quotes-root-app (gdom/getElement "quotes-root-app")]
    (rf/dispatch-sync [:load-quotes])
    (rd/render [qviews/quotes-app] quotes-root-app))
  (when-let [search-root-app (gdom/getElement "search-root-app")]
    (rf/dispatch-sync [:load-search])
    (rd/render [seaviews/search-app] search-root-app))
  (when-let [comments-root-app (gdom/getElement "comments-root-app")]
    (rf/dispatch-sync [:load-comments])
    (rd/render [cviews/comments-root-app] comments-root-app)))

(main)
