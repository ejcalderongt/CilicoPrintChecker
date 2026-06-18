package com.olc.printcilico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

public class ShowMsg {
    public static String msg;
    public static void showMsg(String msg, Context context) {
        Toast toast= Toast.makeText(context,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    private static void show(String msg, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("OK", (dialog, whichButton) -> {
        });
        alertDialog.create();
        alertDialog.show();
    }
}
