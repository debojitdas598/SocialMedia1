package com.example.socialmedia1.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.models.ReplyDataItem;
import com.example.socialmedia1.ui.adapter.RVadapterReply;
import com.example.socialmedia1.ui.adapter.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Reply extends AppCompatActivity {
    private TextView posttext,timestamp,likecount;
    public TextView postid;
    private EditText replytext;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RVadapterReply adapter;
    private ImageView postlikebutton,postimage;
    int likeindicator;
    String key;
    String postIDtext, postImageIndicator;
    private List<ReplyDataItem> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        initializer();
        settingInitialValues();
        onClicking();
        getData();
        likehandler();

    }

    private void likehandler(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(key).document(postIDtext);
        postlikebutton.setOnClickListener(v -> {
            if(likeindicator == 1){
                postlikebutton.setImageResource(R.drawable.unlikebutton);
                likecount.setTextColor(Color.parseColor("#000000"));
                likeindicator = 0;
                long likes = Long.valueOf(likecount.getText().toString())-1;
                likecount.setText(String.valueOf(likes));
            }
            else{
                postlikebutton.setImageResource(R.drawable.likedbutton);
                likecount.setTextColor(Color.parseColor("#D90000"));
                likeindicator = 1;
                long likes = Long.valueOf(likecount.getText().toString())+1;
                likecount.setText(String.valueOf(likes));
            }
            String userid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("likes");
            databaseReference.child(postIDtext).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        snapshot.getRef().removeValue().addOnSuccessListener(unused -> {
                            documentReference.update("likes", FieldValue.increment(-1)).addOnSuccessListener(unused1 -> {

                            }).addOnFailureListener(e -> {

                            });
                        }).addOnFailureListener(e -> {

                        });
                    }
                    else {
                        databaseReference.child(postIDtext).setValue(postIDtext).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                documentReference.update("likes",FieldValue.increment(1)).addOnSuccessListener(unused12 -> {

                                }).addOnFailureListener(e -> {});
                            }
                        }).addOnFailureListener(e -> {
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void onClicking() {

        replytext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((event !=null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    replyposter();
                }
                return false;
            }
        });

    }

    private void replyposter() {
        String replyName = replyName();
        Intent intent = getIntent();
        String postid =  intent.getStringExtra("postid");
        if(!replytext.getText().toString().isEmpty()){
            Map<String, Object> data = new HashMap<>();
            data.put("reply",replytext.getText().toString());
            data.put("likes",0);
            data.put("time", com.google.firebase.Timestamp.now());

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

            firestore.collection("dsiblr").document(postid).collection("replies").document(replyName).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser()
                                            .getUid()).child("replies").child(replyName)
                                    .setValue(replytext.getText().toString());
                            replytext.setText("");
                            Toast.makeText(Reply.this, "Reply posted!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });;
        }


    }


    private String replyName(){
        Random random = new Random();
        String randomName = "Repli"+String.valueOf(random.nextInt(90000) + 10000);
        return randomName;
    }

    private void initializer() {
        firestore = FirebaseFirestore.getInstance();
        postid = findViewById(R.id.postid);
        posttext = findViewById(R.id.posttext);
        timestamp = findViewById(R.id.timestamp);
        likecount = findViewById(R.id.totallikes);
        replytext = findViewById(R.id.replytextbox);
        recyclerView = findViewById(R.id.replyRV);
        postlikebutton = findViewById(R.id.likebtnreply);
        postimage = findViewById(R.id.postimage);
        Intent intent = getIntent();
        postIDtext =  intent.getStringExtra("postid");
        postImageIndicator = intent.getStringExtra("imageindicator");
        key = intent.getStringExtra("key");
        Log.d("imageurl", "initializer: "+postImageIndicator);
        if(postImageIndicator.equals("1")){
            imageHandler();
        }
        else{
            postimage.setVisibility(View.GONE);
        }
        adapter = new RVadapterReply(getApplicationContext(),dataList,postIDtext);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void imageHandler(){

        postimage.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference fileRef = storageReference.child("posted_images").child(postIDtext);
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadURL = uri.toString(); //URL required to fetch the image file
            Log.d("tag1212", "imageHandler: "+downloadURL);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadURL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(getApplicationContext(), "Error while downloading the image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        InputStream inputStream = response.body().byteStream();
                        Log.d("TAG1212", "onResponse: "+inputStream);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        postimage.setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).post(() -> postimage.setImageBitmap(bitmap));

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error while parsing inputstream.", Toast.LENGTH_SHORT).show();

                    }
                }
            });



        }).addOnFailureListener(e -> {
            if(e instanceof StorageException){
                StorageException storageException = (StorageException) e;
            }
        });

    }

    private void getData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String postid =  intent.getStringExtra("postid");
        CollectionReference collectionRef = db.collection("dsiblr").document(postid).collection("replies");

        collectionRef.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<ReplyDataItem> dataList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String replyId = document.getId().toString();
                    String replytext = document.getString("reply");
                    Timestamp timestamp = document.getTimestamp("time");
                    long likes = (long) document.get("likes");
                    String timeRequired = setDate(timestamp);
                    Log.d("TAG", "onSuccess: "+replytext);
                    dataList.add(new ReplyDataItem(replyId,replytext,timeRequired,likes));
                }
                adapter.setData(dataList);
                recyclerView.setAdapter(adapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }


    private void settingInitialValues() {
        Intent intent = getIntent();
        if(intent!=null){
            postid.setText(intent.getStringExtra("postid"));
            posttext.setText(intent.getStringExtra("posttext"));
            timestamp.setText(intent.getStringExtra("timestamp"));
            likecount.setText(intent.getStringExtra("likes"));
            likeindicator = intent.getIntExtra("likeindicator",0);
            if(likeindicator ==1){
                postlikebutton.setImageResource(R.drawable.likedbutton);
                likecount.setTextColor(Color.parseColor("#D90000"));
            }
        }
        else{
            Toast.makeText(this, "Error in intent passing", Toast.LENGTH_SHORT).show();
        }
    }

    private String setDate(Timestamp timestamp){
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

    public String postIdreturner(){
        Intent intent = getIntent();
        return intent.getStringExtra("postid");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}