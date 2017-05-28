CREATE OR REPLACE FUNCTION insert_users_forum(f CITEXT, a CITEXT) RETURNS void AS '
DECLARE
  exist INT;
BEGIN
  SELECT COUNT(author) INTO exist FROM users_forum
  WHERE lower(forum) = lower(f) AND author = a LIMIT 1;

  IF exist = 0 THEN
    INSERT INTO users_forum(forum, author) VALUES(f,a);
  END IF;
END;
' LANGUAGE plpgsql;
