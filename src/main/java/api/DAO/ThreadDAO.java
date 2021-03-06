package api.DAO;

import api.models.*;
import api.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void truncateTable(){
        String query = new StringBuilder()
                .append("TRUNCATE TABLE thread CASCADE ;").toString();

        template.execute(query);
    }

    public void clear(){
        String query = new StringBuilder()
                .append("DELETE FROM thread ;").toString();

        template.execute(query);
    }

    public Thread add(Thread thread){
        Thread newThread = null;
        try {
            int id = template.queryForObject(Queries.getInsertThread(), Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage());
            template.update(Queries.getUpdateThreadsForum(), thread.getForum());
            template.queryForObject(Queries.getInsertUsersForum(), Object.class,thread.getForum(), thread.getAuthor());
            if(thread.getCreated() != null) {
                String st = ZonedDateTime.parse(thread.getCreated()).format(DateTimeFormatter.ISO_INSTANT);
                template.update(Queries.getUpdateCreatedThread(), new Timestamp(ZonedDateTime.parse(st).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()), id);
            }
            if(thread.getSlug() != null) {
                template.update(Queries.getUpdateSlugThread(), thread.getSlug(), id);
            }
            newThread = getById(id);
        }
        catch (DataAccessException e){
            return null;
        }

        return newThread;
    }

    public Thread update(ThreadUpdate threadUpdate, int id){
        StringBuilder queryBuilder = new StringBuilder()
                .append("UPDATE thread SET ");

        boolean f = false;
        if(threadUpdate.getTitle() != null){
            queryBuilder.append("title = '" + threadUpdate.getTitle() + "',");
            f = true;
        }
        if(threadUpdate.getMessage() != null){
            queryBuilder.append("message = '" + threadUpdate.getMessage() + "',");
            f = true;
        }
        queryBuilder.deleteCharAt(queryBuilder.length()-1);
        queryBuilder.append(" WHERE id = '" + id + "' ;");

        if(f) {
            try {
                template.update(queryBuilder.toString());
            } catch (DataAccessException e) {
                return null;
            }
        }
        return getById(id);
    }

    public Thread getDuplicate(Thread thread) {
        Thread dupThread;
        try {
            dupThread = template.queryForObject(Queries.getGetBySlugThread(), threadMapper, thread.getSlug());
        } catch (DataAccessException e) {
            return null;
        }

        return dupThread;
    }

    public Thread getById(int id) {
        Thread thread;
        try {
            thread = template.queryForObject(Queries.getGetByIdThread(), threadMapper, id);
        } catch (DataAccessException e) {
            return null;
        }

        return thread;
    }

    public Thread getBySlug(String slug) {
        Thread thread;
        try {
            thread = template.queryForObject(Queries.getGetBySlugThread(), threadMapper, slug);
        } catch (DataAccessException e) {
            return null;
        }

        return thread;
    }

    public List<Thread> getByForum(String slug, Integer limit, String since, boolean desc) {
        Timestamp time = null;
        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT * FROM thread WHERE LOWER(forum) = LOWER(?) ");

        if(since != null) {
            String st = ZonedDateTime.parse(since).format(DateTimeFormatter.ISO_INSTANT);
            time = new Timestamp(ZonedDateTime.parse(st).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        }

        if(since != null) {
            if (desc) {
                queryBuilder.append("AND created <= ? ");
            } else
                queryBuilder.append("AND created >= ? ");
        }

        if(desc) {
            queryBuilder.append("ORDER BY created DESC ");
        } else
            queryBuilder.append("ORDER BY created ");

        queryBuilder.append("LIMIT ? ;");

        String query = queryBuilder.toString();

        ArrayList<Thread> threads = null;
        try {
            List<Map<String, Object>> rows;
            if(since != null)
                rows = template.queryForList(query, slug, time, limit);
            else
                rows = template.queryForList(query, slug, limit);

            threads = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                String sl;
                if(row.get("slug") == null){
                    sl = null;
                } else {
                    sl = row.get("slug").toString();
                }
                threads.add(new Thread(
                                Integer.parseInt(row.get("id").toString()), row.get("title").toString(),
                                row.get("author").toString(), row.get("forum").toString(),
                                row.get("message").toString(), Integer.parseInt(row.get("votes").toString()),
                                sl, Timestamp.valueOf(row.get("created").toString())
                                .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        )
                );
            }
        }
        catch (DataAccessException e){
            return null;
        }
        return threads;
    }

    public int getCount(){
        return template.queryForObject(Queries.getCountThread(), Integer.class);
    }

    private final RowMapper<Thread> threadMapper = (rs, num) -> {
        final int id = rs.getInt("id");
        final String title = rs.getString("title");
        final String author = rs.getString("author");
        final String forum = rs.getString("forum");
        final String message = rs.getString("message");
        final int votes = rs.getInt("votes");
        final String slug = rs.getString("slug");
        final String created = rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new Thread(id, title, author, forum, message, votes, slug, created);
    };
}
