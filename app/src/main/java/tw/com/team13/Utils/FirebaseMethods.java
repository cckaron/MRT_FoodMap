package tw.com.team13.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import tw.com.team13.firebaselogin.R;
import tw.com.team13.model.StringManipulation;
import tw.com.team13.model.User;

/**
 * @author Chun-Kai Kao on 2018/5/27 14:05
 * @github http://github.com/cckaron
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    private boolean exist = false;

    private String userID;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private Context mContext;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mContext = context;

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public boolean checkIfUsernameExists(String username){
        mFirestore.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()){
                                Log.d(TAG, "onComplete: DocumentID:" + document.getId() + "=>" + document.getData());
                                exist = true;
                            }
                        }else{
                            exist = false;
                        }

                    }
                });

        return exist;

    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */

    public void registerNewEmail (final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete: "+ task.isSuccessful());

                        if (!task.isSuccessful()){
                            Toast.makeText(mContext, R.string.register_failed, Toast.LENGTH_SHORT).show();
                        }
                        else if(task.isSuccessful()){
                            //send verification email
                            sendVerificationEmail();

                        }
                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(mContext, "註冊成功，請至信箱收取認證信", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add a information to Users collection
     * @param email
     * @param username
     * @param userID
     * @param random
     */

    public static void addNewUser(String email, String username, final String userID, boolean random){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference myRef = db.collection("Users").document(userID);

        // if username exists, then append the random ID to username
        if (random){
            String randomID = myRef.getId().substring(3);
            username = username + randomID;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);
        user.put("display_name", StringManipulation.condenseUsername(username));
        user.put("user_id", userID);
        user.put("phone_number", "");
        user.put("description", "");
        user.put("followers", 0);
        user.put("following", 0);
        user.put("posts", 0);
        user.put("profile_photo", "");
        user.put("website", "");

        myRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: DocumentSnapshot successfully written!"); 
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error while writing document", e);
                    }
                });
    }

    /**
     * Retrieves the account content for the user currently logged in
     */
    public static User getUserSettings(final String userID, CollectionReference myRef){

        Log.d(TAG, "getUserAccount: retrieving user account settings from FireStore.");

        final User user = new User();


        myRef.whereEqualTo("user_id", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()){
                                Log.d(TAG, "onSuccess: List empty");
                            } else {
                                for (DocumentSnapshot document : task.getResult()){

                                    try{
                                        user.setDisplay_name(
                                                document.getString("display_name")
                                        );
                                        user.setUsername(
                                                document.getString("username")
                                        );
                                        user.setWebsite(
                                                document.getString("website")
                                        );
                                        user.setDescription(
                                                document.getString("description")
                                        );
                                        user.setProfile_photo(
                                                document.getString("profile_photo")
                                        );
                                        user.setPosts(
                                                Long.valueOf(document.getString("posts"))
                                        );
                                        user.setFollowing(
                                                Long.valueOf(document.getString("following"))
                                        );
                                        user.setFollowers(
                                                Long.valueOf(document.getString("followers"))
                                        );

                                        user.setUsername(
                                                document.getString("username")
                                        );

                                        user.setEmail(
                                                document.getString("email")
                                        );

                                        user.setPhone_number(
                                                document.getString("phone_number")
                                        );

                                        user.setUser_id(
                                                document.getString("user_id")
                                        );

                                        Log.d(TAG, "getUserAccountSettings: retrieved information:" + user.toString());

                                    } catch (NullPointerException e){
                                        Log.e(TAG, "getUserAccountSettings: NullPointerException" + e.getMessage());
                                    }
                                }

                            }
                        }
                    }
                });


        return new User();
    }


}
