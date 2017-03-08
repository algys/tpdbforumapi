package api.DAO;

import api.models.Forum;
import api.models.Post;
import api.models.Thread;
import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .append("CREATE TABLE IF NOT EXISTS forum ( ")
                .append("title VARCHAR(128) NOT NULL, ")
                .append("admin CITEXT NOT NULL, ")
                .append("slug CITEXT UNIQUE NOT NULL PRIMARY KEY, ")
                .append("posts BIGINT NOT NULL DEFAULT 0, ")
                .append("threads BIGINT NOT NULL DEFAULT 0, ")
                .append("FOREIGN KEY (admin) REFERENCES users(nickname)); ")
                .toString();

        System.out.println(query);
        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXISTS forum ;").toString();

        System.out.println(query);
        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM forum ;").toString();

        System.out.println(query);
        return template.queryForObject(query, Integer.class);
    }

    public Forum getBySlug(String slug) {
        String query = String.format("SELECT * FROM forum WHERE slug = '%s';", slug);
        Forum forum = null;
        try {
            System.out.println(query);
            forum = template.queryForObject(query, forumMapper);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
        }

        return forum;
    }

    public int add(Forum forum){
        String query = new StringBuilder()
                .append("INSERT INTO forum(title, admin, slug) ")
                .append("VALUES(?,?,?);").toString();

        try {
            System.out.println(query);
            template.update(query, forum.getTitle(), forum.getUser(), forum.getSlug());
        }
        catch (DuplicateKeyException e){
            System.out.println(e.getMessage());
            return Code.ERR_DUPLICATE;
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return Code.ERR_UNDEFINED;
        }
        return Code.OK;
    }

    private final RowMapper<Forum> forumMapper = (rs, num) -> {
        final String title = rs.getString("title");
        final String user = rs.getString("admin");
        final String slug = rs.getString("slug");
        final int posts = rs.getInt("posts");
        final int threads = rs.getInt("threads");

        return new Forum(title, user, slug, posts, threads);
    };
}
