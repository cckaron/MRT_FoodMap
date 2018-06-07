package tw.com.team13.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.team13.firebaselogin.R;
import tw.com.team13.firebaselogin.SearchActivity;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class CameraFragment extends Fragment {
    private static final String TAG="CameraFragment";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        
        return view;
    }
}
