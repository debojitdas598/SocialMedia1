package com.example.socialmedia1.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup extends AppCompatActivity {

    ImageView login,signup;
    EditText email,password,confirmpassword, username;
    FirebaseAuth mAuth;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socialmedia1-310bc-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);
        username = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtxt = email.getText().toString().trim();
                String passwordtxt = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailtxt)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(emailtxt)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    String usernametxt = username.getText().toString();
                                    String passwordtxt = password.getText().toString();
                                    String emailtxt = email.getText().toString();
                                    Toast.makeText(Signup.this, "Account Created.", Toast.LENGTH_SHORT).show();
                                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            databaseReference.child("users").child(userId).child("username").setValue(usernametxt);
                                            databaseReference.child("users").child(userId).child("email").setValue(emailtxt);
                                            databaseReference.child("users").child(userId).child("password").setValue(passwordtxt);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    // You can perform any additional actions here, such as navigating to another activity.
                                } else {
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        String errorMessage = exception.getMessage();
                                        Log.e("SignupActivity", "Signup failed", exception);
                                        Toast.makeText(getApplicationContext(), "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        });
            }
        });



    }
}