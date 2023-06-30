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

import com.example.socialmedia1.utils.VibrationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import com.example.socialmedia1.R;

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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        String collectionPath = "dsiblr";
        collectionRef = firestore.collection(collectionPath);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        Map<String, Object> data = new HashMap<>();
        posttxt = view.findViewById(R.id.etPost);
        addpost = view.findViewById(R.id.postbtn);
        addimage = view.findViewById(R.id.addimagebtn);

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Image feature yet to added.", Toast.LENGTH_SHORT).show();
            }
        });
        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posttxt.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Empty Post", Toast.LENGTH_SHORT).show();
                    VibrationUtils.vibrate(getContext(), 200);
                }
                else{
                    data.put("post text", posttxt.getText().toString());
                    data.put("time", com.google.firebase.Timestamp.now());
                    createDocument(data,view.getContext());
                    dismiss();
                }

            }
        });
        return view;
    }

    private void createDocument(Map<String, Object> data, Context context) {
        collectionRef.add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentRef = task.getResult();
                    if (documentRef != null) {
                        // Document created successfully
                        String documentId = documentRef.getId();
                        Toast.makeText(context, "Posted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error creating document
                    Toast.makeText(context, "Not posted!", Toast.LENGTH_SHORT).show();
                }
            }
            });
        }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.etPost);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
