package com.group5.charryt.ui.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.Listing;

import static com.group5.charryt.R.layout.listing_view;

public class ListingView extends View {
    private TextView titleTextView;
    private FrameLayout frameLayout;

    public ListingView(Context context, ViewGroup parent, Listing listing) {

        super(context);
        View view = inflate(context, listing_view, parent);
        titleTextView = view.findViewById(R.id.title_text);
        frameLayout = view.findViewById(R.id.frame_layout);

        // Bug fix for stupid fudging ID thing that doesn't make sense but shut up this was
        // the only way I could fix it, fight me.
        // This generates a unique ID for each item in the layout. Because of this, all UI
        // references need to be obtained BEFORE this loop.
        int childCount = frameLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = frameLayout.getChildAt(i);
            v.setId(generateViewId());
        }

        titleTextView.setText(listing.getTitle());
        titleTextView.postInvalidate();


    }
}
