

1) Add a PostgreSQL user:

CREATE DATABASE zentaur_test WITH ENCODING='UTF-8';
CREATE DATABASE zentaur_dev WITH ENCODING='UTF-8';
CREATE DATABASE zentaur_production WITH ENCODING='UTF-8';

CREATE USER zentaur WITH PASSWORD 'yourpassword';

GRANT ALL PRIVILEGES ON DATABASE "zentaur_test" to zentaur;
GRANT ALL PRIVILEGES ON DATABASE "zentaur_dev" to zentaur;

2) Migrate

$ lein run migrate

3) Run:

$ lein run

4) In other console run figwheel:

$ lein fig:dev

5) Login in the browser.

admin@example.com/password
