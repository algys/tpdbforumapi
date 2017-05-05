CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users (
  nickname CITEXT UNIQUE NOT NULL PRIMARY KEY,
  fullname varchar(128) NOT NULL,
  about text NOT NULL,
  email CITEXT UNIQUE NOT NULL
);
CREATE UNIQUE INDEX ON users (email);

CREATE TABLE IF NOT EXISTS forum (
  title VARCHAR(128) NOT NULL,
  admin CITEXT NOT NULL,
  slug CITEXT UNIQUE NOT NULL PRIMARY KEY,
  posts BIGINT NOT NULL DEFAULT 0,
  threads BIGINT NOT NULL DEFAULT 0,
  FOREIGN KEY (admin) REFERENCES users(nickname)
);
CREATE INDEX ON forum (title);
CREATE INDEX ON forum (admin);

CREATE TABLE IF NOT EXISTS thread (
  id SERIAL PRIMARY KEY,
  title VARCHAR(128) NOT NULL,
  author CITEXT NOT NULL,
  forum CITEXT NOT NULL,
  message TEXT NOT NULL,
  votes BIGINT NOT NULL DEFAULT 0,
  slug CITEXT UNIQUE,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp,
  FOREIGN KEY (author) REFERENCES users(nickname),
  FOREIGN KEY (forum) REFERENCES forum(slug)
);
CREATE INDEX ON thread (slug);
CREATE INDEX ON thread (forum);
CREATE INDEX ON thread (created);

CREATE TABLE IF NOT EXISTS post (
  id SERIAL PRIMARY KEY,
  parent BIGINT NOT NULL DEFAULT 0,
  author CITEXT NOT NULL,
  message TEXT NOT NULL,
  isEdited BOOLEAN NOT NULL DEFAULT false,
  forum CITEXT NOT NULL,
  thread_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT current_timestamp,
  FOREIGN KEY (author) REFERENCES users(nickname),
  FOREIGN KEY (forum) REFERENCES forum(slug),
  FOREIGN KEY (thread_id) REFERENCES thread(id)
);
CREATE INDEX ON post (parent);
CREATE INDEX ON post (author);
CREATE INDEX ON post (forum);
CREATE INDEX ON post (thread_id);

CREATE TABLE IF NOT EXISTS vote (
  author CITEXT NOT NULL,
  thread_id BIGINT NOT NULL,
  voice INT NOT NULL,
  FOREIGN KEY (author) REFERENCES users(nickname),
  FOREIGN KEY (thread_id) REFERENCES thread(id)
);

CREATE INDEX IF NOT EXISTS vote_id ON vote (author, thread_id);