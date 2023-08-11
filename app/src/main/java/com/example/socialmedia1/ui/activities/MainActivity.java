package com.example.socialmedia1.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.socialmedia1.ui.fragments.PostDialogFragment;
import com.example.socialmedia1.ui.fragments.PostFragment;
import com.example.socialmedia1.R;
import com.example.socialmedia1.ui.fragments.UserFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageView usernavbutton;
    private ImageView settingsnavbutton;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fab;
    String key ;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        key = getIntent().getStringExtra("Collection Key");
        if(key == null){
            sharedPreferences = getSharedPreferences("BoardSelected", Context.MODE_PRIVATE);
            PostFragment postFragment = new PostFragment();
            key  = postFragment.return_previous_board_selection(sharedPreferences);
        }

        Log.d("11", "onCreate: "+key);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }

        fab = findViewById(R.id.fab);
        usernavbutton = findViewById(R.id.feed);
        settingsnavbutton = findViewById(R.id.usersettings);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        FirebaseApp.initializeApp(this);

        setUpFragment(new PostFragment());
        usernavbutton.setOnClickListener(v -> setUpFragment(new PostFragment()));

        settingsnavbutton.setOnClickListener(v -> setUpFragment(new UserFragment()));

        fab.setOnClickListener(v -> showEditDialog());

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
    public String getMyData() {
        String key1 = key;
        return key1;
    }
    public void finishActivity() {
        finish();
    }
}