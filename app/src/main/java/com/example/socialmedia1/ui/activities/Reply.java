package com.example.socialmedia1.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia1.R;

public class Reply extends AppCompatActivity {
    private TextView postid,posttext,timestamp,likecount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        initializer();
        settingInitialValues();
    }

    private void initializer() {
        postid = findViewById(R.id.postid);
        posttext = findViewById(R.id.posttext);
        timestamp = findViewById(R.id.timestamp);
        likecount = findViewById(R.id.totallikes);
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