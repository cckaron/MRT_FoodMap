package tw.com.team13.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Chun-Kai Kao on 2018/5/29 下午 11:24
 * @github http://github.com/cckaron
 */

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */

    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i=0; i < listfiles.length; i++){
            if (listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */


    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i=0; i < listfiles.length; i++){
            if(listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
