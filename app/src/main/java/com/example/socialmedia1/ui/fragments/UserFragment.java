package com.example.socialmedia1.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmedia1.R;
import com.example.socialmedia1.ui.activities.Login;
import com.example.socialmedia1.ui.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {
    public UserFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private CardView logout;
    private TextView userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);

        initializer(view);
        logout.setOnClickListener(v -> {
            logoutfunction();
        });
        getUsername();
        return view;

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
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();

    }

    private void initializer(View view) {
        logout = view.findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        userid = view.findViewById(R.id.userid);


    }
}