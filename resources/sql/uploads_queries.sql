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

-- :name save-upload! :! :n
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, user_id, created_at)
VALUES (:filename, :active, :tags, :user_id, :created_at)

-- :name get-uploads :? :*
-- :doc retrieve uploads given the user id.
SELECT id, filename, active, tags, user_id, created_at FROM uploads
WHERE user_id = :user-id ORDER BY id DESC

-- :name get-upload :? :1
-- :doc retrieve an upload file given the id.
SELECT * FROM uploads
WHERE id = :id

