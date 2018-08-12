-- ;; lein migratus create create-users-table
-- ;; lein run migrate

CREATE TABLE users(
   id serial PRIMARY KEY,
   fname VARCHAR(30) NOT NULL,
   lname VARCHAR(30) NOT NULL,
   uname VARCHAR(30) NOT NULL UNIQUE,
   email VARCHAR(30) NOT NULL,
   admin BOOLEAN NOT NULL DEFAULT false,
   last_login TIME,
   active BOOLEAN,
   password VARCHAR(300),
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone
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
  active,
  password
) VALUES ('Manuel',
          'Montoya',
          'mmontoya',
          'admin@example.com',
          true,
          true,
          'bcrypt+sha512$31663163343233343262646331656136$12$472ab1393ef857b0b30769ebe654e627c0b0a4a3847a6f4e');
