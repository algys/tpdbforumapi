CREATE INDEX ON users (lower(nickname COLLATE "ucs_basic"));

CREATE INDEX ON forum (admin);
CREATE INDEX ON forum (lower(slug));

CREATE INDEX ON thread (slug);
CREATE INDEX ON thread (lower(forum));

CREATE INDEX ON post (forum);
CREATE INDEX ON post (author);
CREATE index on post (thread_id, parent, id);
CREATE index on post (thread_id, id);
CREATE INDEX ON post ((post_path[1]), id);
CREATE INDEX ON post (thread_id, post_path);

CREATE INDEX ON vote (author, thread_id);

CREATE INDEX ON users_forum (lower(forum), author);
CREATE INDEX ON users_forum (author);
CREATE INDEX ON users_forum (forum);



