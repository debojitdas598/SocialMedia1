package com.example.socialmedia1.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.socialmedia1.R;
import com.example.socialmedia1.utils.VibrationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PostDialogFragment extends DialogFragment {
    private EditText mEditText;

    public static PostDialogFragment newInstance(String title) {
        PostDialogFragment frag = new PostDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    private FirebaseFirestore firestore;
    private CollectionReference collectionRef;
    private EditText posttxt;
    private ImageView addpost, addimage;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        String collectionPath = "dsiblr";
        collectionRef = firestore.collection(collectionPath);
    }
    private void initializeViews(View view){
        posttxt = view.findViewById(R.id.etPost);
        addpost = view.findViewById(R.id.postbtn);
        addimage = view.findViewById(R.id.addimagebtn);
    }

    private void onClickfunctions(View view){
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Image feature yet to be added.", Toast.LENGTH_SHORT).show();
            }
        });

        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = posttxt.getText().toString().trim();
                if (postText.isEmpty()) {
                    Toast.makeText(view.getContext(), "Empty Post", Toast.LENGTH_SHORT).show();
                    VibrationUtils.vibrate(getContext(), 200);
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("post text", postText);
                    data.put("time", com.google.firebase.Timestamp.now());
                    data.put("likes", 0);
                    createDocument(data, view.getContext());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        initializeViews(view);
        onClickfunctions(view);
        return view;
    }

    private void createDocument(Map<String, Object> data, Context context) {

        String docname = postName();
       firestore.collection("dsiblr").document(docname)
               .set(data)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Toast.makeText(context, "Posted!", Toast.LENGTH_SHORT).show();
                       databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                       FirebaseAuth auth = FirebaseAuth.getInstance();
                       FirebaseUser user = auth.getCurrentUser();
                       String userId = user.getUid();
                       databaseReference.child(userId).child("posts").child(docname).setValue(posttxt.getText().toString().trim());
                       dismiss();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(context, "Could not post.", Toast.LENGTH_SHORT).show();
                   }
               });

        firestore.collection("dsiblr").document(docname).collection("replies").document("nulldoc_placeholder").set(new HashMap<>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String postName(){
        Random random = new Random();
        String randomName = "Anonym"+String.valueOf(random.nextInt(900000) + 100000);
        return randomName;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = (EditText) view.findViewById(R.id.etPost);
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
