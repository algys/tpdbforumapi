package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by algys on 24.02.17.
 */
public class User {
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String fullname;
    @JsonProperty
    private String about;
    @JsonProperty
    private String email;

    @JsonCreator
    public User(@JsonProperty("nickname") String nickname, @JsonProperty("fullname") String fullname,
                @JsonProperty("about") String about, @JsonProperty("email") String email){
        this.nickname = nickname;
        this.fullname = fullname;
        this.about = about;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
