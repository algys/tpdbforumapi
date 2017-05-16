CREATE OR REPLACE FUNCTION add_to_user_forum() RETURNS TRIGGER AS $$
DECLARE
  nick CITEXT;
  forum CITEXT;
BEGIN
  nick = NEW.author;
  forum =  NEW.forum;
  INSERT INTO users_forum(author,forum) values (nick, forum) ON CONFLICT DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER t_post_insert
AFTER INSERT ON post FOR EACH ROW EXECUTE PROCEDURE add_to_user_forum ();

CREATE TRIGGER t_thread_insert
AFTER INSERT ON thread FOR EACH ROW EXECUTE PROCEDURE add_to_user_forum ();
