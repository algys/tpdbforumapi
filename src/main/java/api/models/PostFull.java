package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by algys on 24.02.17.
 */

public class PostFull {
    @JsonProperty
    private Post post;
    @JsonProperty
    private User author;
    @JsonProperty
    private Thread thread;
    @JsonProperty
    private Forum forum;

    @JsonCreator
    public PostFull(@JsonProperty("post") Post post,
                    @JsonProperty("author") User author,
                    @JsonProperty("thread") Thread thread,
                    @JsonProperty("forum") Forum forum){
        this.post = post;
        this.author = author;
        this.thread = thread;
        this.forum = forum;
    }

    public Post getPost() {
        return post;
    }

    public User getAuthor() {
        return author;
    }

    public Thread getThread() {
        return thread;
    }

    public Forum getForum() {
        return forum;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
