CREATE UNIQUE INDEX ON users (email);
CREATE INDEX ON users (lower(nickname COLLATE "ucs_basic"));

CREATE INDEX ON forum (title);
CREATE INDEX ON forum (admin);
CREATE INDEX ON forum (lower(slug));

CREATE INDEX ON thread (slug);
CREATE INDEX ON thread (forum);
CREATE INDEX ON thread (created);
CREATE INDEX ON thread (author);
CREATE INDEX ON thread (lower(slug));
CREATE INDEX ON thread (lower(forum));

CREATE INDEX ON post (parent);
CREATE INDEX ON post (author);
CREATE INDEX ON post (forum);
CREATE INDEX ON post (thread_id);
CREATE INDEX ON post (lower(forum));
CREATE index on post (thread_id, parent, id);
CREATE index on post (thread_id, id);
CREATE INDEX ON post ((post_path[1]));

CREATE INDEX IF NOT EXISTS vote_id ON vote (author, thread_id);

CREATE INDEX ON users_forum (forum);
CREATE INDEX ON users_forum (author);
CREATE INDEX ON users_forum (lower(forum));
CREATE INDEX ON users_forum (lower(author));
CREATE INDEX ON users_forum (lower(forum), author);


