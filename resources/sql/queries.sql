/****
  Structure: :name :command :result
  Type of commands
     :? = fetch (query)
     :! = execute (statetment like INSERT)
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
***/

/*************************** POSTS ***/

-- :name save-message! :! :n
-- :doc creates a new message record
INSERT INTO posts
(fname, lname, email, pass)
VALUES (:fname, :lname, :email, :pass)

-- :name get-posts :? :*
-- :doc retrieve array posts given the id.
SELECT id, title, body, active, discution, user_id, created_at FROM posts
ORDER BY id DESC LIMIT 5

-- :name get-post :? :1
-- :doc retrieve a post given the id.
SELECT * FROM posts
WHERE id = :id

-- :name save-post! :! :n
-- :doc creates a new post record
INSERT INTO posts
(title, body, active, discution, tags, user_id, created_at)
VALUES (:title, :body, :active, :discution, :tags, :user_id, :created_at)

-- :name update-post! :! :n
-- :doc update an existing user record
UPDATE posts
SET title = :title, body = :body, tags = :tags, active = :active, discution = :discution
WHERE id = :id

-- :name delete-post! :! :n
-- :doc delete a post given the id
DELETE FROM posts
WHERE id = :id

-- :name save-comment :! :n
-- :doc creates a new message record
INSERT INTO comments
(comment, post_id, user_id, created_at)
VALUES (:comment, :post_id, :user_id, :created_at)

-- :name get-comments :? :*
-- :doc retrieve comments from a post given the post id.
SELECT u.id AS user_id, u.fname, u.lname, c.id, c.comment, c.created_at
FROM users AS u, comments AS c
WHERE c.post_id = :id AND u.id=c.user_id ORDER BY c.id

-- :name admin-get-posts :? :*
-- :doc retrieve array posts given the user id.
SELECT id, title, body, active, discution, user_id, created_at FROM posts
WHERE user_id = :user-id ORDER BY id

/*******************  UPLOADS   ***/

-- :name save-upload! :! :n
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, user_id, created_at, hashvar, done)
VALUES (:filename, :active, :tags, :user_id, :created_at, :hashvar, :done)

-- :name get-uploads :? :*
-- :doc retrieve uploads given the user id.
SELECT * FROM uploads
WHERE user_id = :user-id ORDER BY id DESC

-- :name get-upload :? :1
-- :doc retrieve an upload file given the id.
SELECT * FROM uploads
WHERE id = :id

-- :name clj-expr-generic-update :! :n
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
update :i:table set
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
~*/
where id = :id

/******************* USERS ***/

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users (id, fname, lname, email, password, admin, active)
VALUES (:id, :fname, :lname, :email, :password, :admin, :active)

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

/**************   TESTS    ****/

-- :name cdreate-test! :! :n
-- :doc creates a new test record
INSERT INTO tests (id, fname, lname, email, pass)
VALUES (:id, :fname, :lname, :email, :pass)

-- :name update-test! :! :n
-- :doc update an existing test record
UPDATE tests
SET fname = :fname, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-test :? :1
-- :doc retrieve a test given the id.
SELECT * FROM tests
WHERE id = :id

-- :name delete-test! :! :n
-- :doc delete a test given the id
DELETE FROM tests
WHERE id = :id
