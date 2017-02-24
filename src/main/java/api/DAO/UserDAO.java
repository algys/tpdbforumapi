package api.DAO;

import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class UserDAO {

    final private JdbcTemplate template;

    @Autowired
    public UserDAO(JdbcTemplate template){
        this.template = template;
    }

    public void createTable(){
        String query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXIST user (")
                .append("id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY")
                .append("nickname VARCHAR(128) UNIQUE NOT NULL")
                .append("fullname VARCHAR(128) NOT NULL")
                .append("about TEXT NOT NULL")
                .append("email VARCHAR(128) UNIQUE NOT NULL")
                .append("CHARACTER SET utf8 ;").toString();

        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXIST user ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM user ;").toString();

        return template.queryForObject(query, Integer.class);
    }
/*
    public User add(User user){

    }
*/
}
