package com.example.socialmedia1.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.ui.fragments.PostFragment;
import com.google.android.material.card.MaterialCardView;

public class BoardSelector extends AppCompatActivity {
    MaterialCardView japcul,litp,memes,movpopcul,nsfw,vidgames;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_selector);
        japcul = findViewById(R.id.japcul);
        litp = findViewById(R.id.litp);
        memes = findViewById(R.id.memes);
        movpopcul = findViewById(R.id.movpopcul);
        nsfw = findViewById(R.id.nsfw);
        vidgames = findViewById(R.id.vidgames);

        japcul.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("Collection Key","japcul");
            startActivity(intent);
        });

         litp.setOnClickListener(v -> {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("Collection Key","litp");
                    startActivity(intent);
                });

         memes.setOnClickListener(v -> {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("Collection Key","memes");
                    startActivity(intent);
                });

         movpopcul.setOnClickListener(v -> {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("Collection Key","movpopcul");
                    startActivity(intent);
                });

         nsfw.setOnClickListener(v -> {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("Collection Key","nsfw");
                    startActivity(intent);
                });

         vidgames.setOnClickListener(v -> {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("Collection Key","vidgames");
                    startActivity(intent);
                });


            }
}