package com.example.socialmedia1.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.models.DataItemUserPosts;
import com.example.socialmedia1.ui.adapter.RecyclerViewAdapter;
import com.example.socialmedia1.ui.adapter.UserPostsRVadapter;
import com.example.socialmedia1.ui.fragments.PostFragment;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserPosts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserPostsRVadapter adapter;

    private List<DataItemUserPosts> dataList;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> data;
    String key;
    String[] keys = {"japcul","memes","nsfw","vidgames","movpopcul","litp"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        recyclerView = findViewById(R.id.yourpostsrecyclerview);
        adapter = new UserPostsRVadapter(getApplicationContext(),dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        swipeRefreshLayout = findViewById(R.id.swiperefreshuserposts);

        realtimeDBdata();
        adapter.setData(dataList);

        recyclerView.setAdapter(adapter);
        refreshScreen();
    }

    private void refreshScreen() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            realtimeDBdata();
            getdata(data,keys);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void getdata(List<String> realtimeDatabase, String[] keys) {
        Log.d("helloji", "getdata: "+realtimeDatabase);
        List<DataItemUserPosts> dataList = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        for(String key : keys){
            CollectionReference collectionRef = firestore.collection(key); // Replace with your actual collection name
            if(!realtimeDatabase.isEmpty()){
                for(String postidtext : realtimeDatabase){
                    firestore.collection(key).document(postidtext).get().addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            if(!documentSnapshot.getId().equals("nullplaceholder")){
                                String documentId = documentSnapshot.getId().toString();
                                String posttext = documentSnapshot.getString("post text");
                                Timestamp timestamp = documentSnapshot.getTimestamp("time");
                                String  imageindicator =(documentSnapshot.getString("image"));
                                long likes = (long) documentSnapshot.get("likes");
                                String timeRequired = setDate(timestamp);

                                dataList.add(new DataItemUserPosts(documentId,posttext,timeRequired,likes,imageindicator,key));

                            }
                            adapter.setData(dataList);

                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }

                    });

                }
            }
            else {
                Toast.makeText(this, "errorddd", Toast.LENGTH_SHORT).show();
            }
        }


    }
    public static String setDate(Timestamp timestamp){
        Instant instant = timestamp.toDate().toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatterfull = DateTimeFormatter.ofPattern("dd'th' MMM ,yy h:mm a");
        DateTimeFormatter formatterdate = DateTimeFormatter.ofPattern("dd'th' MMM ,yy");
        DateTimeFormatter formattertime = DateTimeFormatter.ofPattern("h:mm a");

        String formattedfullString = dateTime.format(formatterfull);
        String formatteddateString = dateTime.format(formatterdate);
        String formattedtimeString = dateTime.format(formattertime);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd'th' MMM, yy");
        String todayStr = today.format(formatterdate);
        String yesterdayStr = yesterday.format(formatterdate);
        if(todayStr.equals(formatteddateString)){
            return "Today at " + formattedtimeString;
        }
        else if(yesterdayStr.equals(formatteddateString)){
            return "Yesterday at "+formattedtimeString;
        }
        else{
            return formattedfullString;
        }
    }
    private void realtimeDBdata(){
        data = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userid = user.getUid();
        Intent intent = new Intent();
for(String key:keys){
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts").child(key);
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                String posttext = postSnapshot.getValue().toString();
                String postid = postSnapshot.getKey();
                Log.d("TAG", "onDataChange: "+posttext);
                data.add(postid);
            }
            getdata(data, keys);
            Log.d("helloji", "onDataChange: "+data);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(UserPosts.this, "Error", Toast.LENGTH_SHORT).show();
        }
    });
}




    }


}