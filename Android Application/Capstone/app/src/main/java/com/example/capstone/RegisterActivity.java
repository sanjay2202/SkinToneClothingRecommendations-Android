package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Widgets
    EditText usernameET, emailET, passwordET, ageET;
    RadioGroup radioGroupGender;
    Button registerBTN;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing Widgets
        usernameET = findViewById(R.id.username);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        ageET = findViewById(R.id.age);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        registerBTN = findViewById(R.id.register);

        // Firebase auth
        auth = FirebaseAuth.getInstance();

        //Adding Event listener to the button register
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
                String username_text = usernameET.getText().toString();
                String email_text = emailET.getText().toString();
                String password_text = passwordET.getText().toString();
                String age_txt = ageET.getText().toString();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String gender_txt = selectedRadioButton.getText().toString();

                if (TextUtils.isEmpty(username_text)|| TextUtils.isEmpty(email_text)|| TextUtils.isEmpty(password_text)|| TextUtils.isEmpty(age_txt)|| TextUtils.isEmpty(gender_txt)){
                    Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    RegisterNow(username_text,email_text,age_txt,gender_txt,password_text);
                }
            }
        });

    }

    private void RegisterNow(final String username, String email, String age, String gender, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance()
                                    .getReference("MyUsers")
                                    .child(userid);

                            //HashMaps
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username", username);
                            hashMap.put("age", age);
                            hashMap.put("gender",gender);
                            hashMap.put("imageURL","default");
                            hashMap.put("status", "Offline");

                            //Opening the main activity after successful Registration
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent i =new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}