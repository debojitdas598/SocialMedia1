package com.example.socialmedia1.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia1.R;
import com.example.socialmedia1.models.DataItem;
import com.example.socialmedia1.ui.adapter.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostFragment extends Fragment {

    public PostFragment() {

    }

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<DataItem> dataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(requireContext(),dataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setData(dataList);
        recyclerView.setAdapter(adapter);
        getData();

        return view;
    }
    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference collectionRef = db.collection("dsiblr");

        collectionRef.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DataItem> dataList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String documentId = document.getId().toString();
                    String posttext = document.getString("post text");
                    Timestamp timestamp = document.getTimestamp("time");
                    long likes = (long) document.get("likes");

                    String timeRequired = setDate(timestamp);
                    dataList.add(new DataItem(documentId,posttext,timeRequired,likes));

                    //code to get todays and yesterdays date


                }
                adapter.setData(dataList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getView().getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
            }
        });
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

}