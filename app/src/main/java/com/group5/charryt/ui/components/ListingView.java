package com.group5.charryt.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.Listing;

@SuppressLint("ViewConstructor")
public class ListingView extends View {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;

    private LinearLayout linearLayout;

    private Listing listing;

    public ListingView(Context context, ViewGroup parent, Listing listing) {

        super(context);
        View view = inflate(context, R.layout.listing_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);
        imageView = view.findViewById(R.id.item_image_view);

        this.listing = listing;

        updateView();

    }

    @UiThread
    public void updateView() {
        // Bug fix for stupid fudging ID thing that doesn't make sense but shut up this was
        // the only way I could fix it, fight me.
        // This generates a unique ID for each item in the layout. Because of this, all UI
        // references need to be obtained BEFORE this loop.
        linearLayout.setId(generateViewId());
        int childCount = linearLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View v = linearLayout.getChildAt(i);
            v.setId(generateViewId());
        }

        // Test image
        Drawable testImage = ContextCompat.getDrawable(getContext(), R.drawable.test);
        imageView.setImageDrawable(testImage);
        descriptionTextView.setText(listing.getDescription());
        titleTextView.setText(listing.getTitle());
    }

}
