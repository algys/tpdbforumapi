package api.DAO;

import api.models.Forum;
import api.models.Status;
import api.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class ServiceDAO {

    final private JdbcTemplate template;
    final private UserDAO userDAO;
    final private ForumDAO forumDAO;
    final private ThreadDAO threadDAO;
    final private PostDAO postDAO;

    @Autowired
    public ServiceDAO(JdbcTemplate template, UserDAO userDAO,
                      ForumDAO forumDAO, ThreadDAO threadDAO,
                      PostDAO postDAO){

        this.template = template;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.postDAO = postDAO;
        createTables();
    }

    public void createTables(){
        userDAO.createTable();
        forumDAO.createTable();
        threadDAO.createTable();
        postDAO.createTable();
    }

    public void dropTables(){
        postDAO.dropTable();
        threadDAO.dropTable();
        forumDAO.dropTable();
        userDAO.dropTable();
    }

    public Status getStatus(){
        return new Status(userDAO.getCount(), forumDAO.getCount(), threadDAO.getCount(), postDAO.getCount());
    }

}
