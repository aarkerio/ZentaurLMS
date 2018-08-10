(ns zentaur.hiccup_templating.layout-view
  (:require [zentaur.hiccup_templating.helpers-view :as helpers]
            [hiccup.form :as f]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn application [content]
  (def vector-atom (atom (helpers/nav-links)))
  (when-let [email (-> content :identity :email)]
    (swap! vector-atom conj [:li {:class "nav-item"} [:a {:href "/admin/users" :class "nav-link"} "Benutzer"]]
                            [:li {:class "nav-item"} [:a {:href "/admin/posts" :class "nav-link"} "Beiträge"]]
                            [:li {:class "nav-item"} [:a {:href "/admin/uploads" :class "nav-link"} "Dateien"]]
                            [:li (str "Hallo" email "!")]
                            [:li {:class "nav-item"} [:a {:href "/logout" :class "nav-link"} "Logout"]] ))

  (html5 [:head
          [:title (str ":: Zentaur :: Quizz Test for you " (:title content))]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
          (include-js  "//cdnjs.cloudflare.com/ajax/libs/tether/1.4.3/js/tether.min.js")
          (include-js  "https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js")
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/zentaur.css")
          (include-css "/css/style.css")
          (include-js  "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.bundle.min.js")
		      (include-css "http://fonts.googleapis.com/css?family=Open+Sans:400italic,700italic,400,700,300&amp;subset=latin,latin-ext")
		      (include-css "http://fonts.googleapis.com/css?family=Raleway:700,400,300")

         [:body
           (when-let [flash (:flash content)]
             (helpers/success-flash flash))
           [:nav {:class "navbar navbar-toggleable-md navbar-light bg-faded"}
              [:button {:class "navbar-toggler navbar-toggler-right" :type "button" :data-toggle "collapse" :data-target "#navbarSupportedContent"
                              :aria-controls "navbarSupportedContent" :aria-expanded "false" :aria-label "Toggle navigation"}
                      [:span {:class "navbar-toggler-icon"}]]
              [:a {:class "navbar-brand" :href "/"} "Home"]
            [:div {:class"collapse navbar-collapse" :id "navbarSupportedContent"}
                  [:ul {:class"navbar-nav mr-auto"} (for [link @vector-atom] link) ]
                  (when-not (-> content :identity :email)
                    (f/form-to [:post "/login" {:class "form-inline my-2 my-lg-0"}]
                      (f/hidden-field { :value (:csrf-field content)} "__anti-forgery-token")
                      (f/password-field { :class "form-control mr-sm-2" :placeholder "password" } :password )
                      (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
                      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung")))]]
           [:div {:class "blog-header"}
             [:div {:class "container"}
               [:h1 {:class "blog-title" :id "blogtitle"} "Zentaur"]
               [:p  {:class "lead blog-description"} "Tausende von Fragen bereit zu verwenden."]]]

          [:div {:class "container"}  (:contents content)]]
          [:footer {:class "blog-footer"}
            [:img {:src "/img/warning_clojure.png" :alt "Lisp" :title "Lisp"}]
            [:p "Chipotle Software &copy; 2018. MIT License."]
            [:p [:a {:href "#"} "Back to top"]]]
          (include-js "http://0.0.0.0:3449/js/app.js")
          [:div {:id "root-app"} ""]]))

