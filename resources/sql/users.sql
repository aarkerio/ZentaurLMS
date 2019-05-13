/******************* USERS ***/
-- CREATE USER zentaur WITH encrypted password 's0m3erty1WW';
-- ALTER USER zentaur CREATEDB;
-- CREATE DATABASE "zentaur_dev" ENCODING 'UTF8';
-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users (fname, lname, uname, email, password, admin, active, role_id)
VALUES (:fname, :lname, :uname, :email, :password, :admin, :active, :role_id)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET fname = :fname, lname = :lname, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-users :? :*
-- :doc retrieve all users given the active column.
SELECT * FROM users
WHERE active = :active ORDER BY id DESC

-- :name get-user-login :? :1
-- :doc retrieve a user given the email and password.
SELECT id, fname, lname, uname, email, admin FROM users
WHERE email = :email AND password = :password

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id
