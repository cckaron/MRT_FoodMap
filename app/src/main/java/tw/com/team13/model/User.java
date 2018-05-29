package tw.com.team13.model;

/**
 * @author Chun-Kai Kao on 2018/5/27 14:38
 * @github http://github.com/cckaron
 */

public class User {

    private long followers;
    private long following;
    private long posts;

    private String phone_number;
    private String email;
    private String profile_photo;
    private String user_id;
    private String username;
    private String website;
    private String display_name;
    private String description;

    public User(long followers, long following, long posts, String phone_number, String email, String profile_photo, String user_id, String username, String website, String display_name, String description) {
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.phone_number = phone_number;
        this.email = email;
        this.profile_photo = profile_photo;
        this.user_id = user_id;
        this.username = username;
        this.website = website;
        this.display_name = display_name;
        this.description = description;
    }

    public User() {
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "User{" +
                "followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                ", display_name='" + display_name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

