package com.example.todolist.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.PostPutDelTask;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEditActivity extends AppCompatActivity {
    private TextView title;
    private EditText txt_id, txt_name, txt_date, txt_isi;
    private Button btn_submit, btn_cancel;
    private DBHelper SQLite = new DBHelper(this);
    private String id, name, isi, date;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_remake);

        sessionManager = new SessionManager(AddEditActivity.this);

        apiService = ApiClientLocal.getClient().create(ApiService.class);

        title = findViewById(R.id.title);
        txt_id = findViewById(R.id.txt_id);
        txt_name = findViewById(R.id.txt_name);
        txt_date = findViewById(R.id.txt_date);
        txt_isi = findViewById(R.id.txt_isi);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);
        id = getIntent().getStringExtra(MainActivity.TAG_ID);
//        name = getIntent().getStringExtra(MainActivity.TAG_TITLE);
//        isi = getIntent().getStringExtra(MainActivity.TAG_CONTENT);
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
                    saveSQLite();
                    saveApi();
                    Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
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
    private void saveSQLite() {
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
        }
    }

    private void saveApi(){
        String title = String.valueOf(txt_name.getText());
        String date = String.valueOf(txt_date.getText());
        String content = String.valueOf(txt_isi.getText());
        String token = sessionManager.getUserDetail().get("loggedToken");
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        Call<PostPutDelTask> postTaskCall = apiService.postTask(token, title, date, content);
        postTaskCall.enqueue(new Callback<PostPutDelTask>() {
            @Override
            public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {

            }

            @Override
            public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
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
