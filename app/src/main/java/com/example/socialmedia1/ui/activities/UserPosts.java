package com.example.socialmedia1.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.ui.adapter.RecyclerViewAdapter;
import com.example.socialmedia1.ui.adapter.UserPostsRVadapter;
import com.example.socialmedia1.ui.fragments.PostFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserPosts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPostsRVadapter adapter;
    private List<DataItem> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        recyclerView = findViewById(R.id.yourpostsrecyclerview);
        adapter = new UserPostsRVadapter(getApplicationContext(),dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        realtimeDBdata();
        adapter.setData(dataList);

        recyclerView.setAdapter(adapter);
    }

    private void getdata(List<String> realtimeDatabase) {
        Log.d("helloji", "getdata: "+realtimeDatabase);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("dsiblr"); // Replace with your actual collection name
        if(!realtimeDatabase.isEmpty()){
            Query query = collectionRef.whereIn("post text",realtimeDatabase);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot!=null){

                        List<DataItem> dataList = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            Toast.makeText(this, "working almost", Toast.LENGTH_SHORT).show();
                            String posttext = documentSnapshot.getString("post text");
                            String postid = documentSnapshot.getId();
                            Timestamp timestamp = documentSnapshot.getTimestamp("time");
                            long likes = (long) documentSnapshot.get("likes");
                            String timeRequired = PostFragment.setDate(timestamp);
                            String imageindicator = (documentSnapshot.getString("image"));
                            dataList.add(new DataItem(postid,posttext,timeRequired,likes,imageindicator));

                        }
                        adapter.setData(dataList);
                    }
                } else{
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "errorddd", Toast.LENGTH_SHORT).show();
        }

    }

    private void realtimeDBdata(){
        List<String> data = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userid = user.getUid();
        Intent intent = new Intent();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String posttext = postSnapshot.getValue().toString();
                    Log.d("TAG", "onDataChange: "+posttext);
                    data.add(posttext);
                }
                getdata(data);
                Log.d("helloji", "onDataChange: "+data);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPosts.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });



    }


}