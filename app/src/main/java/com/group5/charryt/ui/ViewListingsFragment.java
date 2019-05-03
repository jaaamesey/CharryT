package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.Listing;
import com.group5.charryt.ui.components.ListingView;

import java.util.ArrayList;

public class ViewListingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<Listing> listings = new ArrayList<>();

    private TextView loadingText;
    private LinearLayout listingsVBox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_listings_fragment, container, false);
        loadingText = view.findViewById(R.id.loading_text);
        listingsVBox = view.findViewById(R.id.listings_vbox);

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
        // Clear visible items and show loading text
        listingsVBox.removeAllViews();
        loadingText.setVisibility(View.VISIBLE);

        CollectionReference listingsCollection = db.collection("listings");
        listingsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Utils.showDialog("ERROR: Could not access listings: " + task.getException());
                    loadingText.setVisibility(View.INVISIBLE);
                    return;
                }
                // Update listings array
                listings.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Listing listing = document.toObject(Listing.class);
                    listings.add(listing);
                    ListingView listingView = new ListingView(getContext(), listingsVBox, listing);
                }

                // Done
                loadingText.setVisibility(View.INVISIBLE);
            }
        });
    }


}
