package tw.com.team13.Profile;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import tw.com.team13.Utils.BottomNavigationViewHelper;
import tw.com.team13.Utils.GridImageAdapter;
import tw.com.team13.Utils.UniversalImageLoader;
import tw.com.team13.firebaselogin.R;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class ProfileActivity extends AppCompatActivity{
    private static final String TAG="ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private  static final int NUM_GRID_COLUMNS = 3;
    private Context mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        init();

//        setupBottomNavigationView();
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//
//        tempGridSetup();
    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }
//
//    private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://www.mcdonalds.com/content/dam/usa/documents/mcdelivery/mcdelivery_new11.jpg");
//        imgURLs.add("https://www.goodfood.com.au/content/dam/images/h/0/f/a/q/i/image.related.wideLandscape.940x529.h0fa4n.png/1515456591895.jpg");
//        imgURLs.add("https://cdn.cnn.com/cnnnext/dam/assets/171027052520-processed-foods-overlay-tease.jpg");
//        imgURLs.add("https://imagesvc.timeincapp.com/v3/mm/image?url=https%3A%2F%2Fcdn-image.foodandwine.com%2Fsites%2Fdefault%2Ffiles%2Fstyles%2Fmedium_2x%2Fpublic%2F1519844002%2Ffast-food-mobile-apps-chick-fil-a-FT-BLOG0218.jpg%3Fitok%3D7d_gu0JA&w=700&q=85");
//        imgURLs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1ysvl3tWWeVuBNW4UUCX6TLuzsjSrzBGRRKbRJto-CNxHsbWgRA");
//        imgURLs.add("https://cdn.aarp.net/content/dam/aarp/benefits_discounts/discounts/2015_07/1140-Outback-Bloomin-Onion-Original.web.360.207.jpg");
//
//        setupImageGrid(imgURLs);
//    }
//
//    private void setupImageGrid(ArrayList<String> imgURLs){
//        GridView gridView = findViewById(R.id.gridView);
//
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imgURL = "images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg";
//        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar, "https://");
//    }
//
//    private void setupActivityWidgets(){
//        mProgressBar = findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        profilePhoto = findViewById(R.id.profile_photo);
//    }

    /**
     * Responsible for setting up the profile toolbar
     */

//    private void setupToolbar(){
//        Toolbar toolbar = findViewById(R.id.profileToolBar);
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = findViewById(R.id.profileMenu);
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to account settings.");
//                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    /**
//     *  BottomNavigationView Setup
//     */
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
//
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }

}
