******************************************
      INSTALL  INSTRUCTIONS
******************************************

1) As PostgreSQL root user create a new user:

 $ createuser zentaur --createdb --pwprompt

2) Add a line in the file /etc/postgresql/12/main/pg_hba.conf

local   all             zentaur                 password

3) Create the databases:

 $ createdb zentaur_dev --encoding=UTF-8 -U zentaur
 $ createdb zentaur_test --encoding=UTF-8 -U zentaur

4) Copy and edit:

 $ cp dev-config.edn.example dev-config.edn
 $ cp test-config.edn.example test-config.edn

5) Migrate

 $ lein with-profile dev run migrate

 $ lein with-profile test run migrate

6) Run the repl:

$ lein with-profile dev repl

7) Seed the DB:

(require '[luminus.seeder :as se])
(se/main)

8) Run the webserver:

 $ lein with-profile dev run

In other console run figwheel:

$ lein fig:dev

8) Login in the browser http://localhost:3000/

admin@example.com/password

