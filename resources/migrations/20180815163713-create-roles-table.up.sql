-- ;; lein migratus create create-roles-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate

CREATE TABLE roles (
   id serial PRIMARY KEY,
   name varchar(150) NOT NULL);

INSERT INTO roles (name) VALUES ('Users');
INSERT INTO roles (name) VALUES ('Teachers');
INSERT INTO roles (name) VALUES ('Admins');
INSERT INTO roles (name) VALUES ('Root');

ALTER TABLE users ADD COLUMN role_id int REFERENCES roles NOT NULL default 3;
