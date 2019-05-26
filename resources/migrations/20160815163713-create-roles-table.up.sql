-- ;; lein migratus create create-roles-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate

CREATE TABLE roles (
   id serial PRIMARY KEY,
   name varchar(150) NOT NULL);

INSERT INTO roles (id, name) VALUES (1, 'Users');
INSERT INTO roles (id, name) VALUES (2, 'Teachers');
INSERT INTO roles (id, name) VALUES (3, 'Admins');
INSERT INTO roles (id, name) VALUES (4, 'Root');
