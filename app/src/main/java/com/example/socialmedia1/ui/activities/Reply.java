package com.example.socialmedia1.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Reply extends AppCompatActivity {
    private TextView postid,posttext,timestamp,likecount;
    private EditText replytext;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        initializer();
        settingInitialValues();
        onClicking();

    }

    private void onClicking() {

        replytext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((event !=null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    replyposter();
                }
                return false;
            }
        });

    }

    private void replyposter() {
        String replyName = replyName();
        Intent intent = getIntent();
        String postid =  intent.getStringExtra("postid");

        Map<String, Object> data = new HashMap<>();
        data.put("reply",replytext.getText().toString());
        data.put("likes",0);
        data.put("time", com.google.firebase.Timestamp.now());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        firestore.collection("dsiblr").document(postid).collection("replies").document(replyName).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser()
                                        .getUid()).child("replies").child(replyName)
                                .setValue(replytext.getText().toString());
                        replytext.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });;
    }

    private String replyName(){
        Random random = new Random();
        String randomName = "Repli"+String.valueOf(random.nextInt(90000) + 10000);
        return randomName;
    }

    private void initializer() {
        firestore = FirebaseFirestore.getInstance();
        postid = findViewById(R.id.postid);
        posttext = findViewById(R.id.posttext);
        timestamp = findViewById(R.id.timestamp);
        likecount = findViewById(R.id.totallikes);
        replytext = findViewById(R.id.replytextbox);
    }

    private void settingInitialValues() {
        Intent intent = getIntent();
        if(intent!=null){
            postid.setText(intent.getStringExtra("postid"));
            posttext.setText(intent.getStringExtra("posttext"));
            timestamp.setText(intent.getStringExtra("timestamp"));
            likecount.setText(intent.getStringExtra("likes"));
        }
        else{
            Toast.makeText(this, "Error in intent passing", Toast.LENGTH_SHORT).show();
        }
    }

}