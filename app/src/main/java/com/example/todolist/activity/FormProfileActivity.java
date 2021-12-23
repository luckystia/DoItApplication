package com.example.todolist.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.todolist.R;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private final UserData user = new UserData();
    private EditText inputName, inputUsername, inputOldPassword, inputNewPassword;
    private TextView btnChangePassword, showOldPwd, showNewPwd;
    private ConstraintLayout changePasswordSection;
    private ImageButton btnBack;
    private ImageView avatar;
    private SessionManager sessionManager;
    private ApiService apiService;
    public static final int REQUEST_IMAGE = 100;
    private Uri uri;
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
        avatar = findViewById(R.id.avatar);
        showOldPwd = findViewById(R.id.showOldPwd);
        showNewPwd = findViewById(R.id.showNewPwd);
        changePasswordSection = findViewById(R.id.changePasswordSection);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        apiService = ApiUtils.getUsetService();
        btnSubmit = findViewById(R.id.btn_submit);

        customDIalog = new CustomDIalog(FormProfileActivity.this);

        String avatarUrl = sessionManager.getUserDetail().get("avatar");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        if (avatarUrl != null){
            Glide.with(this).load("http://apitodolistfix.menkz.xyz/storage/"+avatarUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
        .into(avatar);
        }


        avatar.setOnClickListener(v ->{
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                CropImage.startPickImageActivity(FormProfileActivity.this);

            }else{
                EasyPermissions.requestPermissions(this,"Izinkan Aplikasi Mengakses Storage?",REQUEST_IMAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
            }


        });
        showOldPwd.setOnClickListener(v -> {
            if (inputOldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                inputOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showOldPwd.setText("Hide Password");
            }else{
                showOldPwd.setText("Show Password");
                inputOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        showNewPwd.setOnClickListener(v -> {
            if (inputNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                inputNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showNewPwd.setText("Hide Password");
            }else{
                showNewPwd.setText("Show Password");
                inputNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

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
        String filePath = null;
        File file = null;
        MultipartBody.Part body;
        RequestBody old_password = null;
        RequestBody  new_password = null;
        if (uri != null){
            filePath = getRealPathFromURIPath(uri,FormProfileActivity.this);
            file = new File(filePath);

            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"),file); //membungkus file ke dalam request body
            body = MultipartBody.Part.createFormData("avatarUrl",file.getName(),mFile); // membuat formdata multipart berisi request body
        }else{
            RequestBody mFile = RequestBody.create(MultipartBody.FORM,"");
            body = MultipartBody.Part.createFormData("avatarUrl","",mFile);
        }

        RequestBody fullName =
                RequestBody.create(MediaType.parse("multipart/form-data"), user.getName());

        RequestBody username =
                RequestBody.create(MediaType.parse("multipart/form-data"), user.getUsername());

        if (changePasswordSection.getVisibility() == View.VISIBLE){
             old_password =
                    RequestBody.create(MediaType.parse("multipart/form-data"), user.getOld_password());

             new_password =
                    RequestBody.create(MediaType.parse("multipart/form-data"), user.getNew_password());
        }

        Call<User> call = apiService.updateUser(fullName, username, old_password, new_password, body, tokenLogin);
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
//                        Toast.makeText(FormProfileActivity.this, userData.ge(), Toast.LENGTH_SHORT).show();
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

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputName.setText(sessionManager.getUserDetail().get("name"));
        inputUsername.setText(sessionManager.getUserDetail().get("username"));
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 0);
            } else {
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                avatar.setImageURI(result.getUri());
                uri = result.getUri();
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

}