package com.example.socialmedia1.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.models.ReplyDataItem;

import java.util.List;

public class RVadapterReply extends RecyclerView.Adapter<RVadapterReply.ViewHolder> {

    private List<ReplyDataItem> itemList;

    public RVadapterReply(Context context, List<ReplyDataItem> itemList) {
        this.itemList = itemList;
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            replyid = itemView.findViewById(R.id.replyid);
            replytext = itemView.findViewById(R.id.replytext);
            timestamp = itemView.findViewById(R.id.timestamp);
            likecount = itemView.findViewById(R.id.totallikes);
        }
    }
}
