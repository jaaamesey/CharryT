package com.group5.charryt.ui;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
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
import com.group5.charryt.data.Booking;
import com.group5.charryt.ui.components.BookingView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ViewBookingsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Booking> upcomingBookings = new ArrayList<Booking>();
    private List<Booking> pastBookings = new ArrayList<Booking>();

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

    public void refreshBookings() {
        CollectionReference bookingsCollection = db.collection("bookings");
        Query query = bookingsCollection.whereArrayContains("involvedUserIds", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
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
                    upcomingBookings.clear();
                    pastBookings.clear();
                    bookingsLayout.removeAllViews();
                    // Update listings array
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setId(document.getId());
                        // If booking is in the future, add it to the future bookings list.
                        if (booking.getDate().compareTo(Calendar.getInstance().getTime()) >= 0)
                            upcomingBookings.add(booking);
                        // Otherwise, add it to the past bookings.
                        else
                            pastBookings.add(booking);
                    }

                    // Sort upcomingBookings array from oldest to latest
                    upcomingBookings.sort(new Comparator<Booking>() {
                        @Override
                        public int compare(Booking booking1, Booking booking2) {
                            return booking1.getDate().compareTo(booking2.getDate());
                        }
                    });
                    // Sort upcomingBookings from latest to oldest
                    pastBookings.sort(new Comparator<Booking>() {
                        @Override
                        public int compare(Booking booking1, Booking booking2) {
                            return booking2.getDate().compareTo(booking1.getDate());
                        }
                    });

                    addSpacer("Upcoming bookings");

                    for (Booking booking : upcomingBookings) {
                        new BookingView(getContext(), bookingsLayout, booking);
                    }

                    addSpacer("Past bookings");

                    for (Booking booking : pastBookings) {
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


    private void addSpacer(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(39, 40, 40, 40);

        TextView spacer = new TextView(getContext());
        spacer.setText(text);
        spacer.setTextSize(20);
        spacer.setTypeface(ResourcesCompat.getFont(getContext(), R.font.noto));
        spacer.setTextColor(Color.DKGRAY);

        spacer.setLayoutParams(params);

        bookingsLayout.addView(spacer);
    }


}
