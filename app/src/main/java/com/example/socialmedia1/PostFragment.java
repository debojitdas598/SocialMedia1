package com.example.socialmedia1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia1.adaptor.DataItem;
import com.example.socialmedia1.adaptor.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<DataItem> dataList = getData();
        adapter.setData(dataList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.notifyItemChanged(dataList.size()-1);
        adapter.notifyItemRangeChanged(0, dataList.size()-1);


        return view;
    }
    private List<DataItem> getData() {
        List<DataItem> dataList = new ArrayList<>();
        // Add your data to the list

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("dsiblr");

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots){
                    String documentId = document.getId().toString();
                    String posttext = document.getString("post text");
                    Timestamp timestamp = document.getTimestamp("time");
                    Instant instant = timestamp.toDate().toInstant();
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    DateTimeFormatter formatterfull = DateTimeFormatter.ofPattern("dd'th' MMM ,yy h:mm a");
                    DateTimeFormatter formatterdate = DateTimeFormatter.ofPattern("dd'th' MMM ,yy");
                    DateTimeFormatter formattertime = DateTimeFormatter.ofPattern("h:mm a");

                    String formattedfullString = dateTime.format(formatterfull); //full date in dd'th' MMM ,yy h:mm a format
                    String formatteddateString = dateTime.format(formatterdate); //date only in dd'th' MMM ,yy format
                    String formattedtimeString = dateTime.format(formattertime); //time only in h:mm a format

                    //code to get todays and yesterdays date
                    LocalDate today = LocalDate.now();
                    LocalDate yesterday = today.minusDays(1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd'th' MMM, yy");
                    String todayStr = today.format(formatterdate);
                    String yesterdayStr = yesterday.format(formatterdate);
                    if(todayStr.equals(formatteddateString)){
                        dataList.add(new DataItem(documentId,posttext,"Today at "+formattedtimeString));
                    }
                    else if(yesterdayStr.equals(formatteddateString)){
                        dataList.add(new DataItem(documentId,posttext,"Yesterday at "+formattedtimeString));
                    }
                    else{
                        dataList.add(new DataItem(documentId,posttext,formattedfullString));
                    }

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getView().getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                    }
                });
        adapter.notifyDataSetChanged();
        adapter.notifyItemChanged(dataList.size()-1);
        return dataList;
    }
}