package tw.com.team13.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import tw.com.team13.firebaselogin.R;
import tw.com.team13.model.Like;
import tw.com.team13.model.Photo;
import tw.com.team13.model.User;

/**
 * @author Chun-Kai Kao on 2018/5/30 上午 10:09
 * @github http://github.com/cckaron
 */

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    
    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference myRef;
    private FirebaseUser user;

    //widgets
    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes;
    private ImageView mBackArrow, mEllipses, mHeartRed, mHeartWhite, mProfileImage;
    //vars
    private Photo mPhoto;
    private int mActivityNumber = 0;
    private String userID;
    private String userOnDoubleTapID;
    private String photoUsername;
    private String photoUrl;
    private GestureDetector mGestureDetector;
    private String username = "";
    private String mLikeString = "";
    private String mProfilePhoto = "";

    private Photo photo;
    private User myuser;
    private Heart mHeart;
    private Like mLike;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mPostImage = view.findViewById(R.id.post_image);
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackLabel = view.findViewById(R.id.tvBackLabel);
        mCaption = view.findViewById(R.id.image_caption);
        mUsername = view.findViewById(R.id.username);
        mTimestamp = view.findViewById(R.id.image_time_posted);
        mEllipses = view.findViewById(R.id.ivEllipses);
        mHeartRed = view.findViewById(R.id.image_heart_red);
        mHeartWhite = view.findViewById(R.id.image_heart);
        mProfileImage = view.findViewById(R.id.profile_photo);
        mLikes = view.findViewById(R.id.image_likes);

        mHeart = new Heart(mHeartWhite, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        mGestureDetector.setOnDoubleTapListener(new GestureListener());

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();
            getPhotoDetails();
            getLikesString();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: " + e.getMessage());
        }

        setupFirebaseAuth();
        setupBottomNavigationView();


        return view;
    }


    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = mFirebaseFirestore
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Photos")
                .document(mPhoto.getPhoto_id())
                .collection("Likes");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    mUsers = new StringBuilder();
                    Log.d(TAG, "start build up username string");
                    for (DocumentSnapshot document : task.getResult()){
                        Log.d(TAG, "the user who likes this photo is " + document.getData());
                        mLike = document.toObject(Like.class);

                        Log.d(TAG, "mLike.getUser_id() is " + mLike.getUser_id());

                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(mLike.getUser_id());

                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()){
                                        Log.d(TAG, "onComplete: DocumentSnapshot data:" + documentSnapshot.getData());
                                        username = documentSnapshot.getString("username");
                                        Log.d(TAG, "onComplete: the name of the person who like the photo is : " + username);
                                        mUsers.append(username);
                                        mUsers.append(",");
                                        Log.d(TAG, "mUsers is " + mUsers.toString());

                                    }else{
                                        Log.d(TAG, "onComplete: No such document");
                                        mLikeString = "";
                                        mLikedByCurrentUser = false;
                                        setupWidgets();
                                    }
                                }else{
                                    Log.d(TAG, "onComplete: get failed with", task.getException());
                                }
                            }
                        });

                    }

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null){
                        userID = user.getUid();
                    }

                    DocumentReference documentReference = mFirebaseFirestore
                            .collection("Users")
                            .document(userID);

                    documentReference.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    username = task.getResult().getString("username");
                                    Log.d(TAG, "onComplete: username is " + username);

                                    String[] splitUsers = mUsers.toString().split(",");

                                    if (username != null){
                                        if (mUsers.toString().contains(username)){
                                            mLikedByCurrentUser = true;
                                        }else{
                                            mLikedByCurrentUser = false;
                                        }
                                    }

                                    int length = splitUsers.length;
                                    if (length == 1){
                                        mLikeString = "按讚的人" + splitUsers[0];
                                    }
                                    else if (length == 2){
                                        mLikeString = "按讚的人" + splitUsers[0]
                                                + "和" + splitUsers[1];
                                    }
                                    else if (length == 3){
                                        mLikeString = "按讚的人" + splitUsers[0]
                                                + "、" + splitUsers[1] + "和" +splitUsers[2];
                                    }
                                    else if (length == 4){
                                        mLikeString = "按讚的人" + splitUsers[0]
                                                + "、" + splitUsers[1] + "、" +splitUsers[2] + "和"
                                                + splitUsers[3];
                                    }
                                    else if (length > 4){
                                        mLikeString = "按讚的人" + splitUsers[0]
                                                + "、" + splitUsers[1] + "、" +splitUsers[2] + "、"
                                                + splitUsers[3] + "和其他" + (splitUsers.length - 3) + "人";
                                    }
                                    setupWidgets();
                                }
                            });

                }
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            mFirebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference reference = mFirebaseFirestore
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Photos")
                    .document(mPhoto.getPhoto_id())
                    .collection("Likes");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()){
                            userOnDoubleTapID = document.getString("user_id");
                            mFirebaseFirestore = FirebaseFirestore.getInstance();

                            //case1: Then user already liked the photo
                            if (mLikedByCurrentUser &&
                                    userOnDoubleTapID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                mFirebaseFirestore.collection("Users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Photos")
                                        .document(mPhoto.getPhoto_id())
                                        .collection("Likes")
                                        .document(userOnDoubleTapID)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "success to delete like in firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

                                mHeart.toggleLike();
                                getLikesString();
                            }

                            //case2: The user has not liked the photo
                            else if (!mLikedByCurrentUser){
                                //add new like
                                addNewLike();
                                break;
                            }

                            if (!document.exists()){
                                addNewLike();
                                //add new like
                            }
                        }
                    }
                }
            });

            return true;
        }
    }

    private void addNewLike(){
        String userAddLikeID = "";
        Log.d(TAG, "instance initializer: adding new like");
        HashMap<String, Object> like = new HashMap<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            userAddLikeID = user.getUid();
            like.put("user_id", userAddLikeID);
        }

        Log.d(TAG, "addNewLike: mPhoto.getUser_id is " + mPhoto.getUser_id());
        Log.d(TAG, "userAddLikeID is " + userAddLikeID);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseFirestore.collection("Users")
                .document(mPhoto.getUser_id())
                .collection("Photos")
                .document(mPhoto.getPhoto_id())
                .collection("Likes")
                .document(userAddLikeID)
                .set(like)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: success to add like to firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: fail to add like to firestore");
                    }
                });

        mHeart.toggleLike();
        getLikesString();
    }

    private void getPhotoDetails(){
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = mFirebaseFirestore
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Photos");
        reference.document(mPhoto.getPhoto_id()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: going to set Photo object");
                            DocumentSnapshot documentSnapshot = task.getResult();
                            photo = documentSnapshot.toObject(Photo.class);
                        }
                    }
                });

        myRef = mFirebaseFirestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: going to set User object");
                myuser = documentSnapshot.toObject(User.class);
                //setupWidgets();
            }
        });
    }

    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();
        if (!timestampDiff.equals("0")){
            mTimestamp.setText(timestampDiff + "天前");
        }else{
            mTimestamp.setText("今天");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            userID = user.getUid();
        }

        DocumentReference documentReference = mFirebaseFirestore
                .collection("Users")
                .document(userID);

        Log.d(TAG, "setupWidgets: userID is" + userID);

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            mProfilePhoto = task.getResult().getString("profile_photo");
                        }
                    }
                });

        if (mProfilePhoto != null){
            UniversalImageLoader.setImage(mProfilePhoto, mProfileImage, null, "");
        }
        mUsername.setText(myuser.getUsername());
        mLikes.setText(mLikeString);

        if (mLikedByCurrentUser){
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }

    }

    /**
     * retrieve the activity number from the incoming bundle from profileActivity interface
     * @return
     */

    private int getActivityNumFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments:" + getArguments());
        
        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei")); // google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        }catch (ParseException e){
            Log.d(TAG, "getTimestampDifference: ParseException:" + e.getMessage());
            difference = "0";
        }
        return difference;
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */

    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments:" + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null){
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return  null;
        }
    }

    /**
     * BottomNavigationView setup
     */

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(), bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }

            /*
    -----------------------------------------Firebase------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up Firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    userID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: signed in "+ userID);

                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
