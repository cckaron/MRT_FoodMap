package tw.com.team13.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout accoutLayout;
    private TextInputLayout passwordLayout;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        accountEdit = findViewById(R.id.account_edit);
        passwordEdit = findViewById(R.id.password_edit);
        accoutLayout = findViewById(R.id.account_layout);
        passwordLayout = findViewById(R.id.password_layout);
        passwordLayout.setErrorEnabled(true);
        accoutLayout.setErrorEnabled(true);
        loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(TextUtils.isEmpty(account)){
                    accoutLayout.setError(getString(R.string.emptyAccount));
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordLayout.setError(getString(R.string.emptyPassword));
                    return;
                }
                accoutLayout.setError("");
                passwordLayout.setError("");
                mAuth.signInWithEmailAndPassword(account, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}