package com.example.biobolt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
    AnimationDrawable imgBio;
    public static final String LOG_TAG=MainActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();

    public static final int S_KEY=88;
    EditText userNameET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameET = findViewById(R.id.editTextUserName);
        passwordET = findViewById(R.id.editTextPassword);

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        ImageView imageView = (ImageView)findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.animate);
        imgBio = (AnimationDrawable) imageView.getBackground();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        imgBio.start();
    }





    public void login(View view) {



        String userName=userNameET.getText().toString();
        String password=passwordET.getText().toString();

        Log.i(LOG_TAG,"Bejelenkezett:"+userName+", jelszó:"+password);

        mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"Sikeres bejelentkezés!");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"Sikertelen bejelentkezés!");
                    Toast.makeText(MainActivity.this,"Bejelemtkezés sikertelen!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void startShopping(){
        Intent intent = new Intent(this,TermekListaActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisztracioActivity.class);
        intent.putExtra("S_KEY",88);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName",userNameET.getText().toString());
        editor.putString("password",passwordET.getText().toString());
        editor.apply();
        Log.i(LOG_TAG,"onPause");
    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"Sikeres bejelentkezés /Anonym/!");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"Sikertelen bejelentkezés!");
                    Toast.makeText(MainActivity.this,"Bejelemtkezés sikertelen!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }


}