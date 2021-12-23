package com.example.todolist.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.example.todolist.R;
import com.example.todolist.activity.AddEditActivity;
import com.example.todolist.activity.LoginActivity;
import com.example.todolist.activity.MainActivity;

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
            if (message.equals("success") || message.equals("true") || message.equals("success update") || message.equals("success update with password") || message.equals("success add task") || message.equals("success edit task") || message.equals("error date")) {
//                prvent cancel the dialog
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                });

                imageView.setBackgroundResource(R.drawable.check_mark);
                if (message.equals("success")) {
                    messages.setText("Registration is success. Now You Can Login Using Your Account");
                    btnDone.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                    });
                } else if (message.equals("true")) {
                    messages.setText("Login Success. Click the button below to go to the main page! ");
                    btnDone.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                    });
                } else if (message.equals("success add task")) {
                    messages.setText("Add Task Success. Click the button below to go to the main page!");
                    btnDone.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    });
                }else if(message.equals("error date")){
                    imageView.setBackgroundResource(R.drawable.error_animation);
                    messages.setText("The Date Has Passed");
                    btnDone.setText("Close");
                    dialog.setCanceledOnTouchOutside(false);
                    btnDone.setOnClickListener(v -> {
                            dismissDialog();
                    });
                } else if (message.equals("success edit task")) {
                    messages.setText("Edit Task Success. Click the button below to go to the main page! ");
                    btnDone.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    });
                } else if (message.equals("success update") || message.equals("success update with password")) {
                    //on not password update
                    if (!message.equals("success update with password")) {
                        messages.setText("Profile Update Success");
                        btnDone.setOnClickListener(v -> {
                            activity.onBackPressed();
                        });
                    } else {
                        //on password update
                        messages.setText("Profile and Password Update Success. Please Re-Login!");
                        btnDone.setOnClickListener(v -> {
                            SessionManager sessionManager = new SessionManager(activity);
                            sessionManager.logout();

                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        });
                    }

                }
                dialog.setCanceledOnTouchOutside(false);
                btnDone.setText("Done");

            } else {
                imageView.setBackgroundResource(R.drawable.error_animation);
                messages.setText(message);
                btnDone.setText("Close");
                dialog.setCanceledOnTouchOutside(false);
                btnDone.setOnClickListener(v -> {
                    if (message.equals("You Are Not Logged In")) {
                        SessionManager sessionManager = new SessionManager(activity);
                        sessionManager.logout();

                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
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
