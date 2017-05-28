package api.DAO;

import api.models.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.*;
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
    final private Logger LOG = LogManager.getLogger();

    @Autowired
    public PostDAO(JdbcTemplate template){
        this.template = template;
    }

    public void truncateTable(){
        template.execute(Queries.getTruncatePost());
    }

    public int getCount(){
        return template.queryForObject(Queries.getCountPost(), Integer.class);
    }

    public List<Post> addMany(List<Post> posts){
        List<Post> newPosts = new ArrayList<>();
        try(Connection conn = template.getDataSource().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(Queries.getInsertPost(), Statement.NO_GENERATED_KEYS);
            Map<String, Integer> updatedForums = new HashMap<>();
            Integer seq;
            Timestamp curr_time = null;
            String time = null;
            if(posts.get(0).getCreated()==null){
                curr_time = template.queryForObject(Queries.getGetCurrentTimestamp(), Timestamp.class);
                time = curr_time.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }

            for (Post post: posts){
                seq = template.queryForObject(Queries.getGetPostNextId(), Integer.class);
                post.setId(seq);
                preparedStatement.setInt(1, post.getId());
                preparedStatement.setInt(2, post.getParent());
                preparedStatement.setString(3, post.getAuthor());
                preparedStatement.setString(4, post.getMessage());
                preparedStatement.setInt(5, post.getThread());
                preparedStatement.setString(6, post.getForum());
                preparedStatement.setInt(8, post.getParent());
                preparedStatement.setInt(9, post.getId());
                if(post.getCreated()==null) {
                    post.setCreated(time);
                    preparedStatement.setTimestamp(7, curr_time);
                } else
                    preparedStatement.setTimestamp(7, new Timestamp(ZonedDateTime.parse(post.getCreated()).toInstant().toEpochMilli()));

                if(!updatedForums.containsKey(post.getForum()))
                    updatedForums.put(post.getForum(), 1);
                else
                    updatedForums.put(post.getForum(), updatedForums.get(post.getForum())+1);

                template.queryForObject(Queries.getInsertUsersForum(), Object.class, post.getForum(),post.getAuthor());

                preparedStatement.addBatch();
                newPosts.add(post);
            }
            preparedStatement.executeBatch();
            preparedStatement.close();

            preparedStatement = conn.prepareStatement(Queries.getUpdatePostsForum(), Statement.NO_GENERATED_KEYS);
            for (Map.Entry<String, Integer> forum: updatedForums.entrySet()){
                preparedStatement.setInt(1, forum.getValue());
                preparedStatement.setString(2, forum.getKey());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            preparedStatement.close();
            conn.close();
        }
        catch (SQLException e){
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
                return null;
            } catch (NullPointerException e){
                return null;
            }
        }
        return getById(id);
    }

    public Post getById(int id){
        Post post = null;
        try {
            post = template.queryForObject(Queries.getGetByIdPost(), postMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return post;
    }

    public PostPage flatSort(int id, Integer limit, Integer offset, Boolean desc){
        ArrayList<Post> posts = null;
        try {
            List<Map<String, Object>> rows;
            if(desc)
                rows = template.queryForList(Queries.getPostSortFlatDesc(), id, limit, offset);
            else
                rows = template.queryForList(Queries.getPostSortFlat(), id, limit, offset);

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
            return null;
        }
        Integer newMarker = offset;
        if(!posts.isEmpty()){
            newMarker += posts.size();
        }
        return new PostPage(String.valueOf(newMarker), posts);
    }

    public PostPage treeSort(int id, Integer limit, Integer offset, Boolean desc){
        ArrayList<Post> posts = null;
        try {
            List<Map<String, Object>> rows;
            if(desc)
                rows = template.queryForList(Queries.getPostSortTreeDesc(), id, limit, offset);
            else
                rows = template.queryForList(Queries.getPostSortTree(), id, limit, offset);

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
                .append("SELECT id, created FROM post ")
                .append("WHERE thread_id = ? AND parent = 0 ");

        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT p.id, p.parent, p.author, p.message, p.isEdited, p.forum, p.thread_id, p.created FROM post AS p ")
                .append("WHERE p.post_path[1] = ? ");

        if(desc) {
            queryBuilder.append("ORDER BY post_path DESC ;");
            parentQueryBuilder.append("ORDER BY id DESC ");
        } else {
            queryBuilder.append("ORDER BY post_path ;");
            parentQueryBuilder.append("ORDER BY id ");
        }

        parentQueryBuilder.append("LIMIT ? OFFSET ? ;");
        String query = queryBuilder.toString();
        String parentQuery = parentQueryBuilder.toString();

        ArrayList<Post> posts = null;
        List<Map<String, Object>> parentRows;
        try {
            parentRows = template.queryForList(parentQuery, id ,limit, offset);
            posts = new ArrayList<>();
            for (Map<String, Object> parent: parentRows) {
                List<Map<String, Object>> rows;
                Integer p_id = Integer.parseInt(parent.get("id").toString());

                rows = template.queryForList(query, p_id);
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
