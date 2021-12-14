package com.example.todolist.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.R;

import pl.droidsonroids.gif.GifImageView;

public class CustomDIalog {
    private final Activity activity;
    private Dialog dialog;

    public CustomDIalog(Activity myActivity) {
        activity = myActivity;
    }

    public void startAlertDialog(String type, String message, Integer view) {
        dialog = new Dialog(activity);
        dialog.setContentView(view);

        if (type.equals("dialog_info")) {
            GifImageView imageView = dialog.findViewById(R.id.animateIcon);
            TextView messages = dialog.findViewById(R.id.messages);
            Button btnDone = dialog.findViewById(R.id.btnDone);
            if (message.equals("success")) {
//                prvent cancel the dialog
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                });

                imageView.setBackgroundResource(R.drawable.check_mark);
                messages.setText("Registration is success. Now You Can Login Using Your Account");
                dialog.setCanceledOnTouchOutside(false);
                btnDone.setText("Done");
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(activity, "menuju halaman login", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                imageView.setBackgroundResource(R.drawable.error_animation);
                messages.setText(message);
                btnDone.setText("Close");
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                    }
                });
            }
        } else if (type.equals("loading")) {
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }


}
