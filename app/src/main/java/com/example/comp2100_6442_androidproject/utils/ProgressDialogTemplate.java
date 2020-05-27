package com.example.comp2100_6442_androidproject.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogTemplate {

    public static ProgressDialog showDialog(Context context, String message){
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

}
