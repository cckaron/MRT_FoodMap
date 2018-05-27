package tw.com.team13.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tw.com.team13.Login.LoginActivity;
import tw.com.team13.Login.RegisterActivity;

/**
 * @author Chun-Kai Kao on 2018/5/26 01:34
 * @github http://github.com/cckaron
 */

public class HomeActivity extends AppCompatActivity{  //繼承 AppCompatActivity
    private FirebaseAuth auth;  // Auth 認證用
    FirebaseAuth.AuthStateListener authListener;  // authListener 監聽認證狀態
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState){  // 一開始會呼叫onCreate方法
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();  // 取得Firebase
        FirebaseUser user = auth.getCurrentUser();  //取得Firebase帳號使用者 (getInstance內的方法)
        if (user == null){ //使用者未登入
            setContentView(R.layout.activity_home); // 載入HomeActivity(當前)
            login = findViewById(R.id.button_1);
            register = findViewById(R.id.button_2);

            login.setOnClickListener(new View.OnClickListener() { //按下"登入"的動作
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, LoginActivity.class); //將當前Activity替換成LoginActivity
                    startActivity(intent);
                }
            });

            register.setOnClickListener(new View.OnClickListener() {  //按下"註冊"的動作
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, RegisterActivity.class); //將當前Activity替換成RegisterActivity
                    startActivity(intent);
                }
            });

        }else{
            Intent intent = new Intent();
            intent.setClass(HomeActivity.this, MainActivity.class); // 將當前Activity替換成MainActivity
            startActivity(intent); // start
        }
    }

}
