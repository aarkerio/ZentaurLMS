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
