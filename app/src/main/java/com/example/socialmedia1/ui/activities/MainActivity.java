package com.example.socialmedia1.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.socialmedia1.ui.fragments.PostDialogFragment;
import com.example.socialmedia1.ui.fragments.PostFragment;
import com.example.socialmedia1.R;
import com.example.socialmedia1.ui.fragments.UserFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private ImageView usernavbutton;
    private ImageView settingsnavbutton;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        usernavbutton = findViewById(R.id.feed);
        settingsnavbutton = findViewById(R.id.usersettings);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        FirebaseApp.initializeApp(this);

        setUpFragment(new PostFragment());

        usernavbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpFragment(new PostFragment());
            }
        });

        settingsnavbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpFragment(new UserFragment());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void setUpFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitNow();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PostDialogFragment editNameDialogFragment = PostDialogFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_dialog");
    }

    public void finishActivity() {
        finish();
    }
}