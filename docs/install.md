

Add a postgres user:

CREATE DATABASE zentaur_test WITH ENCODING='UTF-8';
CREATE DATABASE zentaur_dev WITH ENCODING='UTF-8';
CREATE USER zentaur WITH PASSWORD 'yourpassword';

GRANT ALL PRIVILEGES ON DATABASE "zentaur_test" to zentaur;
GRANT ALL PRIVILEGES ON DATABASE "zentaur_dev" to zentaur;



