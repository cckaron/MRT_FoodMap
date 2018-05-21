package tw.com.team13.firebaselogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class addRestaurantActivity extends AppCompatActivity{
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;


    private Button add_button;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantDescription;
    private EditText restaurantNameEdit;
    private EditText restaurantAddressEdit;
    private EditText restaurantDescriptionEdit;

    @OnClick(R.id.chooseImage_button)
    public void onClick() {
        chooseImage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrestaurant);
        addData();
    }

    @BindView(R.id.imageView_restaurant)
    ImageView imageView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void addData(){
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(addRestaurantActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addRestaurantActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }


        final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Access a Cloud FireStore instance

        restaurantNameEdit = findViewById(R.id.storeName_edit);
        restaurantAddressEdit = findViewById(R.id.storeAddress_edit);
        restaurantDescriptionEdit = findViewById(R.id.storeDescription_edit);

        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantName = restaurantNameEdit.getText().toString();
                restaurantAddress = restaurantAddressEdit.getText().toString();
                restaurantDescription = restaurantDescriptionEdit.getText().toString();

                //filter
                if (TextUtils.isEmpty(restaurantName)){
                    return;
                }

                /* add to Cloud FireStore
                  Create a new restaurant with name, address and description */
                Map<String, Object> restaurant = new HashMap<>();
                restaurant.put("Name", restaurantName);
                restaurant.put("Address", restaurantAddress);
                restaurant.put("Description", restaurantDescription);

                // Add a new document with a generated ID
                db.collection("restaurants")
                        .add(restaurant)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(addRestaurantActivity.this, R.string.addStore_Success, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.setClass(addRestaurantActivity.this, MainActivity.class);
                                startActivity(intent);
                        }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addRestaurantActivity.this, R.string.addStore_Fail, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

}
