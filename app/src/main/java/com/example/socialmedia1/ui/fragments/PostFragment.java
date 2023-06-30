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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DataItem> dataList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String documentId = document.getId().toString();
                    dataList.add(new DataItem(documentId, "11"));
                    System.out.println(dataList.toString() + " here");
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

}