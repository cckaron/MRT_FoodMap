package tw.com.team13.model;

/**
 * @author Chun-Kai Kao on 2018/5/30 上午 12:51
 * @github http://github.com/cckaron
 */

public class Photo {

    private String caption;
    private String date_created;
    private String image_path;
    private String user_id;
    private String tags;

    public Photo(){

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
