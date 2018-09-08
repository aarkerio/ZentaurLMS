
# Clojure for Rails Developer with Luminus#

Ruby is one of the smartests OO languages out there and Rails is a solid, fun, full featured framework. I enjoy coding with Ruby and I think I'll be doing it for many years to come.

But it's time to try something new, not only because is good per se to taste other flavours but because we could learn a new and better way to code.

Lisp is one of those hidden jewels when we talk about developing software, something that mixes the minimal amount of elements and the maximal power. Clojure is a modern Lisp that runs over the JVM, because of that we can access to which is probably the largest software repository in the world, id est, all those thousands Java packages. Furthermore, Clojure has a lot of smart and sweet syntatic sugar for us. Some friends of mine are already tried Clojure and they talk so many good things about the language that is my time to fall through the rabbit's whole and see by myself what is down there.

OOP inherited a vision of programming that includes imperative steps and changing stuff using *ifs* and *loops*. Lisp has a very different approach because is more about to create "tunnels" that transform data in a declarative way. One of the most attractives traits of this way to code is that is more kind with your brain: in OOP objects are big entities and they have a complex "inner life" where a simple change modifies many things. All we've experienced, when we integrate a new job, one of those projects where a seven hundred lines class controllers calls a three hundred lines class model that use the other four hundred lines module library that ends passing data to a two hundred lines presenter; and you must keep in your mind every state and every step throug the whole process! We see a lot of code but, where is the semantics? and how can I get a grip of meaning from these things that not only are huge entities but each one have a set of different inner states? Is not a surprise than even the smartest developers need weeks or months to understand all that intermingled code. To get the confidence to make changes in that code base costs a lot of money because consumes tons of time. Besides, you need a lot of discipline to avoid technical debt because imperative OOP code tends to be larger and more prone to errors.

In functional programming, in the other hand, you have a single "flow" of functions where the data cross from the point A to point B and that's it, entities are always small and interweaved, you don't need to keep state in your mind anymore, you only need to know that all data structure that fall in your hands must be transformed, moved, routed or saved following a rigid flow, nothing else. When the code is created in that way, the expression of the domain is easier to understand for everybody, you'll need less lines and the code tends to be concise, compact and less prone to fail. Testing and refactoring are also easier. Simplicity actually matters.

At this point you already read and followed some tutorials to know the basics of Clojure. Surely you already know that you have three options to handle a Clojure project: leningen, that is the oldest one, the newer boot and the even more new CLI tools. Since we are new arrivals we'll stick with the most used option. We'll use Luminus, Luminus is not a framework in the way RoR is becuase doesn't have so mucho code behind ir. Luminus is more like a useful template, something you'll have after a year of development.

     $ lein new luminus myblog +postgres +cljs +auth

PostgreSQL, ClojureScript and Auth support. Check the dirs and files that Luminus just created for us. The target dir is irrelevant, is just where the compiled java classes are saved. Notice that Luminus also created a .gitignore file for us and that in the file, the target dir is included because there is no point to add it our repository. By far the most important dir is src because is there where our precious code is kept. Inside src you'll see the clj, cljs and cljc dirs. As you must suppose, clj is for the Clojure code, cljs for ClojureScript and cljc is for files with portable code, I mean, code that can be used for cljs and clj files. A cljc file can have code like:

     (try
       (>! c msg)
         (catch #+clj Exception
                #+cljs :default
        e)
    .... ))

such code can be used in the front and the back end. *Reader Conditionals* is how that feature of Clojure is called.

The dir **env** save the configuration for the three environments: development, test and production. In the root of your new app you'll the scary project.clj file. Open it... scary hu? not so much really, the main sections are the :dependencies and the :profiles sections. It's a good idea to keep your dependencies in alphabetical order. The :profiles sections matches with two fo our environment: the test and the dev. The :uberjar part is related with the production deployment because in the future we'll need to create the file myblog.jar and upload it to our production server.

Now create the user and the database:

     $sudo - postgres
     $psql

     CREATE DATABASE myblog_dev;
     CREATE USER lumi WITH ENCRYPTED PASSWORD 'yourpass';
     GRANT ALL PRIVILEGES ON DATABASE myblog_dev TO lumi;

Edit the file **dev-config.edn** uncomment and change the string :database-url. Surely you have noticed that some files in the clojure's world ends with the extension .edn, the name stand for Extensible Data Notation. EDN is a kind of JSON developed to avoid JSON limitations, mainly the lack of extensibility. There is a subproject called [Transit](https://github.com/cognitect/transit-format) that offers higher performance encoding and decoding over the web and can be used instead of JSON, but we won't need to talk about EDN and Transit from now.

Now start your server:

    $ lein with-profile dev run

The "with-profile dev" part is not necessary, "lein run" will launch the app, but is good to know how to call a profile under leiningen isn't? Now you can see the app in your browser http://localhost:3000/. You can see the message "If you're seeing this message, that means you haven't yet compiled your ClojureScript!". Open other tab in your console and run:

    $ lein figwheel

after figwheel is up, you'll see the letters "Welcome to myblog". Figwheel is a very cool program that will help you coding ClojureScript (CLJS) functions, any change you make in the dir src/cljs/ will be inmmediatly reflected in the browser. Open the file "src/cljs/myblog/core.cljs" and change:

    (defn init! []
      (mount-components))

to:

    (defn init! []
      (.log js/console "Hello figwheel!!")    )
      (mount-components))

You'll see the error message "Could not Read" because that last parenthesis in the line that we just added shouldn't be there. Remove it, save the file and the error will disappear. In your *project.clj* file you can see the section :figwheel, besides, there is a section :project/dev and inside it :cljs, here is where CLJS knows how to be compiled. In the :cljs section you see:

    :main "myblog.app"

that means the CLJS compiler search for that name space and the file that contain is: *env/dev/cljs/myblog/app.cljs*. If you look in side that file you can see some general configurations and, at the end, the call to:

    (core/init!)

the function that you previously edited.

Now check the content of the dir src/clj/myblog/, most of the files there keep configuraton details, when you launched the app you saw something like:

     [main] INFO  myblog.nrepl - starting nREPL server on port 7000
     [main] INFO  myblog.core - #'myblog.db.core/*db* started
     [main] INFO  myblog.core - #'myblog.handler/init-app started
     [main] INFO  myblog.core - #'myblog.handler/app started
     [main] INFO  myblog.core - #'myblog.core/http-server started
     [main] INFO  myblog.core - #'myblog.core/repl-server started

As you can read, the namespace myblog.core put all together and then launch what we need: a network repl, the connection to the database, the http server, etc.

You can connect to the repl using:

    $ lein repl :connect localhost:7000

Inside the repl you can do cool stuff like render the home page:

    (require '[myblog.routes.home :as mb])
    (mb/home-page)

As you now from the Rails console, is very important to use the repl because is much more efficent debug code there than through the browser.

The core of a Luminus app is [Ring](https://github.com/ring-clojure/ring): "Ring is a Clojure web applications library inspired by Python's WSGI and Ruby's Rack." So Ring gives us the HTTP protocol. In the other hand "Compojure is a small routing library for Ring that allows web applications to be composed of small, independent parts." Compujore is que equivalent to routes.rb in Rails, in our case, the routes file is in src/clj/myblog/routes/home.clj. If you open that file you'll see the current two routes of our application, "/" and "docs". We can add a new route editing the home.clj in this way:

     (ns myblog.routes.home
       (:require [markdown.core :refer [md-to-html-string]]
                 [myblog.layout :as layout]
                 [myblog.db.core :as db]
                 [compojure.core :refer [defroutes GET]]
                 [ring.util.http-response :as response]
                 [clojure.java.io :as io]))

     (defn home-page []
       (layout/render "home.html"))

     (defroutes home-routes
       (GET "/" []
         (home-page))

       (GET "/mypage" []
            (-> (response/ok (-> "docs/mypage.md" io/resource slurp md-to-html-string))
                (response/header "Content-Type" "text/html	; charset=utf-8")))

       (GET "/docs" []
            (-> (response/ok (-> "docs/docs.md" io/resource slurp md-to-html-string))
                (response/header "Content-Type" "text/html	; charset=utf-8"))))


Note the "markdown.core" library in the top of the file. Markdown is a way to create HTML pages with less code, if you are edited a Wikipedia page, you are alreay used MD. Now add the file resources/docs/mypage.md with this content:

     <!-- file: resources/docs/mypage.md -->
     # Congratulations, your [Luminus]("http://luminusweb.net") site is ready!

     This page will help guide you through the first steps of building your site.

     #### This page is called

     from  `myblog.routes.home` namespace:

    ```
     (GET "/mypage" []
       (-> (response/ok (-> "docs/mypage.md" io/resource slurp md-to-html-string))
           (response/header "Content-Type" "text/html	; charset=utf-8")))
    ```

See the results in http://localhost:3000/mypage. Sometimes, when we add a route, edit the middleware or change a SQL file, we need to restart our Luminus server. Adding an html page is even easier, we just need to put:

           (GET "/somepage" []
             (layout/render "mynewpage.html"))

inside the defroutes section.

As a Rails developer you are acquainted with the concept of middleware. Ring offers the basics of the HTTP protocol, but also offers a way to enrich an application through middleware. In our app the file that defines that is *src/clj/myblog/middleware.clj*. If you open that file you'll see the things that our app puts between the request and your code. You can see functions like "wrap-csrf" that, like in Rails, helps us to avoid XSS atacks, "wrap-auth" that check if a user is authenticated or "restful-format-options", that help us in case that we send JSON or Transit data. All that functions are gather in the bottom of the file:

    (defn wrap-base [handler]
       (-> ((:middleware defaults) handler)
       wrap-auth
       wrap-webjars
       wrap-flash
       (wrap-session {:cookie-attrs {:http-only true}})
       (wrap-defaults
         (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-internal-error))

This middleware functions make our life easier: cookies, sessions, multiparts, forms, json requests and responses, web assets, flash messages, all that is handle by default by the middleware.

Note: org.joda.time is a package that has become the standard way to deal with dates and timestamps in the Java world. Since is so useful, we just import it in our code and use it.

## Hiccup

If you are like me, you find typping HTML annoying and verbose. In rails we have HAML and in Clojure we have [Hiccup](https://github.com/weavejester/hiccup). To use it we need to add:

    [hiccup "1.0.5"]

to our dependencies in the project.clj file. Create a dir and two new files:

     mkdir src/clj/myblog/hiccup
     touch src/clj/myblog/hiccup/about_view.clj
     touch src/clj/myblog/hiccup/layout_view.clj

inside the file layout_view.clj set:

     ;; file: src/clj/myblog/hiccup/layout_view.clj
     (ns myblog.hiccup.layout-view
       (:require [hiccup.page :refer [html5 include-css include-js]]))

     (defn application [content]
       (html5 [:head
          [:title (str ":: My Blog :: " (:title content))]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          (include-css "/css/styles.css")

          [:body
            [:div {:class "blog-header"}
             [:div {:class "blog-title" :id "blogtitle"} "My blog"]
                [:div {:class "blog-description"} "Tausende von Fragen bereit zu verwenden."]]
           [:div {:class "container"}  (:contents content)]]
          [:footer {:class "blog-footer"}
           [:img {:src "/img/warning_clojure.png" :alt "Lisp" :title "Lisp"}]
           [:p "My blog &copy; 2018. "]
           [:p [:a {:href "#"} "Back to top"]]]
          (include-js "/js/app.js")]))

Note the "include-js" in the bottom because we still want CLJS working in our page! If you don't have a "styles.css" file, now is a good moment to create it inside the dir: resources/public/css. Now, inside the file about_view.clj set:

    ;; file: src/clj/myblog/hiccup/about_view.clj
    (ns myblog.hiccup.about-view
      (:require [hiccup.element :refer [link-to]]))

    (defn index [name]
      [:div {:id "vision-container"}
      [:h1 {:class "text-success"} (str "Vision " name)]
      [:li {:class "nav-item"} [:a {:class "nav-link" :href "/mypage"} "My Page"]]])

The routes file now looks like:

    ;; file: src/clj/myblog/routes/home.clj
    (ns myblog.routes.home
      (:require [markdown.core :refer [md-to-html-string]]
            [myblog.layout :as layout]
            [myblog.db.core :as db]
            [myblog.hiccup.layout-view :as hiccup-layout]
            [myblog.hiccup.about-view :as about-view]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

    (defn home-page []
      (layout/render "home.html"))

    (defroutes home-routes
      (GET "/" []
        (home-page))

      (GET "/about" []
        (hiccup-layout/application {:title "About Us" :contents (about-view/index "Hello santa!")}))

      (GET "/mypage" []
           (-> (response/ok (-> "docs/mypage.md" io/resource slurp md-to-html-string))
               (response/header "Content-Type" "text/html	; charset=utf-8")))

      (GET "/docs" []
           (-> (response/ok (-> "docs/docs.md" io/resource slurp md-to-html-string))
               (response/header "Content-Type" "text/html	; charset=utf-8"))))

Stop and restart your app and lein will download and install the hiccup package. You can see the results in http://localhost:3000/about

## MVC

All that is cool, but when we see the file src/clj/myblog/routes/home.clj we saw a lot of elements mixed. We can try to emulate the MVC that we are used creating some dirs, that way our code will be separated by responsability as is in Rails.

      $ mkdir -p src/clj/myblog/controllers src/clj/myblog/models

We need to edit our file src/clj/myblog/routes/home.clj to reflect the modifications:

     ;; file: src/clj/myblog/routes/home.clj
     (ns myblog.routes.home
       (:require [compojure.core :refer [defroutes GET]]))

    (defroutes home-routes
       (GET    "/"      request (cont-company/index request))
       (GET    "/about" request (cont-company/about request)))

Look how clean our file is now! no UI parts, no database calls, just routing like in Rails! Now our controller:

    ;; file: src/clj/myblog/controllers/company_controller.clj
    (ns ^{:doc "This NS is for static pages"} myblog.controllers.company-controller
      (:require [clojure.tools.logging :as log]
                [myblog.hiccup.layout-view :as layout]
                [myblog.hiccup.about-view :as about-view]))
    (defn index [request]
      (let [params  (:params request)]
        (layout/application {:title "Starting!" :contents (about-view/index params)})))

    (defn about [request]
       (layout/application {:title "About Us" :contents (about-view/about request)}))

Note that even when the name space is "company-controller", the file name is "company_controller.clj" because in Clojure a file name must use the underscore. A caret in front of a map like in *^{:doc "This NS is for static pages"}* is a way to add metadata to an element in Clojure, if you go to the repl and run:

    (meta (find-ns 'myblog.controllers.company-controller))

you'll see the informaton linked to the namespace, metadata is good way to document code. Now our view:

     ;; file: src/clj/myblog/hiccup/about_view.clj
     (ns myblog.hiccup.about-view
       (:require [hiccup.element :refer [link-to]]))

     (defn index [params]
       [:div {:id "vision-container"}
        [:h1 {:class "text-success"} (str "Params: " params)]
        [:li {:class "nav-item"} [:a {:class "nav-link" :href "/about"} "About"]]])

     (defn about [request]
       [:div {:id "vision-container"}
        [:h1 {:class "text-success"} (str "All the request: " request)]
        [:li {:class "nav-item"} [:a {:class "nav-link" :href "/"} "Start"]]])

Now you can see the results in the browser. The params in the index action is empty because we are not sending information, but if you set "http://localhost:3000/?foo=dasdasd" in the browser, params will be populated. You can see the whole "request" info in http://localhost:3000/about. As you already know, what "request" holds is determinated by the functions called in the middleware file.

# DATABASE

In the section plugins of the file project.clj, add:

     [migratus-lein "0.6.0"]

Stop and start your app. Run the command:

    $ lein migratus create create-posts-table

Luminus use migratus, that offers a similar functonality that we use in Rails to handle migrations.





















