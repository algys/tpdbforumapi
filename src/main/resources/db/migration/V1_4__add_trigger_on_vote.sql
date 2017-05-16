CREATE OR REPLACE FUNCTION reset_vote_thread() RETURNS TRIGGER AS $$
DECLARE
  thread_id INT;
  voice INT;
  old_voice INT;
BEGIN
  thread_id = NEW.thread_id;
  voice =  NEW.voice;

  IF TG_OP = 'UPDATE' THEN
    old_voice = OLD.voice;
    UPDATE thread SET votes = votes - old_voice + voice WHERE id = thread_id;
  ELSEIF TG_OP = 'INSERT' THEN
    UPDATE thread SET votes = votes + voice WHERE id = thread_id;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER t_vote
AFTER INSERT OR UPDATE ON vote FOR EACH ROW EXECUTE PROCEDURE reset_vote_thread ();

