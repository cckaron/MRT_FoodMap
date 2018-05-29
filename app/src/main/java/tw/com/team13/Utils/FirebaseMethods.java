package tw.com.team13.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import tw.com.team13.firebaselogin.R;
import tw.com.team13.model.Photo;
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
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;
    private CollectionReference colRef;
    private DocumentReference docRef;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    private int imageCount;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public boolean checkIfUsernameExists(String username){
        mFirebaseFirestore.collection("Users")
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


    public int getImageCount(CollectionReference mycolRef){
        imageCount = 0;
        mycolRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document :task.getResult()){
                        imageCount ++;
                    }
                    Log.d(TAG, "onComplete: image count: " + imageCount);
                }
            }
        });
        return imageCount;
    }

    public void uploadNewPhoto(String photoType, final String caption, int count, final String imgUrl){
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo");

        FilePaths filePaths = new FilePaths();

        //case 1) new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getUploadSessionUri();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to firestore
                    addPhotoToDatabase(caption, firebaseUrl.toString(), user_id);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed. ", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");
        }

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url, final String user_id){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        docRef = mFirebaseFirestore.collection("Users")
                .document(user_id)
                .collection("Photos")
                .document();

        String newPhotoKey = docRef.getId();
        String tags = StringManipulation.getTags(caption);

        Map<String, Object> input_photo = new HashMap<>();
        input_photo.put("photo_id", newPhotoKey);
        input_photo.put("date_created", getTimestamp());
        input_photo.put("image_path", url);
        input_photo.put("tags", tags);
        input_photo.put("caption", caption);

        docRef.set(input_photo, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: add data to firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: fail to add data to firestore");
                    }
                });
    }

}
