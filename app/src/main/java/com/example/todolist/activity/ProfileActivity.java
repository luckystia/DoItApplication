package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.helper.DBHelper;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    TextView textName, textAge, textGender, textHobby;
    Button buttonEditProfile, buttonBack;
    DBHelper SQLite = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textName = findViewById(R.id.textName);
        textGender = findViewById(R.id.textGender);
        textAge = findViewById(R.id.textAge);
        textHobby = findViewById(R.id.textHobby);
        buttonEditProfile = findViewById(R.id.btn_edit_profile);
        buttonEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, FormProfileActivity.class));
        });
        buttonBack = findViewById(R.id.btn_back);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    public void setUserData() {
        HashMap<String, String> row = SQLite.getUser();
        String name = row.get("username") == null ? "" : row.get("username");
        String gender = row.get("gender") == null ? "" : row.get("gender");
        String age = row.get("age") == null ? "" : row.get("age");
        String hobby = row.get("hobby") == null ? "" : row.get("hobby");

        textName.setText("Nama : " + name);
        textGender.setText("Jenis Kelamin : " + gender);
        textAge.setText("Umur : " + age);
        textHobby.setText("Hobi : " + hobby);
    }
}