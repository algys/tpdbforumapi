package api.DAO;

import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class UserDAO {

    final private JdbcTemplate template;

    @Autowired
    public UserDAO(JdbcTemplate template) {
        this.template = template;
    }

    public void dropTable() {
        String query = new StringBuilder()
                .append("DROP TABLE IF EXISTS users ;").toString();

        template.execute(query);
    }

    public void truncateTable(){
        String query = new StringBuilder()
                .append("TRUNCATE TABLE users CASCADE ;").toString();

        template.execute(query);

        query = new StringBuilder()
                .append("TRUNCATE TABLE users_forum CASCADE ;").toString();

        template.execute(query);
    }

    public void clear(){
        String query = new StringBuilder()
                .append("DELETE FROM users ;").toString();

        template.execute(query);
    }

    public int getCount() {
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM users ;").toString();

        return template.queryForObject(query, Integer.class);
    }

    public int add(User user) {
        String query = new StringBuilder()
                .append("INSERT INTO users(nickname, fullname, about, email) VALUES(?,?,?,?) ;").toString();

        try {
            template.update(query, user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());
        } catch (DuplicateKeyException e) {
            return Code.ERR_DUPLICATE;
        } catch (DataAccessException e) {
            return Code.ERR_UNDEFINED;
        }

        return Code.OK;
    }

    public int update(User user){
        StringBuilder queryBuilder = new StringBuilder()
                .append("UPDATE users SET ");

        boolean f = false;
        if(user.getFullname() != null){
            queryBuilder.append("fullname = '" + user.getFullname() + "',");
            f = true;
        }
        if(user.getEmail() != null){
            queryBuilder.append("email = '" + user.getEmail() + "',");
            f = true;
        }
        if(user.getAbout() != null){
            queryBuilder.append("about = '" + user.getAbout() + "',");
            f = true;
        }
        queryBuilder.deleteCharAt(queryBuilder.length()-1);
        queryBuilder.append(" WHERE nickname = '" + user.getNickname() + "';");

        if(f) {
            try {
                template.execute(queryBuilder.toString());
            } catch (DuplicateKeyException e) {
                return Code.ERR_DUPLICATE;
            } catch (DataAccessException e) {
                return Code.ERR_UNDEFINED;
            }
        }
        return Code.OK;
    }

    public User getByNickname(String nickname) {
        String query = String.format("SELECT * FROM users WHERE nickname = '%s';", nickname);
        User user = null;
        try {
            user = template.queryForObject(query, userMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return user;
    }

    public User getByEmail(String email) {
        String query = String.format("SELECT * FROM users WHERE email = '%s';", email);
        User user = null;
        try {
            user = template.queryForObject(query, userMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return user;
    }

    public List<User> getDuplicate(User user) {
        String query = String.format("SELECT * FROM users WHERE nickname = '%s' OR email = '%s';",
                user.getNickname(), user.getEmail());
        List<User> users = null;
        try {
            List<Map<String, Object>> rows = template.queryForList(query);
            users = new ArrayList<>();
            for(Map<String, Object>row: rows){
                users.add(new User(
                        row.get("nickname").toString(), row.get("fullname").toString(),
                        row.get("about").toString(), row.get("email").toString()
                ));
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return users;
    }
    public List<User> getByForum(String slug, Integer limit, String since, boolean desc) {

        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT users.* FROM users WHERE nickname IN ( ")
                .append("SELECT author FROM users_forum WHERE LOWER(users_forum.forum) = LOWER(?) ) ");

        if(since != null) {
            if (desc) {
                queryBuilder.append("AND LOWER(nickname COLLATE \"ucs_basic\") < LOWER(? COLLATE \"ucs_basic\") ");
            } else
                queryBuilder.append("AND LOWER(nickname COLLATE \"ucs_basic\") > LOWER(? COLLATE \"ucs_basic\") ");
        }

        if(desc) {
            queryBuilder.append("ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC ");
        } else
            queryBuilder.append("ORDER BY LOWER(nickname COLLATE \"ucs_basic\") ");

        queryBuilder.append("LIMIT ? ;");

        String query = queryBuilder.toString();

        ArrayList<User> users = null;
        try {
            List<Map<String, Object>> rows;
            if(since != null)
                rows = template.queryForList(query, slug, since, limit);
            else
                rows = template.queryForList(query, slug, limit);
            users = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                users.add(new User(
                            row.get("nickname").toString(), row.get("fullname").toString(),
                            row.get("about").toString(), row.get("email").toString()
                        )
                );
            }
        }
        catch (DataAccessException e){
            return null;
        }
        return users;
    }

    public boolean exist(String nickname) {
        String query = String.format("SELECT nickname FROM users WHERE nickname = '%s';", nickname);

        try {
            template.queryForObject(query, String.class);
        } catch (EmptyResultDataAccessException e){
            return false;
        }
        return true;
    }

    private final RowMapper<User> userMapper = (rs, num) -> {
        final String nickname = rs.getString("nickname");
        final String fullname = rs.getString("fullname");
        final String email = rs.getString("email");
        final String about = rs.getString("about");

        return new User(nickname, fullname, about, email);
    };
}
