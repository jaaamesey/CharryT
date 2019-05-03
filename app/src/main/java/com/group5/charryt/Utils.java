package com.group5.charryt;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.group5.charryt.ui.MainActivity;


// A class to stash everyone's little helper functions that don't belong in any other class.
// Functions must ALL be static.
public class Utils {
    public static Context currentContext;

    // Creates a dialog box - useful for debugging and showing error messages
    public static void showDialog(String text, Context context, boolean alsoPrintToConsole) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(text);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        if (alsoPrintToConsole)
            System.out.println(text);
    }

    public static void showDialog(String text, Context context) {
        showDialog(text, context, true);
    }

    public static void showDialog(String text) {
        showDialog(text, currentContext, true);
    }

    // Shorthand way to get a reference to the main activity (pass "this" into the function parameters)
    public static MainActivity getMainActivity(Fragment self) {
        MainActivity main = (MainActivity) self.getActivity();
        // If this is null, the main activity probably either doesn't exist or the thing you are
        // calling it from was not created from the main activity.
        assert main != null;
        return main;
    }


}
