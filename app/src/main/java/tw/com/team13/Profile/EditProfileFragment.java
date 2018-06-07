package tw.com.team13.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.CrashUtils;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.com.team13.Share.ShareActivity;
import tw.com.team13.Utils.UniversalImageLoader;
import tw.com.team13.firebaselogin.R;
import tw.com.team13.model.User;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class EditProfileFragment extends Fragment{

    private static final String TAG = "EditProfileFragment";

    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    private String userID;
    private User myuser;

    private boolean exist;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber = view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);


        //FireStore
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

//        setProfileImage();

        setupFirebaseAuth();
        
        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSetting();
            }
        });

        return view;
    }


    /**
     * Retrieve the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSetting(){

        final String displayname = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final String phoneNumber = mPhoneNumber.getText().toString();

        //case 1: if the user made a change to their username
        if (!myuser.getUsername().equals(username)){
            checkIfUsernameExists(username);
        }

        //case 2: if the user made a change to their email
        if (!myuser.getEmail().equals(email)){
            // step1) Reauthenticate
            // Confirm the password and email
        }

        if (!exist){
            Map<String, Object> user = new HashMap<>();
            user.put("display_name", displayname);
            user.put("username", username);
            user.put("website", website);
            user.put("description", description);
            user.put("email", email);
            user.put("phone_number", phoneNumber);

            myRef = mFirebaseFirestore.collection("Users").document(userID);

            myRef.set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "username has been used", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkIfUsernameExists(String username){
        Log.d(TAG, "checkIfUsernameExists: check if " + username + "exists");

        CollectionReference usersRef = mFirebaseFirestore.collection("Users");
        usersRef.whereEqualTo("username", username).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()){
                                if (document.exists()){
                                    exist = true;
                                } else{
                                    exist = false;
                                }
                            }
                        }
                    }
                });
        return exist;
    }

    private void setProfileWidgets (User user){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firestore: " + user.toString());

        UniversalImageLoader.setImage(user.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(user.getDisplay_name());
        mUsername.setText(user.getUsername());
        mWebsite.setText(user.getWebsite());
        mDescription.setText(user.getDescription());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(user.getPhone_number());

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

    }

             /*
    -----------------------------------------Firebase------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up Firebase auth.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    userID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: signed in "+ userID);
                    myRef = mFirebaseFirestore.collection("Users").document(userID);
                    myRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            myuser = documentSnapshot.toObject(User.class);
                            Log.d(TAG, "The user is " + myuser);
                            setProfileWidgets(myuser);
                        }
                    });

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
