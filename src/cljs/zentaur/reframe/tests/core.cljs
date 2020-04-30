(ns zentaur.reframe.tests.core
  (:require [cljs.core.async :refer [<! chan close!]]
            [cljs-http.client :as http]
            [cljs.loader :as loader]
            [goog.dom :as gdom]
            [goog.events :as events]
            [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [zentaur.reframe.tests.comment-views :as cviews]    ;; Comments view
            [zentaur.reframe.tests.events :as myevents]    ;; These two are only required to make the compiler
            [zentaur.reframe.tests.subs :as mysubs]        ;; my subscriptions
            [zentaur.reframe.tests.test-views :as tviews])
  (:require-macros [cljs.core.async.macros :as m :refer [go]])
  (:import [goog History]
           [goog.history EventType]))

(re-frame/dispatch
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
;;    Using the sync version of dispatch means that value is in
;; place before we go onto the next step.
(re-frame/dispatch-sync [:initialise-db])

(defn ^:export main
  []
  (when-let [root-app (gdom/getElement "test-root-app")]
    (re-frame/dispatch-sync [:test-load])
    (rd/render [tviews/test-app] root-app))
  (when-let [search-root-app (gdom/getElement "search-root-app")]
    (re-frame/dispatch-sync [:load-search])
    (rd/render  [tviews/search-app] search-root-app))
  (when-let [comments-root-app (gdom/getElement "comments-root-app")]
    (re-frame/dispatch-sync [:load-comments])
    (rd/render [cviews/comments-root-app] comments-root-app)))

(main)
