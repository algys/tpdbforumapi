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

    public void truncateTable(){
        template.execute(Queries.getTruncateUsers());
        template.execute(Queries.getTruncateUsersForum());
    }

    public int getCount() {
        return template.queryForObject(Queries.getCountUsers(), Integer.class);
    }

    public int add(User user) {
        try {
            template.update(Queries.getInsertUsers(), user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());
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
        User user = null;
        try {
            user = template.queryForObject(Queries.getGetByNicknameUsers(), userMapper, nickname);
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
        ArrayList<User> users = null;
        try {
            List<Map<String, Object>> rows;
            if(since != null) {
                if (desc) {
                    rows = template.queryForList(Queries.getSelectUsersByForumSinceDesc(),slug,since,limit);
                } else {
                    rows = template.queryForList(Queries.getSelectUsersByForumSince(),slug,since,limit);
                }
            } else {
                if(desc) {
                    rows = template.queryForList(Queries.getSelectUsersByForumDesc(),slug,limit);
                } else {
                    rows = template.queryForList(Queries.getSelectUsersByForum(),slug,limit);
                }
            }
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
