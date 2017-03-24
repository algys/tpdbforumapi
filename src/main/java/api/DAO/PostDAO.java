package api.DAO;

import api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .append("CREATE TABLE IF NOT EXISTS post ( ")
                .append("id SERIAL PRIMARY KEY, ")
                .append("parent BIGINT NOT NULL DEFAULT 0, ")
                .append("author CITEXT NOT NULL, ")
                .append("message TEXT NOT NULL, ")
                .append("isEdited BOOLEAN NOT NULL DEFAULT false, ")
                .append("forum CITEXT NOT NULL, ")
                .append("thread_id BIGINT NOT NULL, ")
                .append("created TIMESTAMP NOT NULL DEFAULT current_timestamp, ")
                .append("FOREIGN KEY (author) REFERENCES users(nickname), ")
                .append("FOREIGN KEY (forum) REFERENCES forum(slug), ")
                .append("FOREIGN KEY (thread_id) REFERENCES thread(id)); ")
                .append("CREATE UNIQUE INDEX ON post (id); ")
                .append("CREATE INDEX ON post (author); ")
                .append("CREATE INDEX ON post (forum); ")
                .append("CREATE INDEX ON post (thread_id); ")
                .toString();

        template.execute(query);
    }

    public void dropTable(){
        String query = new StringBuilder()
                .append("DROP TABLE IF EXISTS post ;").toString();

        template.execute(query);
    }

    public void truncateTable(){
        String query = new StringBuilder()
                .append("TRUNCATE TABLE post CASCADE ;").toString();

        template.execute(query);
    }

    public int getCount(){
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM post ;").toString();

        return template.queryForObject(query, Integer.class);
    }

    public Post add(Post post){
        String query = new StringBuilder()
                .append("INSERT INTO post(parent, author, message, thread_id, forum) ")
                .append("VALUES(?,?,?,?,?) RETURNING * ;").toString();
        String subQuery = new StringBuilder()
                .append("UPDATE forum SET posts = posts + 1 ")
                .append("WHERE slug = ? ;")
                .toString();
        Post newPost;
        try {
            newPost = template.queryForObject( query, postMapper,
                    post.getParent(), post.getAuthor(), post.getMessage(), post.getThread(), post.getForum());
            template.update(subQuery, post.getForum());
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
        return newPost;
    }

    public List<Post> addMany(List<Post> posts){
        String query = new StringBuilder()
                .append("INSERT INTO post(parent, author, message, thread_id, forum) ")
                .append("VALUES(?,?,?,?,?) RETURNING * ;").toString();
        String subQuery = new StringBuilder()
                .append("UPDATE forum SET posts = posts + 1 ")
                .append("WHERE slug = ? ;")
                .toString();

        List<Post> newPosts = new ArrayList<>();

        try {
            for( Post post: posts) {
                Post newPost = template.queryForObject( query, postMapper,
                        post.getParent(), post.getAuthor(), post.getMessage(), post.getThread(), post.getForum());
                template.update(subQuery, post.getForum());
                newPosts.add(newPost);
            }
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
        return newPosts;
    }

    public Post update(PostUpdate postUpdate, int id){
        StringBuilder queryBuilder = new StringBuilder()
                .append("UPDATE post SET isEdited = true , ");

        boolean f = false;
        if(postUpdate.getMessage() != null){
            queryBuilder.append("message = '" + postUpdate.getMessage() + "' ");
            f = true;
        }
        queryBuilder.append(" WHERE id = '" + id + "' ;");

        if(f) {
            try {
                Post oldPost = getById(id);
                if(oldPost.getMessage().equals(postUpdate.getMessage())){
                    return oldPost;
                }
                template.update(queryBuilder.toString());
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
                return null;
            } catch (NullPointerException e){
                return null;
            }
        }
        return getById(id);
    }

    public Post getById(int id){
        String query = String.format("SELECT * FROM post WHERE id = '%d';", id);
        Post post = null;
        try {
            post = template.queryForObject(query, postMapper);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
        }

        return post;
    }

    public PostPage flatSort(int id, Integer limit, Integer offset, Boolean desc){
        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT * FROM post WHERE thread_id = ? ");

        if(desc) {
            queryBuilder.append("ORDER BY id DESC ");
        } else
            queryBuilder.append("ORDER BY id ");

        queryBuilder.append("LIMIT ? ");
        queryBuilder.append("OFFSET ? ;");
        String query = queryBuilder.toString();

        ArrayList<Post> posts = null;
        try {
            List<Map<String, Object>> rows;
            rows = template.queryForList(query, id, limit, offset);

            posts = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                posts.add(new Post(
                                Integer.parseInt(row.get("id").toString()), Integer.parseInt(row.get("parent").toString()),
                                row.get("author").toString(), row.get("message").toString(),
                                Boolean.parseBoolean(row.get("isEdited").toString()), row.get("forum").toString(),
                                Integer.parseInt(row.get("thread_id").toString()), Timestamp.valueOf(row.get("created").toString())
                                .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        )
                );
            }
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
        Integer newMarker = offset;
        if(!posts.isEmpty()){
            newMarker += posts.size();
        }
        return new PostPage(String.valueOf(newMarker), posts);
    }

    public PostPage treeSort(int id, Integer limit, Integer offset, Boolean desc){
        StringBuilder queryBuilder = new StringBuilder()
                .append("WITH RECURSIVE recursepost (id, parent, path, author, message, isEdited, forum, thread_id, created) AS ( ")
                .append("SELECT id, parent, array[id], author, message, isEdited, forum, thread_id, created FROM post WHERE parent = 0 ")
                .append("UNION ALL ")
                .append("SELECT p.id, p.parent, array_append(path, p.id), p.author, p.message, p.isEdited, p.forum, p.thread_id, p.created FROM post AS p ")
                .append("JOIN recursepost rp ON rp.id = p.parent ) ")
                .append("SELECT id, parent, path, author, message, isEdited, forum, thread_id, created FROM recursepost WHERE thread_id = ? ");

        if(desc) {
            queryBuilder.append("ORDER BY path DESC ");
        } else
            queryBuilder.append("ORDER BY path ");

        queryBuilder.append("LIMIT ? ");
        queryBuilder.append("OFFSET ? ;");
        String query = queryBuilder.toString();

        ArrayList<Post> posts = null;
        try {
            List<Map<String, Object>> rows;
            rows = template.queryForList(query, id, limit, offset);

            posts = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                posts.add(new Post(
                                Integer.parseInt(row.get("id").toString()), Integer.parseInt(row.get("parent").toString()),
                                row.get("author").toString(), row.get("message").toString(),
                                Boolean.parseBoolean(row.get("isEdited").toString()), row.get("forum").toString(),
                                Integer.parseInt(row.get("thread_id").toString()), Timestamp.valueOf(row.get("created").toString())
                                .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        )
                );
            }
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
        Integer newMarker = offset;
        if(!posts.isEmpty()){
            newMarker += posts.size();
        }
        return new PostPage(String.valueOf(newMarker), posts);
    }

    public PostPage parentTreeSort(int id, Integer limit, Integer offset, Boolean desc){
        StringBuilder parentQueryBuilder = new StringBuilder()
                .append("SELECT id FROM post WHERE parent = 0 AND thread_id = ? ");

        StringBuilder queryBuilder = new StringBuilder()
                .append("WITH RECURSIVE recursepost (id, parent, path, author, message, isEdited, forum, thread_id, created) AS ( ")
                .append("SELECT id, parent, array[id], author, message, isEdited, forum, thread_id, created FROM post WHERE id = ? ")
                .append("UNION ALL ")
                .append("SELECT p.id, p.parent, array_append(path, p.id), p.author, p.message, p.isEdited, p.forum, p.thread_id, p.created FROM post AS p ")
                .append("JOIN recursepost rp ON rp.id = p.parent ) ")
                .append("SELECT id, parent, path, author, message, isEdited, forum, thread_id, created FROM recursepost WHERE thread_id = ? ");

        if(desc) {
            queryBuilder.append("ORDER BY path DESC ;");
            parentQueryBuilder.append("ORDER BY id DESC ");
        } else {
            queryBuilder.append("ORDER BY path ;");
            parentQueryBuilder.append("ORDER BY id ");
        }
        parentQueryBuilder.append("LIMIT ? OFFSET ? ;");

        String query = queryBuilder.toString();
        String parentQuery = parentQueryBuilder.toString();

        ArrayList<Post> posts = null;
        List<Map<String, Object>> parentRows;
        try {
            parentRows = template.queryForList(parentQuery, id, limit, offset);
            posts = new ArrayList<>();
            for (Map<String, Object> parent: parentRows) {
                List<Map<String, Object>> rows;
                rows = template.queryForList(query, Integer.parseInt(parent.get("id").toString()), id);
                for (Map<String, Object> row : rows) {
                    posts.add(new Post(
                                    Integer.parseInt(row.get("id").toString()), Integer.parseInt(row.get("parent").toString()),
                                    row.get("author").toString(), row.get("message").toString(),
                                    Boolean.parseBoolean(row.get("isEdited").toString()), row.get("forum").toString(),
                                    Integer.parseInt(row.get("thread_id").toString()), Timestamp.valueOf(row.get("created").toString())
                                    .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            )
                    );
                }
            }
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
        Integer newMarker = offset;
        if(!posts.isEmpty()){
            newMarker += parentRows.size();
        }
        return new PostPage(String.valueOf(newMarker), posts);
    }

    public PostPage getByThread(int id, Integer limit, String marker, String sort, Boolean desc){
        PostPage page = null;

        if(sort.toLowerCase().equals("flat")){
            page = flatSort(id, limit, Integer.parseInt(marker), desc);
        }
        if(sort.toLowerCase().equals("tree")){
            page = treeSort(id, limit, Integer.parseInt(marker), desc);
        }
        if(sort.toLowerCase().equals("parent_tree")){
            page = parentTreeSort(id, limit, Integer.parseInt(marker), desc);
        }
        return page;
    }

    private final RowMapper<Post> postMapper = (rs, num) -> {
        final int id = rs.getInt("id");
        final int parent = rs.getInt("parent");
        final String author = rs.getString("author");
        final String message = rs.getString("message");
        final boolean isEdited = rs.getBoolean("isEdited");
        final String forum = rs.getString("forum");
        final int thread = rs.getInt("thread_id");
        final String created = rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new Post(id, parent, author, message, isEdited, forum, thread, created);
    };
}
