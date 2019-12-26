(ns zentaur.hiccup.layout-view
  (:require [zentaur.hiccup.helpers-view :as helpers]
            [hiccup.form :as f]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn- ^{:private true} login-form
  "Html helper"
  [csrf-field]
  [:div {:class "form-box"}
    [:div {:class "login-form"}
    [:form {:method "post" :action "/login" }
      [:div {:class "login-form-group"}
        (f/hidden-field { :value csrf-field } "__anti-forgery-token")
        (f/email-field {:class "field-form" :maxlength 50 :size 20 :placeholder "Email"} "email")]
      [:div {:class "login-form-group"}
        (f/password-field {:class "field-form" :maxlength 50 :size 10 :placeholder "Password"} "password")]
      [:div {:class "login-form-group"}
        (f/submit-button  {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung")]]]])

(defn application [content]
  (def vector-atom (atom (helpers/nav-links)))
  (when-let [email (-> content :identity :email)]
    (swap! vector-atom conj [:li {:class "nav-item"} [:a {:href "/admin/users" :class "nav-link"} "Benutzer"]]
                            [:li {:class "nav-item"} [:a {:href "/admin/posts" :class "nav-link"} "Beiträge"]]
                            [:li {:class "nav-item"} [:a {:href "/admin/tests" :class "nav-link"} "Quiztest"]]
                            [:li {:class "nav-item"} [:a {:href "/admin/uploads" :class "nav-link"} "Dateien"]]
                            [:li {:class "nav-item"} (str "Hallo" email "!")]
                            [:li {:class "nav-item"} [:a {:href "/logout" :class "nav-link"} "Logout"]] ))

  (html5 [:head
          [:title (str ":: Zentaur :: Easy Quizz Tests for you " (:title content))]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
          (include-js  "//cdnjs.cloudflare.com/ajax/libs/tether/1.4.3/js/tether.min.js")
          (include-js  "https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js")
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/zentaur.css")
          (include-css "/css/styles.css")
          (include-js  "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.bundle.min.js")]

          [:body
            (when-not (-> content :identity :email)
                  (login-form (:csrf-field content)))
            (when-let [flash (:flash content)]
             (helpers/display-flash flash))
            [:div {:class "blog-header"}
                [:div {:class "blog-title" :id "blogtitle"} "Zentaur"]
                [:div {:class "blog-description"} "Tausende von Fragen bereit zu verwenden."]]
            [:nav {:class "navbar navbar-toggleable-md navbar-light bg-faded"}
             [:button {:class "navbar-toggler navbar-toggler-right" :type "button" :data-toggle "collapse" :data-target "#navbarSupportedContent"
                        :aria-controls "navbarSupportedContent" :aria-expanded "false" :aria-label "Toggle navigation"}
                      [:span {:class "navbar-toggler-icon"}]]
              [:a {:class "navbar-brand" :href "/"} "Home"]
              [:div {:class"collapse navbar-collapse" :id "navbarSupportedContent"}
                [:ul {:class"navbar-nav mr-auto"} (for [link @vector-atom] link) ]]]
           [:div {:class "top-banner"}]
            [:div {:class "container"}  (:contents content)]
          [:div {:class "blog-footer" :id "footer"}
              [:img {:src "/img/warning_clojure.png" :alt "Lisp" :title "Lisp"}]
             [:p "Chipotle Software &copy; 2018-2020. MIT License."]
              [:p [:a {:href "#"} "Back to top"]]]
          (include-js "/cljs-out/dev-main.js")
          [:div {:id "root-app"} ""]]))
