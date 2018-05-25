package tw.com.team13.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class Fragment_Member extends Fragment {

    private Button addStoreBtn;
    private Button logoutBtn;
    private String name;
    private String userID;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        TextView textView = getView().findViewById(R.id.textView);
        if (user != null && user.isEmailVerified()){
            name = user.getEmail();
            textView.setText(name+" (已開通)");
        }else {
            textView.setText("您的帳號尚未認證!");
        }

        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView(){
        logoutBtn = getView().findViewById(R.id.button8);
        addStoreBtn = getView().findViewById(R.id.button4);
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent();
                intent.setClass(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });
        addStoreBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UploadActivity.class);
                startActivity(intent);
            }
        });
    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState){
//        super.onActivityCreated(savedInstanceState);
//        addStore = getView().findViewById(R.id.addStore_button);
//        addStore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), addStoreActivity.class); //用getActivity()尋找當前Activity,前往addStoreActivity
//                startActivity(intent);
//            }
//        });
//    }
}
