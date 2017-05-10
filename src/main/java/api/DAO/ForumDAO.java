package api.DAO;

import api.models.Forum;
import api.models.Post;
import api.models.Thread;
import api.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    final private Logger LOG = LogManager.getLogger();

    @Autowired
    public ForumDAO(JdbcTemplate template){
        this.template = template;
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXISTS forum ;").toString();

        template.execute(query);
    }

    public void truncateTable(){
        String query = new StringBuilder()
                .append("TRUNCATE TABLE forum CASCADE;").toString();

        template.execute(query);
    }

    public void clear(){
        String query = new StringBuilder()
                .append("DELETE FROM forum ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(slug) FROM forum ;").toString();

        return template.queryForObject(query, Integer.class);
    }

    public Forum getBySlug(String slug) {
        String query = String.format("SELECT * FROM forum WHERE LOWER(slug) = LOWER('%s');", slug);
        Forum forum = null;
        try {
            forum = template.queryForObject(query, forumMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return forum;
    }

    public boolean hasBySlug(String slug) {
        String query = String.format("SELECT slug FROM forum WHERE LOWER(slug) = LOWER('%s');", slug);
        try {
            template.queryForObject(query, String.class);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        return true;
    }

    public int add(Forum forum){
        String query = new StringBuilder()
                .append("INSERT INTO forum(title, admin, slug) ")
                .append("VALUES(?,?,?);").toString();

        try {
            template.update(query, forum.getTitle(), forum.getUser(), forum.getSlug());
        }
        catch (DuplicateKeyException e){
            return Code.ERR_DUPLICATE;
        }
        catch (DataAccessException e){
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
