package tw.com.team13.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addStoreActivity extends AppCompatActivity{
    private Button add_button;
    private String storeName;
    private String storeAddress;
    private String storeDescription;
    private EditText storeNameEdit;
    private EditText storeAddressEdit;
    private EditText storeDescriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstore);
        addData();
    }

    private void addData(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Access a Cloud Firestore instance

        storeNameEdit = findViewById(R.id.storeName_edit);
        storeAddressEdit = findViewById(R.id.storeAddress_edit);
        storeDescriptionEdit = findViewById(R.id.storeDescription_edit);

        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeName = storeNameEdit.getText().toString();
                storeAddress = storeAddressEdit.getText().toString();
                storeDescription = storeDescriptionEdit.getText().toString();

                //filter
                if (TextUtils.isEmpty(storeName)){
                    return;
                }

                /* add to Cloud Firestore
                  Create a new store with name, address and description */
                Map<String, Object> store = new HashMap<>();
                store.put("Name", storeName);
                store.put("Address", storeAddress);
                store.put("Description", storeDescription);

                // Add a new document with a generated ID
                db.collection("stores")
                        .add(store)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(addStoreActivity.this, R.string.addStore_Success, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.setClass(addStoreActivity.this, MainActivity.class);
                                startActivity(intent);
                        }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addStoreActivity.this, R.string.addStore_Fail, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

}
