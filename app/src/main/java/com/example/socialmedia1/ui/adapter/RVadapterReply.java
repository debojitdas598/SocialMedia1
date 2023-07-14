package com.example.socialmedia1.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.models.ReplyDataItem;
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

public class RVadapterReply extends RecyclerView.Adapter<RVadapterReply.ViewHolder> {
    private DatabaseReference databaseReference;
    private List<ReplyDataItem> itemList;
    private Context context;
    private String postid;

    public RVadapterReply(Context context, List<ReplyDataItem> itemList,String postid) {
        this.itemList = itemList;
        this.context = context;
        this.postid = postid;
    }
    public void setData(List<ReplyDataItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RVadapterReply.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReplyDataItem item = itemList.get(position);

        holder.replyid.setText(item.getText1());
        holder.replytext.setText(item.getText2());
        holder.timestamp.setText(item.getText3());
        holder.likecount.setText(String.valueOf(item.getText4()));

        replyLikeHandler(holder);
    }

    private void replyLikeHandler(ViewHolder holder) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Reply reply = new Reply();

        DocumentReference documentReference = firestore.collection("dsiblr").document(postid)
                .collection("replies").document(holder.replyid.getText().toString());

        holder.likebtn.setOnClickListener(v -> {
            if(user!=null){
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
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("likedreplies");
                databaseReference.child(holder.replyid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                                }
                            });
                        }
                        else {
                            databaseReference.child(holder.replyid.getText().toString()).setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(context, "added to likes", Toast.LENGTH_SHORT).show();
                                    long likes = Long.valueOf(holder.likecount.getText().toString())+1;
                                    holder.likecount.setText(String.valueOf(likes));
                                    documentReference.update("likedreplies", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            else{
                Toast.makeText(reply.getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView replyid;
        TextView replytext;
        TextView timestamp;
        TextView likecount;
        ImageView likebtn;
        int likeindicator=0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            replyid = itemView.findViewById(R.id.replyid);
            replytext = itemView.findViewById(R.id.replytext);
            timestamp = itemView.findViewById(R.id.timestamp);
            likecount = itemView.findViewById(R.id.totallikes);
            likebtn = itemView.findViewById(R.id.likebtn);
        }
    }
}
