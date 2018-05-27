package tw.com.team13.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tw.com.team13.Home.HomeActivity;
import tw.com.team13.firebaselogin.R;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mLogging;
    private EditText mEmail, mPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = findViewById(R.id.progressbar);
        mLogging = findViewById(R.id.logging);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        mLogging.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if (string.equals("")){
            return true;
        }else{
            return false;
        }
    }

    /*
    -----------------------------------------Firebase------------------------------------------
    */

    private void init(){
        //initialize the button for log in
        btnLogin = findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "帳號或密碼不可為空", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mLogging.setVisibility(View.VISIBLE);
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail onComplete:" + task.isSuccessful());
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (!task.isSuccessful()){
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());

                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mLogging.setVisibility(View.GONE);
                                }
                                else{
                                    try{
                                        if (user.isEmailVerified()){
                                            Log.d(TAG, "onComplete: success. Email is verified.");
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(mContext, "您的帳號尚未認證，請至信箱收取認證信", Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                            mLogging.setVisibility(View.GONE);
                                            mAuth.signOut();
                                        }
                                    }catch (NullPointerException e){
                                        Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                    }
                                }
                            }

                        });
            }
        });

        TextView linkSingUp = findViewById(R.id.link_signup);
        linkSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:  navigating to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        /*
        If the user is logged in, then navigate to HomeActivity and call 'finish()'
         */

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // not going back to the login screen
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up Firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in"+ user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
