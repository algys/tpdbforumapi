package api.DAO;

import api.models.Forum;
import api.models.Status;
import api.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class ServiceDAO {

    final private UserDAO userDAO;
    final private ForumDAO forumDAO;
    final private ThreadDAO threadDAO;
    final private PostDAO postDAO;
    final private VoteDAO voteDAO;

    @Autowired
    public ServiceDAO(UserDAO userDAO,
                      ForumDAO forumDAO, ThreadDAO threadDAO,
                      PostDAO postDAO, VoteDAO voteDAO){

        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.postDAO = postDAO;
        this.voteDAO = voteDAO;
  //      dropTables();
  //      createTables();
    }

    public void dropTables(){
        voteDAO.dropTable();
        postDAO.dropTable();
        threadDAO.dropTable();
        forumDAO.dropTable();
        userDAO.dropTable();
    }

    public void clearTables(){
        voteDAO.clear();
        postDAO.clear();
        threadDAO.clear();
        forumDAO.clear();
        userDAO.clear();
    }

    public void truncateTables(){
        voteDAO.truncateTable();
        postDAO.truncateTable();
        threadDAO.truncateTable();
        forumDAO.truncateTable();
        userDAO.truncateTable();
    }

    public Status getStatus(){
        return new Status(userDAO.getCount(), forumDAO.getCount(), threadDAO.getCount(), postDAO.getCount());
    }

}
