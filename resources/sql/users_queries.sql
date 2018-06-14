/*
  Structure: :name :command :result
  Type of commands
     :? = fetch (query)
     :! = execute (statetment like INSERT)
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
*/
-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users (id, fname, lname, email, pass)
VALUES (:id, :fname, :lname, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET fname = :fname, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-user-login :? :1
-- :doc retrieve a user given the email and password.
SELECT id, fname, last_name, email, admin FROM users
WHERE email = :email AND password = :password

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name save-message! :! :n
-- :doc creates a new message record
INSERT INTO posts
(fname, last_name, email, pass)
VALUES (:fname, :last_name, :email, :pass)

-- :name save-upload! :! :n
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, user_id, created_at)
VALUES (:filename, :active, :tags, :user_id, :created_at)

-- :name get-uploads :? :*
-- :doc retrieve uploads given the user id.
SELECT id, filename, active, tags, user_id, created_at FROM uploads
WHERE user_id = :user_id ORDER BY id DESC

-- :name get-posts :? :*
-- :doc retrieve a post given the id.
SELECT id, title, body, active, discution, user_id, created_at FROM posts
ORDER BY id DESC LIMIT 5

-- :name get-post :? :1
-- :doc retrieve a post given the id.
SELECT * FROM posts
WHERE id = :id


