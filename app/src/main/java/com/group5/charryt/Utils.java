package com.group5.charryt;


import android.content.Context;
import android.support.v7.app.AlertDialog;


// A class to stash everyone's little helper functions that don't belong in any other class.
// Functions must ALL be static.
public class Utils {

    public static void showDialog(String text, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(text);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
