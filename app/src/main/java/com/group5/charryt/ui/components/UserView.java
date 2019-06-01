package com.group5.charryt.ui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.MessageActivity;
import com.group5.charryt.ui.UserProfileActivity;

import org.parceler.Parcels;

@SuppressLint("ViewConstructor")
public class UserView extends View {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private LinearLayout linearLayout;

    private User user;

    public UserView(final Context context, ViewGroup parent, final User user, boolean skipToMessages) {
        super(context);
        final View view = inflate(context, R.layout.user_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);

        this.user = user;
        setTag(user);
        updateView(user, skipToMessages);

        if (skipToMessages) {
            ((CardView) linearLayout.getParent()).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageActivity item = new MessageActivity();
                    Intent viewItem = new Intent(getActivity(), item.getClass());
                    viewItem.putExtra("user", Parcels.wrap(user));
                    context.startActivity(viewItem);
                }
            });
        } else {
            ((CardView) linearLayout.getParent()).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserProfileActivity item = new UserProfileActivity();
                    Intent viewItem = new Intent(getActivity(), item.getClass());
                    viewItem.putExtra("user", Parcels.wrap(user));
                    context.startActivity(viewItem);
                }
            });
        }
    }

    @UiThread
    public void updateView(User user, boolean skipToMessages) {
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

        if (skipToMessages) {
            // Email field is used here to store previous message data.
            descriptionTextView.setText(user.getEmailAddress());
        } else {
            if (user.getUserType() == User.UserType.Charity)
                descriptionTextView.setText("Charity");
            else {
                descriptionTextView.setText("Donor");
            }
        }


        titleTextView.setText(user.getName());

    }


    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
