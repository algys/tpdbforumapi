package api.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class PostDAO {

    final private JdbcTemplate template;

    @Autowired
    public PostDAO(JdbcTemplate template){
        this.template = template;
    }

    public void createTable(){
        String query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXIST post (")
                .append("id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY")
                .append("parent BIGINT NOT NULL DEFAULT 0")
                .append("author_id BIGINT NOT NULL")
                .append("message TEXT NOT NULL")
                .append("isEdited BOOLEAN NOT NULL DEFAULT 0")
                .append("forum_id BIGINT NOT NULL")
                .append("thread_id BIGINT NOT NULL")
                .append("FOREIGN KEY (author_id) REFERENCE user(id)")
                .append("FOREIGN KEY (forum_id) REFERENCE forum(id)")
                .append("FOREIGN KEY (thread_id) REFERENCE thread(id)")
                .append("CHARACTER SET utf8 ;").toString();

        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXIST post ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM post ;").toString();

        return template.queryForObject(query, Integer.class);
    }
}
