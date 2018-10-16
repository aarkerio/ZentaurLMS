-- ;; lein migratus create create-users-table
-- ;; lein run migrate

CREATE TABLE users(
   id serial PRIMARY KEY,
   fname VARCHAR(30) NOT NULL,
   lname VARCHAR(30) NOT NULL,
   uname VARCHAR(30) NOT NULL UNIQUE,
   email VARCHAR(30) NOT NULL UNIQUE,
   admin BOOLEAN NOT NULL DEFAULT false,
   last_login TIME,
   active BOOLEAN,
   role_id int REFERENCES roles NOT NULL default 2
   password VARCHAR(300) NOT NULL,
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone
 );

INSERT INTO users (fname,
  lname,
  uname,
  email,
  role_id,
  admin,
  active,
  password
) VALUES ('Manuel',
          'Montoya',
          'mmontoya',
          'admin@example.com',
          4,
          true,
          true,
          'bcrypt+sha512$31663163343233343262646331656136$12$472ab1393ef857b0b30769ebe654e627c0b0a4a3847a6f4e');
