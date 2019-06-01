package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.components.UserView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nullable;

// This code is literally just ViewListingsFragment with a couple of changes.
// Look at ViewListingsFragment and ViewBookingsFragment to get an idea of how things are implemented.
public class ViewMessagesFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<User> users = new ArrayList<>();

    private TextView loadingText;
    private EditText searchBar;
    private Button searchButton;
    private LinearLayout usersVBox;
    private SwipeRefreshLayout refreshLayout;
    private String input = null;
    private User.UserType desiredType = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.view_users_fragment, container, false);
        loadingText = view.findViewById(R.id.loadingText);
        usersVBox = view.findViewById(R.id.users_layout);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        searchBar = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchBtn);
        // Allow refreshing of the page when scrolly refresh thing is pulled
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUsers();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.setToolbarText("Messages");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        refreshUsers();

        db.collection("messages").document(User.getCurrentUser().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                refreshUsers();
            }
        });
    }

    public void refreshUsers() {
        CollectionReference usersCollection = db.collection("messages");
        // do something with the query and don't just show all users, hint hint search box
        DocumentReference query = usersCollection.document(User.getCurrentUser().getId());
        input = searchBar.getText().toString().toLowerCase();
        // Finally perform the query
        query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                loadingText.setVisibility(View.INVISIBLE);
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

                    ArrayList<User> messagedUsers = new ArrayList<>();
                    final Map<String, Object> data = (task.getResult().getData());
                    ArrayList<String> keys = new ArrayList<>();
                    for (String key : task.getResult().getData().keySet()) {
                        keys.add(key);
                    }

                    // Sort by date
                    keys.sort(new Comparator<String>() {
                        @Override
                        public int compare(String a, String b) {
                            Map<String, Object> a_data = (Map<String, Object>) data.get(a);
                            Timestamp a_date = (Timestamp) a_data.get("date");

                            Map<String, Object> b_data = (Map<String, Object>) data.get(b);
                            Timestamp b_date = (Timestamp) b_data.get("date");
                            return a_date.compareTo(b_date);
                        }
                    });

                    for (String key : keys) {
                        User user = new User();
                        user.setId(key);
                        Map<String, Object> messageData = (Map<String, Object>) data.get(key);
                        user.setName((String) messageData.get("name"));
                        user.setEmailAddress((String) messageData.get("text"));
                        messagedUsers.add(user);

                    }
                    // Update users array
                    for (User user : messagedUsers) {
                        try {
                            if (input != null) {
                                if (user.getFirstName() != null && user.getLastName() != null) {
                                    if (user.getFirstName().toLowerCase().contains(input) || user.getLastName().toLowerCase().contains(input)) {
                                        users.add(user);
                                        continue;
                                    }
                                }
                                if (user.getName() != null) {
                                    if (user.getName().toLowerCase().contains(input)) {
                                        users.add(user);
                                        continue;
                                    }
                                }
                            } else
                                users.add(user);
                        } catch (RuntimeException exception) {
                            Utils.showDialog("Invalid user: " + exception.toString());
                        }
                    }

                    Collections.reverse(users);
                    // Create new user views for the UI for each user (auto attached to usersVBox)
                    for (User user : users) {
                        new UserView(getContext(), usersVBox, user, true);
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
