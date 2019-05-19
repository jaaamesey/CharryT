package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.group5.charryt.data.Listing;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.components.ListingView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ViewListingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<Listing> listings = new ArrayList<>();

    private TextView loadingText;
    private LinearLayout listingsVBox;
    private SwipeRefreshLayout refreshLayout;
    private RadioGroup radioGroup;

    private User.UserType desiredType = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.view_listings_fragment, container, false);
        loadingText = view.findViewById(R.id.loadingText);
        listingsVBox = view.findViewById(R.id.listings_vbox);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        radioGroup = view.findViewById(R.id.radioGroup);

        // Allow refreshing of the page when scrolly refresh thing is pulled
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListings();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int buttonIndex = radioGroup.indexOfChild(view.findViewById(i));
                switch (buttonIndex) {
                    // All listings
                    case 0:
                        desiredType = null;
                        break;
                    // Charity listings
                    case 1:
                        desiredType = User.UserType.Charity;
                        break;
                    // Donor listings
                    case 2:
                        desiredType = User.UserType.Donor;
                        break;
                }
                listingsVBox.removeAllViews();
                loadingText.setVisibility(View.VISIBLE);
                refreshListings();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.setToolbarText("Listings");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        refreshListings();
    }

    private void refreshListings() {
        CollectionReference listingsCollection = db.collection("listings");
        Query query = listingsCollection;
        // If desired type isn't null, filter by it.
        if (desiredType != null)
            query = listingsCollection.whereEqualTo("type", desiredType.toString());
        // Finally perform the query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Utils.showDialog("ERROR: Could not access listings: " + task.getException());
                    loadingText.setVisibility(View.INVISIBLE);
                    return;
                }

                // Nested in a try catch block to prevent bugs from user spamming back button
                // and stuff like that.
                try {
                    listings.clear();
                    listingsVBox.removeAllViews();
                    // Update listings array
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            Listing listing = document.toObject(Listing.class);
                            listing.setId(document.getId());
                            listings.add(listing);
                        }
                        catch (RuntimeException exception) {
                            Utils.showDialog("Invalid listing: " + document.getId() + "\n" + exception.toString());
                        }
                    }
                    // Sort listings array from newest to oldest (descending)
                    listings.sort(new Comparator<Listing>() {
                        @Override
                        public int compare(Listing listing1, Listing listing2) {
                            return listing2.getPostDate().compareTo(listing1.getPostDate());
                        }
                    });
                    // Create new listing views for the UI for each listing (auto attached to listingsVBox)
                    for (Listing listing : listings) {
                        new ListingView(getContext(), listingsVBox, listing);
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
