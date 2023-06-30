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

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
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
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView postid;
        private TextView posttext;
        private TextView timestamptext;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            postid = itemView.findViewById(R.id.postid);
            posttext = itemView.findViewById(R.id.posttext);
            timestamptext = itemView.findViewById(R.id.timestamp);
        }
        public void bind(DataItem item) {
            postid.setText(item.getText1());
            posttext.setText(item.getText2());
            timestamptext.setText(item.getText3());
        }
    }
}
