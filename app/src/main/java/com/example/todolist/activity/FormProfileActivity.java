package com.example.todolist.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.R;
import com.example.todolist.helper.DBHelper;
import java.util.HashMap;

public class FormProfileActivity extends AppCompatActivity {
    EditText editTextName;
    RadioButton radioMale, radioFemale;
    SeekBar seekBarAge;
    CheckBox checkBoxPainting, checkboxReading, checkboxSinging;
    Button buttonSave, buttonBack;
    TextView textSeekbarAge;
    String selectedGender, selectedHobby;
    DBHelper SQLite = new DBHelper(this);
    boolean isUserAlreadyExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile);

        editTextName = findViewById(R.id.editName);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        textSeekbarAge = findViewById(R.id.textSeekbarValue);
        seekBarAge = findViewById(R.id.seekBarAge);
        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSeekbarAge.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        checkBoxPainting = findViewById(R.id.checkboxPainting);
        checkboxReading = findViewById(R.id.checkboxReading);
        checkboxSinging = findViewById(R.id.checkboxSinging);
        buttonSave = findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(v -> {
            saveProfile();
        });
        buttonBack = findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(v -> {
            finish();
        });
        setUserData();
    }

    public void saveProfile() {
        selectedGender = "";
        if (radioMale.isChecked()) {
            selectedGender = "Laki-laki";
        } else if (radioFemale.isChecked()) {
            selectedGender = "Perempuan";
        }

        selectedHobby = "";
        if (checkBoxPainting.isChecked()) {
            selectedHobby += "Melukis ";
        }
        if (checkboxReading.isChecked()) {
            selectedHobby += "Membaca ";
        }
        if (checkboxSinging.isChecked()) {
            selectedHobby += " Menyanyi ";
        }

        if (isUserAlreadyExist) {
            SQLite.updateUser(
                    editTextName.getText().toString(),
                    selectedGender,
                    Integer.parseInt(textSeekbarAge.getText().toString()),
                    selectedHobby);

        } else {
            SQLite.insertUser(
                    editTextName.getText().toString(),
                    selectedGender,
                    Integer.parseInt(textSeekbarAge.getText().toString()),
                    selectedHobby);
        }
        finish();
    }

    public void setUserData() {
        HashMap<String, String> row = SQLite.getUser();
        if (!row.isEmpty()) {
            isUserAlreadyExist = true;
        }
        String name = row.get("username") == null ? "" : row.get("username");
        String gender = row.get("gender") == null ? "" : row.get("gender");
        String age = row.get("age") == null ? "" : row.get("age");
        String hobby = row.get("hobby") == null ? "" : row.get("hobby");

        editTextName.setText(name);

        if (gender.equalsIgnoreCase("Laki-laki")) {
            radioMale.setChecked(true);
        } else if (gender.equalsIgnoreCase("Perempuan")) {
            radioFemale.setChecked(true);
        } else {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
        }

        if (!age.equals("")) {
            textSeekbarAge.setText(age);
            seekBarAge.setProgress(Integer.parseInt(age));
        }

        if (hobby.contains("Melukis")) {
            checkBoxPainting.setChecked(true);
        }
        if (hobby.contains("Membaca")) {
            checkboxReading.setChecked(true);
        }
        if (hobby.contains("Menyanyi")) {
            checkboxSinging.setChecked(true);
        }

    }

}