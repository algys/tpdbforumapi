package api.DAO;

import api.models.Thread;
import api.models.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 08.03.17.
 */

@Service
@Transactional
public class VoteDAO {

    final private JdbcTemplate template;
    final private UserDAO userDAO;
    final private ForumDAO forumDAO;
    final private ThreadDAO threadDAO;
    final private PostDAO postDAO;

    @Autowired
    public VoteDAO(JdbcTemplate template, UserDAO userDAO,
                      ForumDAO forumDAO, ThreadDAO threadDAO,
                      PostDAO postDAO){

        this.template = template;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.postDAO = postDAO;
    }

    public void dropTable() {
        String query = new StringBuilder()
                .append("DROP TABLE IF EXISTS vote ;").toString();

        template.execute(query);
    }

    public void truncateTable(){
        String query = new StringBuilder()
                .append("TRUNCATE TABLE vote CASCADE ;").toString();

        template.execute(query);
    }

    public void clear(){
        String query = new StringBuilder()
                .append("DELETE FROM vote ;").toString();
        template.execute(query);
    }

    public Thread add(Vote vote, int thread_id){
        String insertQuery = new StringBuilder()
                .append("INSERT INTO vote (author, thread_id, voice) ")
                .append("VALUES(?,?,?) ")
                .append("ON CONFLICT(author, thread_id) DO UPDATE ")
                .append("SET voice = excluded.voice ;")
                .toString();
        try{
            template.update(insertQuery, vote.getNickname(), thread_id, vote.getVoice());
        } catch (DataAccessException e) {
            return null;
        }
        return threadDAO.getById(thread_id);
    }

}
