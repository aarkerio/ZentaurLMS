CREATE TABLE users(
   id serial PRIMARY KEY,
   fname VARCHAR(30) NOT NULL,
   lname VARCHAR(30) NOT NULL,
   uname VARCHAR(30) NOT NULL UNIQUE,
   email VARCHAR(30) NOT NULL,
   admin BOOLEAN NOT NULL DEFAULT false,
   last_login TIME,
   is_active BOOLEAN,
   password VARCHAR(300)
 );

 -- fname | character varying(30)  | not null
 -- lname  | character varying(30)  | not null
 -- email      | character varying(30)  | not null
 -- admin      | boolean                | not null Vorgabewert false
 -- last_login | time without time zone |
 -- is_active  | boolean                |
 -- password   | character varying(300) |  (hashers/derive "secretpassword")

INSERT INTO users (fname,
  lname,
  uname,
  email,
  admin,
  is_active,
  password
) VALUES ('Manuel',
          'Montoya',
          'mmontoya',
          'admin@example.com',
          true,
          true,
          'bcrypt+sha512$572865bc1b45c7a696332ec879899e03$12$216ec77b706a69c93d9aaa761d39ba77b7043763038f0737');
