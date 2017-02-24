package api.DAO;

import api.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class ForumDAO {

    final private JdbcTemplate template;

    @Autowired
    public ForumDAO(JdbcTemplate template){
        this.template = template;
    }

    public void createTable(){
        String query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXIST forum (")
                .append("id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY")
                .append("title VARCHAR(128) UNIQUE NOT NULL")
                .append("user_id BIGINT NOT NULL")
                .append("slug VARCHAR(128) UNIQUE NOT NULL")
                .append("FOREIGN KEY (user_id) REFERENCE user(id) )")
                .append("CHARACTER SET utf8;").toString();

        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXIST forum ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM forum ;").toString();

        return template.queryForObject(query, Integer.class);
    }
/*
    public Forum add(Forum forum){
        String query = new StringBuilder()
                .append("INSERT INTO forum(user_id, )")
    } */
}
