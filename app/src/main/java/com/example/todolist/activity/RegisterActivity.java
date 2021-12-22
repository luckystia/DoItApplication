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

public class RegisterActivity extends AppCompatActivity {
    private final UserData user = new UserData();
    TextView loginUrl;
    private EditText nameInput, usernameInput, passwordInput, confirmPwdInput;
    private Button btnSubmit;
    private Drawable backgroundEmpty, backgroundFilled;
    private boolean isAllFieldsChecked = false;
    private Drawable img;
    private String usernameValid;
    private ApiService apiService;
    private CustomDIalog customDIalog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameInput = findViewById(R.id.nameInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPwdInput = findViewById(R.id.confirmPwdInput);
        btnSubmit = findViewById(R.id.submitBtn);
        loginUrl = findViewById(R.id.loginUrl);
        apiService = ApiUtils.getUsetService();
        customDIalog = new CustomDIalog(RegisterActivity.this);

        //drwaable check
        img = ContextCompat.getDrawable(RegisterActivity.this, R.drawable.icon_success);
        img.setBounds(0, 0, 70, 70);

        //username validation
        usernameValid = "^[A-Za-z][A-Za-z0-9]{5,10}$";

        //set Backgorund on fill
        setBackgroundFilled(nameInput);
        setBackgroundFilled(usernameInput);
        setBackgroundFilled(passwordInput);
        setBackgroundFilled(confirmPwdInput);

        //custom validation
        customValidationForm(usernameInput, usernameValid);
        passwordInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (passwordInput.getText().toString().trim().matches(confirmPwdInput.getText().toString()) && s.length() != 0) {
                    confirmPwdInput.setCompoundDrawables(null, null, img, null);

                } else if (s.length() < 6) {
                    passwordInput.setCompoundDrawables(null, null, null, null);
                    confirmPwdInput.setCompoundDrawables(null, null, null, null);

                } else {
                    confirmPwdInput.setCompoundDrawables(null, null, null, null);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });
        confirmPwdInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (confirmPwdInput.getText().toString().trim().matches(passwordInput.getText().toString()) && s.length() != 0) {
                    confirmPwdInput.setCompoundDrawables(null, null, img, null);
                } else {
                    confirmPwdInput.setCompoundDrawables(null, null, null, null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });
        getCurrentFirebaseToken();

        //submit button
        btnSubmit.setOnClickListener(v -> {

            isAllFieldsChecked = CheckAllFields();
            if (isAllFieldsChecked) {
                customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);

                user.setName(nameInput.getText().toString());
                user.setUsername(usernameInput.getText().toString());
                user.setPassword(passwordInput.getText().toString());
                user.setPasswordConfirmation(confirmPwdInput.getText().toString());
                register(user);
            }

        });

        //loginUrl
        loginUrl.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

    }

    private void register(UserData user) {
        StringBuilder allaMessages = new StringBuilder();
        Call<User> call = apiService.registerUser(user);
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

                }

            }

            @Override
            public void onFailure(Call<User> call1, Throwable throwable) {
                customDIalog.dismissDialog();
                Toast.makeText(RegisterActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (nameInput.getText().toString().trim().length() <= 0) {
            nameInput.setError("This field is required");
            return false;
        }
        if (usernameInput.getText().toString().trim().length() == 0) {
            usernameInput.setError("This field is required");
            return false;
        } else if (!usernameInput.getText().toString().trim().matches(usernameValid)) {
            usernameInput.setError("Username can't have a blank space & have minimal 6 characters and max 11 charaters");
            return false;
        }
        if (passwordInput.getText().toString().trim().length() == 0) {
            passwordInput.setError("This field is required");
            return false;
        } else if (passwordInput.getText().toString().trim().length() < 6) {
            passwordInput.setError("Password Minimal have 6");
            return false;
        }
        if (confirmPwdInput.getText().toString().trim().length() == 0) {
            confirmPwdInput.setError("This field is required");
            return false;
        } else if (!confirmPwdInput.getText().toString().trim().matches(passwordInput.getText().toString())) {
            confirmPwdInput.setError("Password not match");
            return false;
        } else if (confirmPwdInput.getText().toString().trim().length() < 6) {
            confirmPwdInput.setError("Password Minimal have 6");
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

    private void getCurrentFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("TAG", "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                user.setNotif_token(token);
//                        Toast.makeText(RegisterActivity.this, "berhasil", Toast.LENGTH_SHORT).show();
            }
        });
    }

}