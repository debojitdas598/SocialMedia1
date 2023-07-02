package com.example.socialmedia1.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.google.android.material.card.MaterialCardView;

public class BoardSelector extends AppCompatActivity {
    MaterialCardView dsicardview,bmscecardview;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_selector);
        dsicardview = findViewById(R.id.dsiblrcard);
        bmscecardview = findViewById(R.id.bmscecard);

        dsicardview.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("Collection Key","dsiblr");
            startActivity(intent);
        });
        bmscecardview.setOnClickListener(v -> {
            Toast.makeText(this, "Under Development!", Toast.LENGTH_SHORT).show();
        });

    }
}