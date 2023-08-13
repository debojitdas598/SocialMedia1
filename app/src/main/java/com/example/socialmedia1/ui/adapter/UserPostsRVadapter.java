package com.example.socialmedia1.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.models.DataItemUserPosts;
import com.example.socialmedia1.ui.activities.Reply;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class UserPostsRVadapter extends RecyclerView.Adapter<UserPostsRVadapter.ViewHolder>{

    private Context context;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private List<DataItemUserPosts> data;
    String downloadURL;
    String key;
    String keys[] = {"japcul","memes","litp","movpopcul","nsfw","vidgames"};

    @NonNull
    @Override
    public UserPostsRVadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcards, parent, false);
        return new ViewHolder(view);
    }
    public UserPostsRVadapter(Context context, List<DataItemUserPosts> data){
        this.context = context;
        this.data = data;
    }

    public void setData(List<DataItemUserPosts> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostsRVadapter.ViewHolder holder, int position) {
        DataItemUserPosts item = data.get(position);
        holder.bind(item);
        if(holder.imageindicator.equals("1")){
            imageHandler(holder);
        }

        likehandler(holder,keys);
        switchtoreply(holder);
        deleteHandler(holder,position);

    }

    private void deleteHandler(UserPostsRVadapter.ViewHolder holder, int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts").child(holder.key);
        databaseReference.child(holder.postid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.deletepost.setVisibility(View.VISIBLE);
                    holder.postIndicator=1;
                }
                else
                {
                    holder.deletepost.setVisibility(View.GONE);
                    holder.postIndicator=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(holder.key).document(holder.postid.getText().toString());

        holder.deletepost.setOnClickListener(v -> {
            data.remove(position);
            notifyItemRemoved(position);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("posts").child(holder.key);
            databaseReference.child(holder.postid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        snapshot.getRef().removeValue().addOnSuccessListener(unused -> {
                            Toast.makeText(context, "removed from realtime", Toast.LENGTH_SHORT).show();
                            documentReference.delete().addOnSuccessListener(unused1 -> Toast.makeText(context, "removed from firestore", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "couldnt remove from firestore", Toast.LENGTH_SHORT).show());
                        }).addOnFailureListener(e -> Toast.makeText(context, "couldnt remove from realtime", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void likehandler(UserPostsRVadapter.ViewHolder holder,String[] keys) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        for(String key:keys){
            DocumentReference documentReference = firestore.collection(holder.key).document(holder.postid.getText().toString());
            holder.likebtn.setOnClickListener(v -> {if(user!=null){

                if(holder.likeindicator == 1){
                    holder.likebtn.setImageResource(R.drawable.unlikebutton);
                    holder.likecount.setTextColor(Color.parseColor("#000000"));
                    holder.likeindicator = 0;
                    long likes = Long.valueOf(holder.likecount.getText().toString())-1;
                    holder.likecount.setText(String.valueOf(likes));
                }
                else{
                    holder.likebtn.setImageResource(R.drawable.likedbutton);
                    holder.likecount.setTextColor(Color.parseColor("#D90000"));
                    holder.likeindicator = 1;
                    long likes = Long.valueOf(holder.likecount.getText().toString())+1;
                    holder.likecount.setText(String.valueOf(likes));

                    holder.heartanim.setVisibility(View.VISIBLE);
                    long delayInMillis = 700; // Delay in ms
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.heartanim.setVisibility(View.INVISIBLE);
                        }
                    }, delayInMillis);
                }

                String userid = user.getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("likes");
                databaseReference.child(holder.postid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //remove from likes if already liked
                        if(snapshot.exists()){
                            snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "removed from likes", Toast.LENGTH_SHORT).show();


                                    documentReference.update("likes", FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "No error", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Some error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "couldnt remove from likes", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            databaseReference.child(holder.postid.getText().toString()).setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                //adding to likes if not liked
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "added to likes", Toast.LENGTH_SHORT).show();

                                    documentReference.update("likes", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "No error", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Some error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "couldnt add to likes", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
            else {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }});


            //sets liked posts as liked
            String userid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("likes");
            databaseReference.child(holder.postid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        holder.likebtn.setImageResource(R.drawable.likedbutton);
                        holder.likecount.setTextColor(Color.parseColor("#D90000"));
                        holder.likeindicator=1;
                    }
                    else
                    {
                        holder.likebtn.setImageResource(R.drawable.unlikebutton);
                        holder.likecount.setTextColor(Color.parseColor("#000000"));
                        holder.likeindicator = 0;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    private void switchtoreply(UserPostsRVadapter.ViewHolder holder) {
        holder.cardView.setOnClickListener(v -> {

            Intent intent = new Intent(context, Reply.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postid",holder.postid.getText());
            intent.putExtra("posttext",holder.posttext.getText());
            intent.putExtra("likes",holder.likecount.getText());
            intent.putExtra("timestamp",holder.timestamptext.getText());
            intent.putExtra("likeindicator",holder.likeindicator);
            intent.putExtra("imageindicator",holder.imageindicator);
            intent.putExtra("key",holder.key);
            context.startActivity(intent);
        });
        holder.replybtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Reply.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postid",holder.postid.getText());
            intent.putExtra("posttext",holder.posttext.getText());
            intent.putExtra("likes",holder.likecount.getText());
            intent.putExtra("timestamp",holder.timestamptext.getText());
            intent.putExtra("likeindicator",holder.likeindicator);
            intent.putExtra("imageindicator",holder.imageindicator);
            intent.putExtra("key",holder.key);
            context.startActivity(intent);
        });

    }

    private void imageHandler(UserPostsRVadapter.ViewHolder holder) {
        holder.postimage.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference fileRef = storageReference.child("posted_images").child(holder.postid.getText().toString());
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadURL = uri.toString(); //URL required to fetch the image file
            Log.d("tag1212", "imageHandler: "+downloadURL);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadURL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(context, "Error while downloading the image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        InputStream inputStream = response.body().byteStream();
                        Log.d("TAG1212", "onResponse: "+inputStream);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.postimage.setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).post(() -> holder.linearLayoutCompat.setVisibility(View.INVISIBLE));
                        new Handler(Looper.getMainLooper()).post(() -> holder.postimage.setImageBitmap(bitmap));



                    }
                    else {
                        Toast.makeText(context, "Error while parsing inputstream.", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            downloadURL = null;


        }).addOnFailureListener(e -> {
            if(e instanceof StorageException){
                StorageException storageException = (StorageException) e;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postid;
        private TextView posttext;
        private TextView timestamptext;
        private ImageView likebtn,replybtn,postimage,deletepost;
        private LinearLayoutCompat linearLayoutCompat;
        private GifImageView heartanim;
        private TextView likecount;
        private int likeindicator =0;
        private String imageindicator = "0";
        private String key = "null";

        private int postIndicator = 0;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            postid = itemView.findViewById(R.id.postid);
            posttext = itemView.findViewById(R.id.posttext);
            timestamptext = itemView.findViewById(R.id.timestamp);
            likebtn = itemView.findViewById(R.id.likebtn);
            replybtn = itemView.findViewById(R.id.replybtn);
            likecount = itemView.findViewById(R.id.totallikes);
            cardView = itemView.findViewById(R.id.cv);
            heartanim = itemView.findViewById(R.id.heartanimation);
            postimage = itemView.findViewById(R.id.postimage);
            deletepost = itemView.findViewById(R.id.delete);
            linearLayoutCompat = itemView.findViewById(R.id.loadingimagelayout);
        }
        public void bind(DataItemUserPosts item) {
            postid.setText(item.getText1());
            posttext.setText(item.getText2());
            timestamptext.setText(item.getText3());
            likecount.setText(String.valueOf(item.getText4()));
            imageindicator = String.valueOf(item.getText5());
            key = String.valueOf(item.getText6());
            if(String.valueOf(item.getText5()).equals("0")){
                postimage.setVisibility(View.GONE);
                linearLayoutCompat.setVisibility(View.GONE);
            }
        }
    }
}
