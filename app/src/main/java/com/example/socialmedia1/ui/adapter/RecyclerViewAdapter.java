package com.example.socialmedia1.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
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

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private DatabaseReference databaseReference;
    private List<DataItem> data;
    public RecyclerViewAdapter(Context context, List<DataItem> data){
        this.context = context;
        this.data = data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcards, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataItem item = data.get(position);
        holder.bind(item);

            likehandler(holder);
            switchtoreply(holder);

    }

    private void switchtoreply(ViewHolder holder) {
        holder.cardView.setOnClickListener(v -> {

            Intent intent = new Intent(context, Reply.class);
            intent.putExtra("postid",holder.postid.getText());
            intent.putExtra("posttext",holder.posttext.getText());
            intent.putExtra("likes",holder.likecount.getText());
            intent.putExtra("timestamp",holder.timestamptext.getText());
            intent.putExtra("likeindicator",holder.likeindicator);

            context.startActivity(intent);
        });
        holder.replybtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Reply.class);
            intent.putExtra("postid",holder.postid.getText());
            intent.putExtra("posttext",holder.posttext.getText());
            intent.putExtra("likes",holder.likecount.getText());
            intent.putExtra("timestamp",holder.timestamptext.getText());
            intent.putExtra("likeindicator",holder.likeindicator);
            context.startActivity(intent);
        });

    }

    private void likehandler(ViewHolder holder) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("dsiblr").document(holder.postid.getText().toString());
        holder.likebtn.setOnClickListener(v -> {if(user!=null){

            if(holder.likeindicator == 1){
                holder.likebtn.setImageResource(R.drawable.replyunlikebutton);
                holder.likecount.setTextColor(Color.parseColor("#000000"));
                holder.likeindicator =0;
            }
            else{
                holder.likebtn.setImageResource(R.drawable.replylikedbtn);
                holder.likecount.setTextColor(Color.parseColor("#D90000"));
                holder.likeindicator = 1;
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
                                long likes = Long.valueOf(holder.likecount.getText().toString())-1;
                                holder.likecount.setText(String.valueOf(likes));


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
                                long likes = Long.valueOf(holder.likecount.getText().toString())+1;
                                holder.likecount.setText(String.valueOf(likes));


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

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView postid;
        private TextView posttext;
        private TextView timestamptext;
        private ImageView likebtn,replybtn;
        private TextView likecount;
        private int likeindicator =0;
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
        }
        public void bind(DataItem item) {
            postid.setText(item.getText1());
            posttext.setText(item.getText2());
            timestamptext.setText(item.getText3());
            likecount.setText(String.valueOf(item.getText4()));
        }
    }
}
