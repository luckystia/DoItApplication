package com.example.todolist.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.helper.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddEditActivity extends AppCompatActivity {
    TextView title;
    EditText txt_id, txt_name, txt_date, txt_isi;
    Button btn_submit, btn_cancel;
    DBHelper SQLite = new DBHelper(this);
    String id, name, isi, date;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        title = findViewById(R.id.title);
        txt_id = findViewById(R.id.txt_id);
        txt_name = findViewById(R.id.txt_name);
        txt_date = findViewById(R.id.txt_date);
        txt_isi = findViewById(R.id.txt_isi);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);
        id = getIntent().getStringExtra(MainActivity.TAG_ID);
        name = getIntent().getStringExtra(MainActivity.TAG_NAME);
        isi = getIntent().getStringExtra(MainActivity.TAG_ISI);
        date = getIntent().getStringExtra(MainActivity.TAG_DATE);
        if (id == null || id == "") {
            title.setText("Tambah Data");
        } else {
            title.setText("Edit Data");
            txt_id.setText(id);
            txt_name.setText(name);
            txt_isi.setText(isi);
            txt_date.setText(date);
        }

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelDate();
        };

        txt_date.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(AddEditActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btn_submit.setOnClickListener(v -> {
            try {
                if (txt_id.getText().toString().equals("")) {
                    save();
                } else {
                    edit();
                }
            } catch (Exception e) {
                Log.e("Submit", e.toString());
            }
        });
        btn_cancel.setOnClickListener(v -> {
            blank();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                blank();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateLabelDate() {
        String myFormat ="dd-MM-yyyy" ; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        txt_date.setText(sdf.format(myCalendar.getTime()));
    }

    // Make blank all Edit Text
    private void blank() {
        txt_name.requestFocus();
        txt_id.setText(null);
        txt_name.setText(null);
        txt_date.setText(null);
    }

    // Save data to SQLite database
    private void save() {
        if (String.valueOf(txt_name.getText()) == null || String.valueOf(txt_name.getText()).equals("") ||
                String.valueOf(txt_date.getText()) == null || String.valueOf(txt_date.getText()).equals("") ||
                String.valueOf(txt_isi.getText()) == null || String.valueOf(txt_isi.getText()).equals("")
        ) {
            Toast.makeText(getApplicationContext(),
                    "Inputan tidak boleh kosong ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.insertTask(
                    txt_name.getText().toString().trim(),
                    txt_date.getText().toString().trim(),
                    txt_isi.getText().toString().trim());
            blank();
            finish();
        }
    }

    // Update data in SQLite database
    private void edit() {
        if (String.valueOf(txt_name.getText()) == null || String.valueOf(txt_name.getText()).equals("") ||
                String.valueOf(txt_date.getText()) == null || String.valueOf(txt_date.getText()).equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Inputan tidak boleh kosong ...", Toast.LENGTH_SHORT).show();
        } else {
            SQLite.updateTask(
                    Integer.parseInt(txt_id.getText().toString().trim()),
                    txt_name.getText().toString().trim(),
                    txt_date.getText().toString().trim(),
                    txt_isi.getText().toString().trim()
            );
            blank();
            finish();
        }
    }
}
