package api.DAO;

import org.springframework.context.annotation.Bean;

/**
 * Created by algys on 28.05.17.
 */
public class Queries {
    private static final String truncateForum = "TRUNCATE TABLE forum CASCADE;";
    private static final String truncateUsers = "TRUNCATE TABLE users CASCADE;";
    private static final String truncateUsersForum = "TRUNCATE TABLE users_forum CASCADE;";
    private static final String truncatePost = "TRUNCATE TABLE post CASCADE;";
    private static final String truncateThread = "TRUNCATE TABLE thread CASCADE;";
    private static final String truncateVote = "TRUNCATE TABLE vote CASCADE;";

    private static final String countForum = "SELECT COUNT(slug) FROM forum ;";
    private static final String countUsers = "SELECT COUNT(nickname) FROM users ;";
    private static final String countPost = "SELECT COUNT(id) FROM post ;";
    private static final String countThread = "SELECT COUNT(id) FROM thread ;";
    private static final String countVote = "SELECT COUNT(*) FROM vote ;";

    private static final String getBySlugForum = "SELECT * FROM forum WHERE LOWER(slug) = LOWER(?);";
    private static final String getByIdPost = "SELECT * FROM post WHERE id = ?;";
    private static final String getBySlugThread = "SELECT * FROM thread WHERE LOWER(slug) = LOWER(?);";
    private static final String getByIdThread = "SELECT * FROM thread WHERE id = ?;";
    private static final String getByNicknameUsers = "SELECT * FROM users WHERE LOWER(nickname) = LOWER(?);";
    private static final String getSlugForumBySlug = "SELECT slug FROM forum WHERE LOWER(slug) = LOWER(?);";
    private static final String insertForum =
            "INSERT INTO forum(title, admin, slug) " +
            "VALUES(?,?,?);";

    private static final String insertThread =
            "INSERT INTO thread(title, author, forum, message) " +
            "VALUES(?,?,?,?) RETURNING id;";

    private static final String insertUsersForum = "SELECT insert_users_forum(?::CITEXT,?::CITEXT)";
    private static final String insertUsers = "INSERT INTO users(nickname, fullname, about, email) VALUES(?,?,?,?) ;";
    private static final String insertPost = "INSERT INTO post(id, parent, author, message, thread_id, forum, created, post_path) " +
            "VALUES(?,?,?,?,?,?,?,array_append((SELECT post_path FROM post WHERE id = ?), ?));";
    private static final String updatePostsForum = "UPDATE forum SET posts = posts + ? " +
            "WHERE slug = ?; ";
    private static final String getPostNextId = "SELECT nextval('post_id_seq');";
    private static final String getCurrentTimestamp = "SELECT current_timestamp ;";

    private static final String updateCreatedThread = "UPDATE thread SET created = ? WHERE id = ? ;";
    private static final String updateSlugThread = "UPDATE thread SET slug = ? WHERE id = ? ;";
    private static final String updateThreadsForum =
            "UPDATE forum SET threads = threads + 1 " +
            "WHERE slug = ? ;";
    private static final String selectUsersByForum = "SELECT users.* FROM users WHERE nickname IN ( " +
            "SELECT author FROM users_forum WHERE LOWER(users_forum.forum) = LOWER(?) ) " +
            "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") " +
            "LIMIT ?;";
    private static final String selectUsersByForumDesc = "SELECT users.* FROM users WHERE nickname IN ( " +
            "SELECT author FROM users_forum WHERE LOWER(users_forum.forum) = LOWER(?) ) " +
            "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC " +
            "LIMIT ?;";
    private static final String selectUsersByForumSince = "SELECT users.* FROM users WHERE nickname IN ( " +
            "SELECT author FROM users_forum WHERE LOWER(users_forum.forum) = LOWER(?) ) " +
            "AND LOWER(nickname COLLATE \"ucs_basic\") > LOWER(? COLLATE \"ucs_basic\") " +
            "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") " +
            "LIMIT ?;";
    private static final String selectUsersByForumSinceDesc = "SELECT users.* FROM users WHERE nickname IN ( " +
            "SELECT author FROM users_forum WHERE LOWER(users_forum.forum) = LOWER(?) ) " +
            "AND LOWER(nickname COLLATE \"ucs_basic\") < LOWER(? COLLATE \"ucs_basic\") " +
            "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC " +
            "LIMIT ?;";

    public static String getTruncateForum() {
        return truncateForum;
    }

    public static String getTruncateUsers() {
        return truncateUsers;
    }

    public static String getTruncatePost() {
        return truncatePost;
    }

    public static String getTruncateThread() {
        return truncateThread;
    }

    public static String getTruncateVote() {
        return truncateVote;
    }

    public static String getCountForum() {
        return countForum;
    }

    public static String getCountUsers() {
        return countUsers;
    }

    public static String getCountPost() {
        return countPost;
    }

    public static String getCountThread() {
        return countThread;
    }

    public static String getCountVote() {
        return countVote;
    }

    public static String getGetBySlugForum() {
        return getBySlugForum;
    }

    public static String getGetByIdPost() {
        return getByIdPost;
    }

    public static String getGetBySlugThread() {
        return getBySlugThread;
    }

    public static String getGetByIdThread() {
        return getByIdThread;
    }

    public static String getGetByNicknameUsers() {
        return getByNicknameUsers;
    }

    public static String getInsertForum() {
        return insertForum;
    }

    public static String getInsertThread() {
        return insertThread;
    }

    public static String getInsertUsersForum() {
        return insertUsersForum;
    }

    public static String getInsertPost() {
        return insertPost;
    }

    public static String getUpdatePostsForum() {
        return updatePostsForum;
    }

    public static String getGetPostNextId() {
        return getPostNextId;
    }

    public static String getGetCurrentTimestamp() {
        return getCurrentTimestamp;
    }

    public static String getUpdateCreatedThread() {
        return updateCreatedThread;
    }

    public static String getUpdateSlugThread() {
        return updateSlugThread;
    }

    public static String getUpdateThreadsForum() {
        return updateThreadsForum;
    }

    public static String getGetSlugForumBySlug() {
        return getSlugForumBySlug;
    }

    public static String getTruncateUsersForum() {
        return truncateUsersForum;
    }

    public static String getInsertUsers() {
        return insertUsers;
    }

    public static String getSelectUsersByForum() {
        return selectUsersByForum;
    }

    public static String getSelectUsersByForumDesc() {
        return selectUsersByForumDesc;
    }

    public static String getSelectUsersByForumSince() {
        return selectUsersByForumSince;
    }

    public static String getSelectUsersByForumSinceDesc() {
        return selectUsersByForumSinceDesc;
    }
}
