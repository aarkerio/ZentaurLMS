

As postgres user:

createuser -P -s -e zentaur

CREATE DATABASE zentaur_test WITH ENCODING='UTF-8';

ALTER ROLE zentaur PASSWORD 'qwerty78';


{:profiles/dev  {:env {:classname "net.sf.log4jdbc.DriverSpy" :database-url "jdbc:postgresql://localhost/zentaur_dev?user=zentaur&password=yourpassword"}}
 :profiles/test {:env {:classname "net.sf.log4jdbc.DriverSpy" :database-url "jdbc:postgresql://localhost/zentaur_test?user=zentaur&password=yourpassword"}}}


$ lein run migrate

$ lein run
