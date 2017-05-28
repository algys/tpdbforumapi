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

    public void truncateTable(){
        template.execute(Queries.getTruncateForum());
    }

    public int getCount(){
        return template.queryForObject(Queries.getCountForum(), Integer.class);
    }

    public Forum getBySlug(String slug) {
        Forum forum = null;
        try {
            forum = template.queryForObject(Queries.getGetBySlugForum(), forumMapper, slug);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return forum;
    }

    public boolean hasBySlug(String slug) {
        try {
            template.queryForObject(Queries.getGetSlugForumBySlug(), String.class, slug);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        return true;
    }

    public int add(Forum forum){
        try {
            template.update(Queries.getInsertForum(), forum.getTitle(), forum.getUser(), forum.getSlug());
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
