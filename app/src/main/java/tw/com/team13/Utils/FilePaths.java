package tw.com.team13.Utils;

import android.os.Environment;

/**
 * @author Chun-Kai Kao on 2018/5/29 下午 11:22
 * @github http://github.com/cckaron
 */

public class FilePaths {

    // "storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
