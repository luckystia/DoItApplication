package com.example.todolist.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.todolist.R;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, usernameInput, passwordInput, confirmPwdInput;
    private Button btnSubmit;
    private Drawable backgroundEmpty, backgroundFilled;
    private boolean isAllFieldsChecked = false;
    private Drawable img;
    private String emailPattern, usernameValid;
    private ApiService apiService;
    private CustomDIalog customDIalog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPwdInput = findViewById(R.id.confirmPwdInput);
        btnSubmit = findViewById(R.id.submitBtn);
        apiService = ApiUtils.getUsetService();
        customDIalog = new CustomDIalog(RegisterActivity.this);
        //drwaable
        img = ContextCompat.getDrawable(RegisterActivity.this, R.drawable.icon_success);
        img.setBounds(0, 0, 70, 70);

        //email validation
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        usernameValid = "^[A-Za-z][A-Za-z0-9]{5,}$";

        //set Backgorund on fill
        setBackgroundFilled(nameInput);
        setBackgroundFilled(emailInput);
        setBackgroundFilled(usernameInput);
        setBackgroundFilled(passwordInput);
        setBackgroundFilled(confirmPwdInput);

        //custom validation
        customValidationForm(emailInput, emailPattern);
        customValidationForm(usernameInput, usernameValid);
        passwordInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (passwordInput.getText().toString().trim().matches(confirmPwdInput.getText().toString()) && s.length() != 0) {
                    confirmPwdInput.setCompoundDrawables(null, null, img, null);
                    // or

                } else if (s.length() > 12 || s.length() < 6) {
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
                    // or

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

        //submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
                    UserData user = new UserData();
                    user.setName(nameInput.getText().toString());
                    user.setEmail(emailInput.getText().toString());
                    user.setUsername(usernameInput.getText().toString());
                    user.setPassword(passwordInput.getText().toString());
                    user.setPasswordConfirmation(confirmPwdInput.getText().toString());
                    register(user);

                }
            }
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
        if (emailInput.getText().toString().trim().length() == 0) {
            emailInput.setError("This field is required");
            return false;
        } else if (!emailInput.getText().toString().trim().matches(emailPattern)) {
            emailInput.setError("Email format is invalid");
            return false;
        }
        if (usernameInput.getText().toString().trim().length() == 0) {
            usernameInput.setError("This field is required");
            return false;
        } else if (!usernameInput.getText().toString().trim().matches(usernameValid)) {
            usernameInput.setError("Username can't have a blank space & have minimal 6 character");
            return false;
        }
        if (passwordInput.getText().toString().trim().length() == 0) {
            passwordInput.setError("This field is required");
            return false;
        } else if (passwordInput.getText().toString().trim().length() > 12 || passwordInput.getText().toString().trim().length() < 6) {
            passwordInput.setError("Password Minimal have 6 & Maximal have 12 character");
            return false;
        }
        if (confirmPwdInput.getText().toString().trim().length() == 0) {
            confirmPwdInput.setError("This field is required");
            return false;
        } else if (!confirmPwdInput.getText().toString().trim().matches(passwordInput.getText().toString())) {
            confirmPwdInput.setError("Password not match");
            return false;
        } else if (confirmPwdInput.getText().toString().trim().length() > 12 || confirmPwdInput.getText().toString().trim().length() < 6) {
            confirmPwdInput.setError("Password Minimal have 6 & Maksimal have 12 character");
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


}