package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.components.UserView;

import java.util.ArrayList;

// This code is literally just ViewListingsFragment with a couple of changes.
// Look at ViewListingsFragment and ViewBookingsFragment to get an idea of how things are implemented.
public class ViewUsersFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<User> users = new ArrayList<>();

    private TextView loadingText;
    private LinearLayout usersVBox;
    private SwipeRefreshLayout refreshLayout;

    private User.UserType desiredType = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.view_users_fragment, container, false);
        loadingText = view.findViewById(R.id.loadingText);
        usersVBox = view.findViewById(R.id.users_layout);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        // Allow refreshing of the page when scrolly refresh thing is pulled
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.setToolbarText("Users");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        refreshUsers();
    }

    public void refreshUsers() {
        CollectionReference usersCollection = db.collection("users");
        // do something with the query and don't just show all users, hint hint search box
        Query query = usersCollection;

        // Finally perform the query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Utils.showDialog("ERROR: Could not access users: " + task.getException());
                    loadingText.setVisibility(View.INVISIBLE);
                    return;
                }

                // Nested in a try catch block to prevent bugs from user spamming back button
                // and stuff like that.
                try {
                    users.clear();
                    usersVBox.removeAllViews();
                    // Update users array
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            User user = document.toObject(User.class);
                            user.setId(document.getId());
                            users.add(user);
                        } catch (RuntimeException exception) {
                            Utils.showDialog("Invalid listing: " + document.getId() + "\n" + exception.toString());
                        }
                    }

                    // Create new user views for the UI for each user (auto attached to usersVBox)
                    for (User user : users) {
                        new UserView(getContext(), usersVBox, user);
                    }

                    // Done
                    loadingText.setVisibility(View.INVISIBLE);
                } catch (NullPointerException nullPointerException) {
                    System.out.println("ERROR: " + nullPointerException);
                }

                refreshLayout.setRefreshing(false);
            }
        });
    }


}
