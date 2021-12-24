package com.example.todolist.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.R;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.DatePickerFragment;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.PostPutDelTask;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView title;
    private EditText txt_id, txt_name, txt_date, txt_isi;
    private Button btn_submit, btn_cancel, btn_delete;
    private final DBHelper SQLite = new DBHelper(this);
    private String id, name, isi, date;
    private final Calendar myCalendar = Calendar.getInstance();
    private SessionManager sessionManager;
    private ApiService apiService;
    private DatePickerDialog datePickerDialog;
    private CustomDIalog customDIalog;
    private Dialog dialog;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_remake);

        sessionManager = new SessionManager(AddEditActivity.this);

        customDIalog = new CustomDIalog(AddEditActivity.this);

        apiService = ApiClientLocal.getClient().create(ApiService.class);

        title = findViewById(R.id.title);
        txt_id = findViewById(R.id.txt_id);
        txt_name = findViewById(R.id.txt_name);
        txt_date = findViewById(R.id.txt_date);
        txt_isi = findViewById(R.id.txt_isi);
        btn_submit = findViewById(R.id.btn_submit);
        btn_delete = findViewById(R.id.btn_delete);
        btn_cancel = findViewById(R.id.btn_cancel);
        id = getIntent().getStringExtra(MainActivity.TAG_ID);
        name = getIntent().getStringExtra(MainActivity.TAG_TITLE);
        isi = getIntent().getStringExtra(MainActivity.TAG_CONTENT);
        date = getIntent().getStringExtra(MainActivity.TAG_DATE);
        backButton = findViewById(R.id.backBtn);

        //check is login
        if (sessionManager.isLoggedIn() == false || sessionManager.getUserDetail().get("loggedToken").isEmpty()) {
      moveToLogin();
        }

        if (id == null || id == "") {
            title.setText("Tambah Data");
            btn_delete.setVisibility(View.GONE);
        } else {
            title.setText("Edit Data");
            txt_id.setText(id);
            txt_name.setText(name);
            txt_isi.setText(isi);
            txt_date.setText(date);
            btn_submit.setText("Edit Task");
        }

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelDate();
        };

        txt_date.setOnClickListener(v -> {
//             TODO Auto-generated method stub
//            new DatePickerDialog(AddEditActivity.this, date, myCalendar
//              .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//              myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        btn_submit.setOnClickListener(v -> {
            try {
                if (txt_id.getText().toString().equals("")) {
                    saveSQLite();
                    saveApi();
                    customDIalog.startAlertDialog("dialog_info", "success add task", R.layout.info_layout_dialog);
                } else {
                    editApi();
                    editSQLite();
                    customDIalog.startAlertDialog("dialog_info", "success edit task", R.layout.info_layout_dialog);
                }
            } catch (Exception e) {
                Log.e("Submit", e.toString());
            }
        });
        btn_cancel.setOnClickListener(v -> {
            blank();
            finish();
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                deleteSQLite();
//                deleteApi();
//                Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                startAlertDialog("dialog_info", "delete confirm", R.layout.confirm_layout_dialog);
            }
        });
    }

    private void moveToLogin() {
        Intent intent = new Intent(AddEditActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
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
        String myFormat = "dd-MM-yyyy"; //In which you need put here
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

    private void saveApi() {
        String title = String.valueOf(txt_name.getText());
        String date = String.valueOf(txt_date.getText());
        String content = String.valueOf(txt_isi.getText());
        String token = sessionManager.getUserDetail().get("loggedToken");
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

    private void editApi() {
        String title = String.valueOf(txt_name.getText());
        String date = String.valueOf(txt_date.getText());
        String content = String.valueOf(txt_isi.getText());
        String token = sessionManager.getUserDetail().get("loggedToken");
        Call<PostPutDelTask> updateTaskCall = apiService.putTask(token, id, title, date, content);
        updateTaskCall.enqueue(new Callback<PostPutDelTask>() {
            @Override
            public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {
//                MainActivity.ma.refresh();
//                finish();
            }

            @Override
            public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Update data in SQLite database
    private void editSQLite() {
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
//            blank();
//            finish();
        }
    }

    private void deleteSQLite() {
        DBHelper SQLite = new DBHelper(AddEditActivity.this);
        SQLite.deleteTask(Integer.valueOf(id));
    }

    private void deleteApi() {
        String token = sessionManager.getUserDetail().get("loggedToken");
        Call<PostPutDelTask> deleteTaskCall = apiService.deleteTask(token, id);
        deleteTaskCall.enqueue(new Callback<PostPutDelTask>() {
            @Override
            public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {
//                MainActivity.ma.refresh();
//                finish();
            }

            @Override
            public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startAlertDialog(String type, String message, Integer view) {
        dialog = new Dialog(this);
        dialog.setContentView(view);

        if (type.equals("dialog_info")) {
            TextView messages = dialog.findViewById(R.id.messages);
            Button btnDelete = dialog.findViewById(R.id.btnDelete);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            if (message.equals("delete confirm")) {
//                prvent cancel the dialog
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                });

                if (message.equals("delete confirm")) {
                    messages.setText("Are you sure want to delete this task?");
                    btnCancel.setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                    btnDelete.setOnClickListener(v -> {
                        deleteSQLite();
                        deleteApi();
                        Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
                dialog.setCanceledOnTouchOutside(false);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                dialog.show();
            }


//    private void showDatePickerDialog(){
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//          this, this,
//          Calendar.getInstance().get(Calendar.YEAR),
//          Calendar.getInstance().get(Calendar.MONTH),
//          Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        );
//        datePickerDialog.show();
//    }
//
//    @Override
//    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//        String date = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
//        if (new SimpleDateFormat("MM/yyyy").parse(date).before(new Date())) {
//
//        }
//    }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        Date now = new Date();

        if (c.getTime().before(now)) {
            customDIalog.startAlertDialog("dialog_info", "error date", R.layout.info_layout_dialog);
        } else {
            txt_date.setText(sdf.format(c.getTime()));
        }

//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText(currentDateString);
    }
}