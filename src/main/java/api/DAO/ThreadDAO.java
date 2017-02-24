package api.DAO;

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
public class ThreadDAO {

    final private JdbcTemplate template;

    @Autowired
    public ThreadDAO(JdbcTemplate template){
        this.template = template;
    }

    public void createTable(){
        String query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXIST thread (")
                .append("id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY")
                .append("title VARCHAR(128) NOT NULL")
                .append("author_id BIGINT NOT NULL")
                .append("forum_id BIGINT NOT NULL")
                .append("message TEXT NOT NULL")
                .append("votes BIGINT NOT NULL DEFAULT 0")
                .append("slug VARCHAR(128) UNIQUE NOT NULL")
                .append("created TIMESTAMP NOT NULL DEFAULT now")
                .append("FOREIGN KEY (author_id) REFERENCE user(id)")
                .append("FOREIGN KEY (forum_id) REFERENCE forum(id)")
                .append("CHARACTER SET utf8 ;").toString();

        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXIST thread ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM thread ;").toString();

        return template.queryForObject(query, Integer.class);
    }
}
