package tw.com.team13.model;

/**
 * @author Chun-Kai Kao on 2018/5/27 14:38
 * @github http://github.com/cckaron
 */

public class User {

    private String user_id;
    private String phone_number;
    private String email;
    private String username;

    public User() {

    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
