package tw.com.team13.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class Fragment_Home extends Fragment {

    @OnClick(R.id.searching)
    public void onClick(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }



    @Override
    public void onStart() {
        TextView textView = getView().findViewById(R.id.textOne);
        textView.setText("探索美食");
        super.onStart();
    }
}
