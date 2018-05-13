package tw.com.team13.firebaselogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private String account;
    private String password;
    private String name;
    private TextInputLayout accountLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout nameLayout;
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private Button signUpBtn;
    private FirebaseUser user;
    private String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView(){
        auth = FirebaseAuth.getInstance();
        accountEdit = findViewById(R.id.account_edit);
        passwordEdit = findViewById(R.id.password_edit);
        nameEdit = findViewById(R.id.name_edit);
        accountLayout = findViewById(R.id.account_layout);
        passwordLayout = findViewById(R.id.password_layout);
        nameLayout = findViewById(R.id.name_layout);

        accountLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        nameLayout.setErrorEnabled(true);

        signUpBtn = findViewById(R.id.signUp_button);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
                name = nameEdit.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    accountLayout.setError(getString(R.string.emptyAccount));
                    passwordLayout.setError("");
                    nameLayout.setError("");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    accountLayout.setError("");
                    passwordLayout.setError(getString(R.string.emptyPassword));
                    nameLayout.setError("");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    accountLayout.setError("");
                    passwordLayout.setError("");
                    nameLayout.setError(getString(R.string.emptyName));
                    return;
                }
                accountLayout.setError("");
                passwordLayout.setError("");
                nameLayout.setError("");
                auth.createUserWithEmailAndPassword(account, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, R.string.email_hasSent, Toast.LENGTH_SHORT).show();
                                                    } else{
                                                        Toast.makeText(RegisterActivity.this, "發生錯誤，請重新註冊"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    Intent intent = new Intent();
                                    intent.setClass(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}