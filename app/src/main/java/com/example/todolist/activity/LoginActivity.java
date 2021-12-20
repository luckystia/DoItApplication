package com.example.todolist.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.todolist.R;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private TextView registerUrl;
    private Button btnSubmit;
    private CustomDIalog customDIalog;
    private String usernameValid;
    private Drawable img;
    private SessionManager sessionManager;
    private ApiService apiService;
    private boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        btnSubmit = findViewById(R.id.submitBtn);
        registerUrl = findViewById(R.id.registerUrl);
        customDIalog = new CustomDIalog(LoginActivity.this);
        apiService = ApiUtils.getUsetService();
        //drwaable
        img = ContextCompat.getDrawable(LoginActivity.this, R.drawable.icon_success);
        img.setBounds(0, 0, 70, 70);

        //set Backgorund on fill
        setBackgroundFilled(username);
        setBackgroundFilled(password);

        //custom validation
        usernameValid = "^[A-Za-z][A-Za-z0-9]{5,}$";
        customValidationForm(username, usernameValid);

        btnSubmit.setOnClickListener(v -> {
            isAllFieldsChecked = CheckAllFields();
            if (isAllFieldsChecked) {
                customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
                UserData user = new UserData();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                login(user);
            }

        });

        //register url
        registerUrl.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        });

    }

    private void login(UserData user) {
        StringBuilder allaMessages = new StringBuilder();
        Call<User> call = apiService.loginUser(user);
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

                    if (allaMessages.toString().equals("true")) {
                        sessionManager = new SessionManager(LoginActivity.this);
                        UserData userData = response.body().getData();
                        sessionManager.createLoginSession(userData);
                        getCurrentFirebaseToken(userData.getLoggedToken());
                    }

                }

            }

            @Override
            public void onFailure(Call<User> call1, Throwable throwable) {
                customDIalog.dismissDialog();
                Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (username.getText().toString().trim().length() == 0) {
            username.setError("This field is required");
            return false;
        } else if (!username.getText().toString().trim().matches(usernameValid)) {
            username.setError("Username can't have a blank space & have minimal 6 character");
            return false;
        }
        if (password.getText().toString().trim().length() == 0) {
            password.setError("This field is required");
            return false;
        } else if (password.getText().toString().trim().length() > 12 || password.getText().toString().trim().length() < 6) {
            password.setError("Password Minimal have 6 & Maximal have 12 character");
            return false;
        }
        // after all validation return true.
        return true;
    }

    private void customValidationForm(EditText form, String validation) {
        form.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (form.getText().toString().trim().matches(validation) && s.length() != 0) {
                    form.setCompoundDrawables(null, null, img, null);
                    // or

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

    private void getCurrentFirebaseToken(String tokenLogin) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        UserData userData = new UserData();
                        userData.setNotif_token(token);

                        Call<User> call = apiService.updateNotifToken(tokenLogin, userData);
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                Log.d("refreshToken", "Berhasil");
                            }

                            @Override
                            public void onFailure(Call<User> call1, Throwable throwable) {
                                Log.d("refreshToken", "Gagal");

                            }
                        });
                    }
                });
    }
}