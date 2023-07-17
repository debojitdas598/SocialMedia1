package com.example.socialmedia1.ui.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.ui.activities.Login;
import com.example.socialmedia1.ui.activities.MainActivity;
import com.example.socialmedia1.ui.activities.UserLikes;
import com.example.socialmedia1.ui.activities.UserPosts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    public UserFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private CardView logout;
    private TextView userid;
    private CardView userPosts;
    private CardView userReplies;
    private CardView userLikes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);

        initializer(view);
        logoutfunction();
        onClickFunction();

        getUsername();
        return view;

    }

    private void onClickFunction() {

        userPosts.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), UserPosts.class);

            startActivity(intent);
        });
  userLikes.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), UserLikes.class);

            startActivity(intent);
        });

    }




    private void getUsername() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String userId = user.getUid();
            DatabaseReference usernameRef = usersRef.child(userId).child("username");
            usernameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    userid.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void logoutfunction() {
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        });


    }

    private void initializer(View view) {
        logout = view.findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        userid = view.findViewById(R.id.userid);
        userLikes = view.findViewById(R.id.yourlikes);
        userPosts = view.findViewById(R.id.yourposts);
        userReplies = view.findViewById(R.id.yourreplies);


    }
}