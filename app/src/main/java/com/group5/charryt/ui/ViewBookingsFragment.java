package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.group5.charryt.data.Booking;
import com.group5.charryt.data.Listing;
import com.group5.charryt.ui.components.BookingView;
import com.group5.charryt.ui.components.ListingView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ViewBookingsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Booking> bookings = new ArrayList<Booking>();

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout bookingsLayout;
    private TextView loadingTv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_bookings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.setToolbarText("Bookings");

        refreshLayout = view.findViewById(R.id.refreshLayout);
        bookingsLayout = view.findViewById(R.id.bookingsLayout);
        loadingTv = view.findViewById(R.id.loadingText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBookings();
            }
        });

        refreshBookings();


    }

    private void refreshBookings() {
        CollectionReference bookingsCollection = db.collection("bookings");
        Query query = bookingsCollection.whereArrayContains("involvedUsers", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Utils.showDialog("ERROR: Could not access bookings: " + task.getException());
                    loadingTv.setVisibility(View.INVISIBLE);
                    return;
                }

                // Nested in a try catch block to prevent bugs from user spamming back button
                // and stuff like that.
                try {
                    bookings.clear();
                    bookingsLayout.removeAllViews();
                    // Update listings array
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setId(document.getId());

                        bookings.add(booking);
                    }

                    // Sort bookings array from newest to oldest (descending)
                    bookings.sort(new Comparator<Booking>() {
                        @Override
                        public int compare(Booking booking1, Booking booking2) {
                            return booking2.getDate().compareTo(booking1.getDate());
                        }
                    });

                    for (Booking booking : bookings) {
                        new BookingView(getContext(), bookingsLayout, booking);
                    }

                    // Done
                    loadingTv.setVisibility(View.INVISIBLE);
                } catch (NullPointerException nullPointerException) {
                    System.out.println("ERROR: " + nullPointerException);
                    loadingTv.setVisibility(View.INVISIBLE);
                    refreshLayout.setRefreshing(false);
                }

                loadingTv.setVisibility(View.INVISIBLE);
                refreshLayout.setRefreshing(false);
            }
        });
    }


}
