package com.example.socialmedia1.ui.activities;

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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private ImageView login, signup;
    private EditText email, password, confirmPassword, username;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);
        confirmPassword = findViewById(R.id.confirmPassword);
        username = findViewById(R.id.username);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString().trim();
                String passwordTxt = password.getText().toString().trim();
                String confirmPasswordTxt = confirmPassword.getText().toString().trim();
                String usernameTxt = username.getText().toString().trim();

                if (TextUtils.isEmpty(emailTxt)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordTxt)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordTxt.equals(confirmPasswordTxt)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Save user details to the database
                                    databaseReference.child(userId).child("username").setValue(usernameTxt);
                                    databaseReference.child(userId).child("email").setValue(emailTxt);
                                    databaseReference.child(userId).child("password").setValue(passwordTxt);

                                    Toast.makeText(Signup.this, "Account Created.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signup.this, Login.class));
                                    finish();
                                } else {
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        String errorMessage = exception.getMessage();
                                        Log.e("SignupActivity", "Signup failed", exception);
                                        Toast.makeText(getApplicationContext(), "Signup failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}
