

As postgres user:

createuser -P -s -e zentaur


CREATE DATABASE zentaur_test WITH ENCODING='UTF-8';

ALTER ROLE zentaur ENCRYPTED PASSWORD 'md5dcaa7d861a3ae3bfebc54c9b7fcc69ea';
