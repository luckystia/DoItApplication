package com.example.todolist.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todolist.R;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormProfileActivity extends AppCompatActivity {
    private final UserData user = new UserData();
    private EditText inputName, inputUsername, inputOldPassword, inputNewPassword;
    private TextView btnChangePassword;
    private ConstraintLayout changePasswordSection;
    private ImageButton btnBack;
    private SessionManager sessionManager;
    private ApiService apiService;
    private Button btnSubmit;
    private CustomDIalog customDIalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile);
        sessionManager = new SessionManager(this);
        btnBack = findViewById(R.id.backbtn);
        changePasswordSection = findViewById(R.id.changePasswordSection);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        apiService = ApiUtils.getUsetService();
        btnSubmit = findViewById(R.id.btn_submit);
        customDIalog = new CustomDIalog(FormProfileActivity.this);

        inputName.setText(sessionManager.getUserDetail().get("name"));
        inputUsername.setText(sessionManager.getUserDetail().get("username"));

        btnChangePassword.setOnClickListener(v -> {
            if (changePasswordSection.getVisibility() != View.VISIBLE) {
                changePasswordSection.setVisibility(View.VISIBLE);
                btnChangePassword.setText("Cancel Password Change");
            } else {
                changePasswordSection.setVisibility(View.GONE);
                btnChangePassword.setText("Change Password");
            }
        });
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnSubmit.setOnClickListener(v -> {
            customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
            user.setName(inputName.getText().toString());
            user.setUsername(inputUsername.getText().toString());

            if (changePasswordSection.getVisibility() == View.VISIBLE)
                user.setOld_password(inputOldPassword.getText().toString().trim());
            user.setNew_password(inputNewPassword.getText().toString().trim());
            updateUser(sessionManager.getUserDetail().get("loggedToken"), user);
        });

    }

    private void updateUser(String tokenLogin, UserData user) {
        StringBuilder allaMessages = new StringBuilder();
        Call<User> call = apiService.updateUser(tokenLogin, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    customDIalog.dismissDialog();

                    for (String alert : response.body().getMessage()) {
                        if (allaMessages.length() > 0) {
                            allaMessages.append(" & "); // some divider between the different texts
                        }
                        allaMessages.append(alert);
                    }
                    customDIalog.startAlertDialog("dialog_info", allaMessages.toString(), R.layout.info_layout_dialog);

                    if (allaMessages.toString().equals("success update")) {
                        sessionManager = new SessionManager(FormProfileActivity.this);
                        sessionManager.clear();

                        UserData userData = response.body().getData();
                        sessionManager.createLoginSession(userData);

                    }

                }

            }

            @Override
            public void onFailure(Call<User> call1, Throwable throwable) {
                customDIalog.dismissDialog();
                Toast.makeText(FormProfileActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputName.setText(sessionManager.getUserDetail().get("name"));
        inputUsername.setText(sessionManager.getUserDetail().get("username"));
    }


}