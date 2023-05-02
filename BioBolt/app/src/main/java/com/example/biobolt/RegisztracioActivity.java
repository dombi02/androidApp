package com.example.biobolt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisztracioActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String LOG_TAG=RegisztracioActivity.class.getName();
    private static final String PREF_KEY=RegisztracioActivity.class.getPackage().toString();
    private static final int S_KEY=88;
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText telEditText;
    Spinner spinner;
    EditText addressEditText;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisztracio);

        int s_key = getIntent().getIntExtra("S_KEY",2);

        if(s_key!=88){
            finish();
        }
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        telEditText = findViewById(R.id.telEditText);
        spinner = findViewById(R.id.telSpinner);
        addressEditText = findViewById(R.id.addressEditText);

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String userName = preferences.getString("userName","");
        String password = preferences.getString("password","");

        userEmailEditText.setText(userName);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tel_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mAuth=FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String userName=userNameEditText.getText().toString();
        String email=userEmailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        String passwordagain=passwordAgainEditText.getText().toString();

        if(!password.equals(passwordagain)){
            Log.e(LOG_TAG,"Nem egyezik jelszó és a megerősítése!");
            return;
        }

        String telNumber = telEditText.getText().toString();
        String telType = spinner.getSelectedItem().toString();
        String address = addressEditText.getText().toString();
        Log.i(LOG_TAG,"Regisztrált:"+userName+", email:"+email);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"Felhasználó létrehozása sikeres!");
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"Nem sikerült létrehozni a felhasználót!");
                    Toast.makeText(RegisztracioActivity.this,"Nem sikerült: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void cancel(View view) {
        finish();
    }

    private void startShopping(){
        Intent intent = new Intent(this,TermekListaActivity.class);
        // intent.putExtra("S_KEY", S_KEY);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG,selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO
    }
}