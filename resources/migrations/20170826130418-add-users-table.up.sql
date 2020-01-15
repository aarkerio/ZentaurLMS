-- ;; lein migratus create create-users-table
-- ;; lein run migrate
-- ;; <-- this is necessary between statements to avoid "Too many update results were returned."
CREATE TABLE users(
   id serial PRIMARY KEY,
   fname VARCHAR(30) NOT NULL,
   lname VARCHAR(30) NOT NULL,
   uname VARCHAR(30) NOT NULL UNIQUE,
   email VARCHAR(30) NOT NULL UNIQUE,
   admin BOOLEAN NOT NULL DEFAULT false,
   last_login timestamp(0) with time zone,
   active BOOLEAN NOT NULL DEFAULT true,
   role_id int REFERENCES roles NOT NULL DEFAULT 2,
   password VARCHAR(300) NOT NULL,
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
 );
--;;
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
--;;
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at := CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--;;

CREATE TRIGGER trig_users
BEFORE UPDATE ON "users"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
