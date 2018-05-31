package tw.com.team13.model;

/**
 * @author Chun-Kai Kao on 2018/5/31 上午 02:32
 * @github http://github.com/cckaron
 */
public class Like {
    private String user_id;

    public Like(String user_id) {
        this.user_id = user_id;
    }

    public Like() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Like{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
