package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by algys on 08.03.17.
 */
public class PostPage {
    @JsonProperty
    String marker;
    @JsonProperty
    List<Post> posts;

    @JsonCreator
    public PostPage(@JsonProperty("marker") String marker, @JsonProperty("posts") List<Post> posts){
        this.marker = marker;
        this.posts = posts;
    }

    public String getMarker() {
        return marker;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
