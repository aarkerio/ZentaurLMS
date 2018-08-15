-- ;; lein migratus create create-roles-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate

CREATE TABLE roles (
   id serial PRIMARY KEY,
   name varchar(150) NOT NULL);

ALTER TABLE users ADD COLUMN role_id int REFERENCES roles;
