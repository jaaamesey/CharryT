package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;
import com.group5.charryt.Utils;

public class ProfileFragment extends Fragment {
    private EditText firstNameTxt, lastNameTxt;
    private TextView userType;
    private Button editDetails;
    private FirebaseUser user;
    private FirebaseFirestore db;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = Utils.getMainActivity(this);
        main.setToolbarText("Profile Details");
        firstNameTxt = getView().findViewById(R.id.firstNameTxt);
        lastNameTxt = getView().findViewById(R.id.lastNameTxt);
        userType = getView().findViewById(R.id.userType);
        editDetails = getView().findViewById(R.id.editDetails);
        firstNameTxt.setInputType(0);
        lastNameTxt.setInputType(0);
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                firstNameTxt.setText("No user logged in");
            } else {
                db = FirebaseFirestore.getInstance();

                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                firstNameTxt.setText(document.getString("firstName"));
                                lastNameTxt.setText(document.getString("lastName"));
                                if(document.getString("userType") != null)
                                    userType.setText(document.getString("userType"));
                            }
                        }
                    }
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = editDetails.getText().toString();
                if(state.equals("Edit Details")){
                    firstNameTxt.setInputType(1);
                    lastNameTxt.setInputType(1);
                    editDetails.setText("Make Changes");
                } else {
                    firstNameTxt.setInputType(0);
                    lastNameTxt.setInputType(0);
                    String newFirstName = firstNameTxt.getText().toString();
                    String newLastName = lastNameTxt.getText().toString();
                    DocumentReference docRef = db.collection("users").document(user.getUid());
                    docRef.update("firstName", newFirstName);
                    docRef.update("lastName", newLastName);
                    editDetails.setText("Edit Details");
                }

            }
        });
    }



}
