package com.example.socialmedia1.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.socialmedia1.PostDialogFragment;
import com.example.socialmedia1.PostFragment;
import com.example.socialmedia1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ImageView usernavbutton;
    private ImageView settingsnavbutton;
    private ImageView button3;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        usernavbutton = findViewById(R.id.feed);
        settingsnavbutton = findViewById(R.id.usersettings);
        FirebaseApp.initializeApp(this);
        fragmentManager = getSupportFragmentManager();
        fragment = new PostFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        usernavbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new PostFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });
        settingsnavbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new UserFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });




        fab.setOnClickListener(
                view -> showEditDialog()
        );
    }



    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PostDialogFragment editNameDialogFragment = PostDialogFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_dialog");
    }
}