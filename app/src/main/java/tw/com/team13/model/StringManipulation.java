package tw.com.team13.model;

/**
 * @author Chun-Kai Kao on 2018/5/27 14:45
 * @github http://github.com/cckaron
 */

public class StringManipulation {


    public static String expandUsername(String username){
        return username.replace("."," ");
    }

    public static String condenseUsername(String username){
        return username.replace(" ", ".");
    }
}
