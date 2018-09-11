<!--  Local Variables:
ispell-check-comments: exclusive
ispell-local-dictionary: "en_GB"
End: -->

# Clojure for Rails Developer with Luminus

<p align="center">
  <img src="https://raw.githubusercontent.com/aarkerio/ZentaurLMS/master/resources/public/img/warning_clojure.png">
</p>

Ruby is one of the smartest OO languages out there and Rails is a solid, fun and full featured framework. I enjoy coding with Ruby, I've doing it for ten years now and I think I'll be doing it for many years to come. However, it's time to try something new, not only because is good *per se* to taste other flavours but because we could learn news and betters ways to code in the process.

Lisp is one of those hidden jewels when we talk about developing software, something that mixes the minimal amount of elements and the maximal power of expression. Clojure is a modern Lisp that runs over the JVM, because of that, we can access to which is probably the largest software repository in the world, id est, all those thousands and thousands of Java packages and libraries. Furthermore, Clojure has a lot of smart and sweet syntactic sugar waiting for us. Some friends of mine have already tried Clojure and they have being talking so many good things about the language than now is my turn to fall through the rabbit's whole and see what is down there for me. These are the notes to the journey so far.

<p align="center">
  <img src="https://raw.githubusercontent.com/aarkerio/ZentaurLMS/master/resources/public/img/5ikdvgoazp911.png">
</p>

OOP inherited a vision of programming that includes imperative steps and changing stuff using *ifs* and *loops*. Lisp has a very different approach because is more about to create "tunnels" that transform data in a declarative way. One of the most attractive traits of this way to code is that is more kind with your brain because the "cognitive charge" is lighter: in OOP objects are big entities and they have a complex "inner life" where a simple change modifies how methods work. All we've experienced, when we integrate to a new job, one of those projects where a seven hundred lines class controllers calls a three hundred lines class model that use the other four hundred lines module library that ends passing data to a two hundred lines presenter; and you must keep in your mind every state and every step through the whole process! Mentally, is an exhausting and stressing process. We see a lot of code but, where is the semantics? and how can I get a grip of meaning from these things that not only are huge entities but each one have a set of different inner states? Is not a surprise than even the smartest developers need weeks or months to understand all that intermingled code. To get the confidence to make changes in that code base costs a lot of money because consumes tons of time. Besides, you need a lot of discipline to avoid technical debt because imperative OOP code tends to be larger and more prone to errors.

In functional programming, in the other hand, you have a single "flow" of functions where the data cross from the point A to point B and that's it, entities are always small and interweaved, you don't need to keep states in your mind anymore, you only need to know that all data structure that fall in your hands must be transformed, moved, routed or saved following a rigid flow, nothing else. When the code is created in that way, the expression of the domain is easier to understand for everybody, you'll need less lines and the code tends to be concise, compact and less prone to fail. Testing and refactoring are also easier. Simplicity actually matters. In conclusion, there are few languages that can been considered as productive as Ruby, but Clojure indeed is one of those languages.

At this point you already read and followed some tutorials to know the basics of Clojure, in particular the excellent introduction [Clojure Distilled](https://yogthos.github.io/ClojureDistilled.html). Surely you already know that you have three options to start and handle a Clojure project: leiningen, that is the oldest one, the newer [boot](http://boot-clj.com/) and the even more new CLI tools. Since we are new arrivals we'll stick with the most used option. We'll use Luminus, Luminus is not a framework in the way RoR is because --even when it has some libraries developed *ad hoc*-- it doesn't have so much code behind it. You will notice that Clojure tools are more rustic and do less *magic* that many Rails tools, but in exchange, you will gain more control and knowledge over those tools. Luminus is more like a useful and full featured template. Because of its nature, you'll realise that while you learn more and more about Clojure and its ecosystem, Luminus could start to drift away (if you want so) and gradually you will modify a lot of things in your own way. But anyway, Luminus is a great starting point so:

     $ lein new luminus myblog +postgres +cljs +auth

We created a new app with PostgreSQL, ClojureScript and Auth support. ClojureScript is a different language that Clojure but their similarities are so strong that is possible to say that if you know Clojure, you already know how to code in ClojureScript. To have an homogeneous environment for the Back and FrontEnd is a big advantage, you don't need a drastic distinction between BE and FE tasks anymore, any developer can work in both sides. One of the greatest news about ClojureScript is its tooling, which has improved a lot in the last couple of years. Of course, if you share the extended opinion that JavaScript is an ugly and bad designed language, ClojureScript indeed is a bless.

BTW, when we created our project, we could have added SASS support with the *+sassc* option, but that would have required to install the *sassc* parser first, usually with:

    $ apt/yum/pacman install sassc

Anyhow, check the directories and files that Luminus just created for us. The *target* dir is irrelevant, is just where the compiled java classes are saved. Notice that Luminus also created a .gitignore file for us and that in the file, the *target* dir is included because there is no point to add it to our repository. By far the most important dir is *src* because is there where our precious code is kept. Inside *src* you'll see the clj, cljs and cljc dirs. As you must suppose, clj is for the Clojure code, cljs for ClojureScript and cljc is for files with portable code, I mean, code that can be used for cljs and clj files. A cljc file can have code like:

     (try
       (>! c msg)
         (catch #+clj Exception
                #+cljs :default
        e)
    .... ))

such code can be used in the front and the back end, another great reason of why Clojure environment is so cool, can you mix Python and React code?, no you can't! but you can with *Reader Conditionals*, that is how this feature of Clojure/ClojureScript is called, btw.

The dir *env* save the configuration for the three environments: development, test and production. In the root of your new app you'll the *project.clj* file. Open it... scary hu? not so much really, the main sections are the *:dependencies* and the *:profiles* sections. It's a good idea to keep your dependencies in alphabetical order. The *:profiles* sections matches with two of our environment: the test and the dev. The *:uberjar* part is related with the production deployment because, in the future, we'll need to create the file *myblog.jar* and upload it to our production server. As with Puma or Unicorn, an Nginx server is used to setup a Clojure app in production.

The *:plugins* section in the project.clj file adds functionalities, but not to the general project but to the lein console. It means that adding a new plugin will allow us to run different kinds of commands. There is a cool project called [Ultra](https://github.com/venantius/ultra) that you can try later.

Now create a new user and a new database:

     $sudo - postgres
     $psql

     CREATE DATABASE myblog_dev;
     CREATE USER lumi WITH ENCRYPTED PASSWORD 'yourpass';
     GRANT ALL PRIVILEGES ON DATABASE myblog_dev TO lumi;

Edit the file **dev-config.edn**, uncomment and change the string *:database-url*. Surely you have noticed that some files in the clojure's world ends with the extension .edn, the name stand for Extensible Data Notation. EDN is a kind of JSON developed inside the Clojure community to avoid JSON limitations, mainly its lack of extensibility. There is a subproject called [Transit](https://github.com/cognitect/transit-format) that offers higher performance encoding and decoding option over the web and it can be used instead of JSON, but we don't need to talk about EDN and Transit right now.

Now start your server:

    $ lein with-profile dev run

The "with-profile dev" part is not necessary, "lein run" will launch the app, but is good to know how to call a profile under leiningen isn't? Now you can see the app in your browser http://localhost:3000/. You can see the message "If you're seeing this message, that means you haven't yet compiled your ClojureScript!". Open another tab in your console and run:

    $ lein figwheel

after figwheel is up, you'll see the letters "Welcome to myblog". Figwheel is a very cool program that will help you coding ClojureScript (CLJS) applications, any change you make in the dir *src/cljs/* will be immediately reflected in the browser. Open the file "src/cljs/myblog/core.cljs" and change:

    (defn init! []
      (mount-components))

to:

    (defn init! []
      (.log js/console "Hello figwheel!!")    )
      (mount-components))

You'll see the error message "Could not Read" because that last parenthesis in the line that we just added shouldn't be there. Remove it, save the file and the error will disappear. In your *project.clj* file you can see the section *:figwheel*, besides, there is a section *:project/dev* and inside it *:cljs*, here is where CLJS knows how to be compiled. In the :cljs section you see:

    :main "myblog.app"

that means the CLJS compiler search for that name space and the file that contain it is: *env/dev/cljs/myblog/app.cljs*. If you look in side that file you can see some general configurations and, at the end, the call to:

    (core/init!)

the function that you previously edited.

Now check the content of the dir *src/clj/myblog/*, most of the files there keep configuration details, when you launched the app you saw something like:

     [main] INFO  myblog.nrepl - starting nREPL server on port 7000
     [main] INFO  myblog.core - #'myblog.db.core/*db* started
     [main] INFO  myblog.core - #'myblog.handler/init-app started
     [main] INFO  myblog.core - #'myblog.handler/app started
     [main] INFO  myblog.core - #'myblog.core/http-server started
     [main] INFO  myblog.core - #'myblog.core/repl-server started

As you can read, the name space *myblog.core* put all together and then launch what we need: a network repl, the connection to the database, the http server, etc. To do so Luminus use the  [Mount](https://github.com/tolitius/mount) library to start and keep the *states*.

You can connect to the repl using:

    $ lein repl :connect localhost:7000

Inside the repl you can do cool stuff like render the home page:

    (require '[myblog.routes.home :as mb])
    (mb/home-page)

As you now from the Rails console, is very important to use the REPL extensively because is much more efficient to debug code there than through the browser.

The core of a Luminus app is [Ring](https://github.com/ring-clojure/ring): "Ring is a Clojure web applications library inspired by Python's WSGI and Ruby's Rack." So Ring gives us the HTTP protocol. In the other hand "Compojure is a small routing library for Ring that allows web applications to be composed of small, independent parts." Compujore is the equivalent to the *routes.rb* file in Rails, in our case, the routes file is in *src/clj/myblog/routes/home.clj*. If you open that file you'll see the current two routes of our application, "/" and "docs". We can add a new route editing the home.clj in this way:

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


Note the "markdown.core" library in the top of the file. Markdown is a way to create HTML code without all the tags, if you are edited a Wikipedia page, you are already used MD. Now add the file resources/docs/mypage.md with this content:

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

As a Rails developer you are acquainted with the concept of middleware. Ring offers the basics of the HTTP protocol, but also offers a way to enrich an application through middleware. In our app the file that defines that is *src/clj/myblog/middleware.clj*. If you open that file you'll see the things that our app puts between the request and your code. You can see functions like "wrap-csrf" that, like in Rails, helps us to avoid XSS atacks, "wrap-auth" that check if a user is authenticated or "restful-format-options", that help us in case that we send JSON or Transit data over HTTP. All that functions are gather in the bottom of the file:

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

This middleware functions make our life easier: cookies, sessions, multipart forms, json requests and responses, web assets, flash messages, all that is handle by default by the middleware.

Note: org.joda.time is a package that has become the standard way to deal with dates and timestamps in the Java world. Since is so useful, we just import it in our code and use it.

## Hiccup

If you are like me, you find typing HTML annoying and verbose. In rails we have HAML and in Clojure we have [Hiccup](https://github.com/weavejester/hiccup). To use it we need to add:

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

Note the "include-js" in the bottom because we still want CLJS and figwheel working in our site! If you don't have a "styles.css" file, now is a good moment to create it inside the dir: *resources/public/css*. Now, inside the file about_view.clj set:

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

All that is cool, but when we see the file *src/clj/myblog/routes/home.clj* we saw a lot of elements mixed and that is not good. We can try to emulate the MVC that we are used creating some dirs, that way our code will be separated by responsibility as is in Rails.

      $ mkdir -p src/clj/myblog/controllers src/clj/myblog/models

We need to edit our file src/clj/myblog/routes/home.clj to reflect the modifications:

     ;; file: src/clj/myblog/routes/home.clj
     (ns myblog.routes.home
       (:require [myblog.controllers.company-controller :as cont-company]
                 [compojure.core :refer [defroutes GET]]))

    (defroutes home-routes
       (GET    "/"      request (cont-company/index request))
       (GET    "/about" request (cont-company/about request)))

Look how clean our routes file is now! no UI parts, no database calls, just routing like in Rails! Now we must add out first controller:

    ;; file: src/clj/myblog/controllers/company_controller.clj
    (ns ^{:doc "This NS is just for static pages"} myblog.controllers.company-controller
      (:require [clojure.tools.logging :as log]
                [myblog.hiccup.layout-view :as layout]
                [myblog.hiccup.about-view :as about-view]))
    (defn index [request]
      (let [params  (:params request)
            _       (log/info (str ">>> *** PRINT THE request *** >>>>> " request))]
        (layout/application {:title "Starting!" :contents (about-view/index params)})))

    (defn about [request]
       (layout/application {:title "About Us" :contents (about-view/about request)}))

Note that even when the name space is "company-controller", the file name is "company_controller.clj" because in Clojure, a file name must use the underscore. A caret in front of a map like in *^{:doc "This NS is for static pages"}* is a way to add metadata to an element in Clojure, if you go to the repl and run:

    (meta (find-ns 'myblog.controllers.company-controller))

you'll see the information linked to the namespace, metadata is good way to document code. Note also that we are debugging a variable importing the logging library and using the "log/info" function. In Clojure using the underscore as we are doing it in the line:

        _    (log/info (str ">>> *** PRINT THE request *** >>>>> " request))]

just means "I don't care about this variable, but since I forced to use one, I use the underscore", is just a convention to fulfil a requirement. You see the "log/info" content printed in the console where the application is running.

Now we can add our view:

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

You can see the results in the browser. The params in the index action is empty because we are not sending information, but if you type "http://localhost:3000/?foo=bar" in the browser, *params* will be populated. You can see the whole *request* info in http://localhost:3000/about. As you already know, what *request* holds is determined by the functions called in the middleware file.

# DATABASE

<p align="center">
  <img src="https://raw.githubusercontent.com/aarkerio/ZentaurLMS/master/resources/public/img/pfrr.jpeg">
</p>

In the section plugins of the file project.clj, add:

     [migratus-lein "0.6.0"]

Stop and start your app. Run the command:

    $ lein migratus create create-posts-table

Luminus use [migratus](https://github.com/yogthos/migratus), that offers a similar functionality that we use in Rails to handle migrations. Now you have a file like  *20180908020454-create-posts-table.up.sql* in the dir resources/migrations/. Open it and add the lines:

    -- ;; lein migratus create create-posts-table
    -- ;; lein run migrate
    -- ;; lein with-profile test run migrate

    CREATE TABLE posts (
       id serial PRIMARY KEY,
       title varchar(250) NOT NULL,
       body text NOT NULL,
       published boolean NOT NULL DEFAULT false,
       created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
       updated_at timestamp(0) with time zone
    );
    -- Initial posts
    INSERT INTO posts (title, body, published) VALUES ('Some title', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam', true);
    INSERT INTO posts (title, body, published) VALUES ('Other title', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor', true);

Save the file and run:

     $ lein run migrate

Since Luminus had already created the "SOMEDATE-add-users-table.up.sql" file, migratus will create the new two tables in our database. If you run the migrate command again, migratus won't do anything because now the dates are behind its log. If you need to alter a table you need to create a migration like:

      $ lein migratus create adding-comments-column-to-posts-table

and inside the new file to set:

     ALTER TABLE posts ADD COLUMN comments boolean NOT NULL DEFAULT false;

and run the migrate command again.

Open the file *resources/sql/queries.sql* and add after the last line:

     -----   POSTS QUERIES

     -- :name create-post! :! :n
     -- :doc creates a new post record
     INSERT INTO posts
     (title, body, published)
     VALUES (:title, :body, :published)

     -- :name update-user! :! :n
     -- :doc updates an existing user record
     UPDATE posts
     SET title = :title, body = :body, published = :published
     WHERE id = :id

     -- :name get-all-posts :? :*
     -- :doc retrieves a post record given the id
     SELECT * FROM posts
     WHERE published = true

     -- :name get-posts :? :1
     -- :doc retrieves a post record given the id
     SELECT * FROM posts
     WHERE id = :id

     -- :name delete-post! :! :n
     -- :doc deletes a post record given the id
     DELETE FROM posts
     WHERE id = :id


Notice that he we are following a Clojure convention: when something ends with a "!" notation, that means a change in the state for atoms, metadata, vars, transients, agents and input/output as well (alters a table, writes a file, etc), so, we put the admiration mark (!) to indicate that that part is not "pure", but mutating the sate.

Luminus use [Hugsql](https://www.hugsql.org/), I know what are you thinking: "Wait, no ORM!". Well, ORM is a concept created to fulfil the gap between SQL (mathematics) and Objects ("intuitive" entities), but we don't have objects anymore. Hugsql returns us a lazy sequence, and we don't need anything else. A Hugsql query can be written like:

    -- :Name insert-post!
    -- :command :execute
    -- :result :raw
    -- :doc creates a new post record

But that is too verbose, if we see the Hugsql options:

      Type of command:
         :? = fetch (query)
         :! = execute (statement like INSERT)
      Type of results:
         :* = vectors [1, 3, 4]
         :affected or :n = number of rows affected (inserted/updated/deleted)
         :1 = one row
         :raw = passthrough an untouched result (default)

We see that we can write the same query in a more concise way:

     -- :name insert-post! :! :n
     -- :doc creates a new post record

If you see the file *src/clj/myblog/db/core.clj*, that is the place where the file "queries.sql" is called and mounted to be used, so when we want to use the DB, we must call that name space first. As mentioned early, all changes that we do to the queries.sql file, requires to restart our app.

We can access our table posts, first we need to add a new route to our file src/clj/myblog/routes/home.clj, change:

     ;; file: src/clj/myblog/routes/home.clj
     (ns myblog.routes.home
       (:require [myblog.controllers.company-controller :as cont-company]
                 [myblog.controllers.posts-controller :as cont-posts]
                 [compojure.core :refer [defroutes GET]]))

    (defroutes home-routes
       (GET    "/"      request (cont-company/index request))
       (GET    "/posts" request (cont-posts/posts request))
       (GET    "/about" request (cont-company/about request)))

The new controller must be like:

     ;; file: src/clj/myblog/controllers/posts_controller.clj
     (ns myblog.controllers.posts-controller
       (:require [clojure.tools.logging :as log]
                 [myblog.models.posts :as model-posts]
                 [myblog.hiccup.layout-view :as layout]
                 [myblog.hiccup.posts-view :as posts-view]))

     ;; GET /posts
     (defn posts [request]
       (let [posts    (model-posts/get-all-posts)]
         (layout/application {:title "::My Blog::" :contents (posts-view/posts posts)})))

And our new model:

    ;; file: src/clj/myblog/models/posts.clj
    (ns myblog.models.posts
      (:require [myblog.db.core :as db]))

    (defn get-all-posts
      "Get all published posts"
      []
      (db/get-all-posts))

after restart our app we can see our posts in:  http://localhost:3000/posts.

In the file src/myblog/routes/home.clj add the http method changing the line:

    [compojure.core :refer [defroutes GET]]

to:

    [compojure.core :refer [defroutes GET POST]]

In the same file, add the new two routes:

     (GET    "/posts/new" request (cont-posts/form-new request))
     (POST   "/posts"     request (cont-posts/save-post request))

The posts controller now looks like:

     (ns myblog.controllers.posts-controller
       (:require [clojure.tools.logging :as log]
                 [myblog.models.posts :as model-posts]
                 [myblog.hiccup.layout-view :as layout]
                 [myblog.hiccup.posts-view :as posts-view]
                 [ring.util.http-response :as response]))
     ;; GET /posts
     (defn posts [request]
       (let [posts    (model-posts/get-all-posts)]
         (layout/application {:title "::My Blog::" :contents (posts-view/posts posts)})))

     ;; GET /posts/form-new
     (defn form-new [request]
       (let [csrf-field (-> request :session :ring.middleware.anti-forgery/anti-forgery-token)]
         (layout/application
             {:title "New Post" :csrf-field csrf-field :contents (posts-view/form-new csrf-field)})))

     ;; POST /posts
     (defn save-post [request]
       (let [params (:params request)]
         (model-posts/save-post! (dissoc params :__anti-forgery-token :button-save))
         (response/found "/posts")))

Our view:

     (defn form-new [csrf-field]
      [:div {:id "cont"}
      (f/form-to [:post "/posts"]
        (f/hidden-field { :value csrf-field} "__anti-forgery-token")
        [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
        [:div (f/text-area {:cols 90 :rows 20} "body")]
        [:div (f/label "published" "Published") (f/check-box {:title "Publish this" :value "1"} "published")]
       (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Einrichen")) ] )

We need the hidden element "__anti-forgery-token" because otherwise the middleware would think this request is an XSS attack and would launch an error message. The *csrf-field* is valid only for a few minutes before expire.

Our posts model shows a new function in save the params data:

    ;; file: src/clj/myblog/models/posts.clj
    (defn save-post! [params]
      (let [published (contains? params :published)]
      (db/create-post! (assoc params :published published))))

Notice that we need to update the field published since Hugsql es expecting a boolean value, not a string.

# Authentication

TODO

# Validation the form with ClojureScript

TODO

# Validation the model with Construct

TODO

# TDD in Clojure

TODO

# Reagent: React with ClojureScript

TODO


