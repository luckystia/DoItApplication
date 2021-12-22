package com.example.todolist.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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
    private Drawable img;
    private String usernameValid;
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

        //drwaable
        img = ContextCompat.getDrawable(FormProfileActivity.this, R.drawable.icon_success);
        img.setBounds(0, 0, 70, 70);

        //email validation
        usernameValid = "^[A-Za-z][A-Za-z0-9]{5,10}$";

        //set Backgorund on fill
        setBackgroundFilled(inputName);
        setBackgroundFilled(inputUsername);
        setBackgroundFilled(inputOldPassword);
        setBackgroundFilled(inputNewPassword);

        //custom validation
        customValidationForm(inputUsername, usernameValid);

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
            Boolean isAllFieldsChecked = CheckAllFields();
            if (isAllFieldsChecked) {

            customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
            user.setName(inputName.getText().toString());
            user.setUsername(inputUsername.getText().toString());

            if (changePasswordSection.getVisibility() == View.VISIBLE)
                user.setOld_password(inputOldPassword.getText().toString().trim());
            user.setNew_password(inputNewPassword.getText().toString().trim());
            updateUser(sessionManager.getUserDetail().get("loggedToken"), user);
            }
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

    private void setBackgroundFilled(EditText form) {
        form.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                form.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!form.getText().toString().trim().isEmpty()) {
                    form.setTag(R.drawable.on_fill_form);
                    int drawableId = Integer.parseInt(form.getTag().toString());
                    form.setBackgroundResource(drawableId);
                } else {
                    form.setTag(R.drawable.rounded_form);
                    int drawableId = Integer.parseInt(form.getTag().toString());
                    form.setBackgroundResource(drawableId);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!form.getText().toString().trim().isEmpty()) {
                    form.setCompoundDrawables(null, null, img, null);
                } else {
                    form.setCompoundDrawables(null, null, null, null);
                }
            }
        });
    }

    private boolean CheckAllFields() {
        if (inputName.getText().toString().trim().length() <= 0) {
            inputName.setError("This field is required");
            return false;
        }
        if (inputUsername.getText().toString().trim().length() == 0) {
            inputUsername.setError("This field is required");
            return false;
        } else if (!inputUsername.getText().toString().trim().matches(usernameValid)) {
            inputUsername.setError("Username can't have a blank space & have minimal 6 characters and max 11 charaters");
            return false;
        }
        if (changePasswordSection.getVisibility() == View.VISIBLE){
            if (inputOldPassword.getText().toString().trim().length() == 0) {
                inputOldPassword.setError("This field is required");
                return false;
            } else if (inputOldPassword.getText().toString().trim().length() < 6) {
                inputOldPassword.setError("Password Minimal have 6");
                return false;
            }
            if (inputNewPassword.getText().toString().trim().length() == 0) {
                inputNewPassword.setError("This field is required");
                return false;
            } else if (inputNewPassword.getText().toString().trim().length() < 6) {
                inputNewPassword.setError("Password Minimal have 6");
                return false;
            }
        }

        // after all validation return true.
        return true;
    }

    private void customValidationForm(EditText form, String validation) {
        form.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (form.getText().toString().trim().matches(validation) && s.length() != 0) {
                    form.setCompoundDrawables(null, null, img, null);

                } else {
                    form.setCompoundDrawables(null, null, null, null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
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