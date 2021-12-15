package com.example.todolist.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.adapter.RecyclerItemClickListener;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.TaskData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    RecyclerView recyclerView;
    AlertDialog.Builder dialog;
    ArrayList<TaskData> itemList = new ArrayList<>();
    TaskAdapter adapter;
    DBHelper SQLite = new DBHelper(this);
    ImageView imageEditProfile;
    TextView txtUsername;
    FloatingActionButton fab;
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DATE = "date";
    public static final String TAG_ISI = "isi";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        sessionManager = new SessionManager(MainActivity.this);

        //check is login
        if (sessionManager.isLoggedIn() == false){
            moveToLogin();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        imageEditProfile = findViewById(R.id.imageEditProfile);
        imageEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
        txtUsername = findViewById(R.id.txt_username);
        txtUsername.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        fab = findViewById(R.id.floatPlus);
        recyclerView = findViewById(R.id.list);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final String idx = itemList.get(position).getId();
                        final String name = itemList.get(position).getJudul();
                        final String date = itemList.get(position).getDate();
                        final String isi = itemList.get(position).getIsi();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle(name);
                        dialog.setMessage(isi);
                        dialog.setPositiveButton("Edit", (d, id1) -> {
                            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                            intent.putExtra(TAG_ID, idx);
                            intent.putExtra(TAG_NAME, name);
                            intent.putExtra(TAG_DATE, date);
                            intent.putExtra(TAG_ISI, isi);
                            startActivity(intent);
                        });
                        dialog.setNegativeButton("Hapus", (d, id12) -> {
                            DBHelper SQLite = new DBHelper(MainActivity.this);
                            SQLite.deleteTask(Integer.parseInt(idx));
                            itemList.clear();
                            getAllData();
                        });
                        dialog.show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
        SQLite = new DBHelper(this);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
        setUserData();
        getAllData();

    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void setUserData() {
        HashMap<String, String> row = SQLite.getUser();
        String name = row.get("username") == null ? "" : row.get("username");
        if (name.equals("")) {
            txtUsername.setText("User");
        } else {
            txtUsername.setText(name);
        }
    }

    public void getAllData() {
        ArrayList<HashMap<String, String>> row = SQLite.getAllDataTask();
        for (int i = 0; i < row.size(); i++) {
            TaskData taskData = new TaskData();
            taskData.setId(row.get(i).get(TAG_ID));
            taskData.setJudul(row.get(i).get(TAG_NAME));
            taskData.setIsi(row.get(i).get(TAG_ISI));
            taskData.setDate(row.get(i).get(TAG_DATE));
            itemList.add(taskData);
        }
        adapter = new TaskAdapter(itemList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
        itemList.clear();
        getAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Nama : Luckystia Mafasani Ulfa\nNIM : 1905551048");
                builder1.setTitle("ABOUT APP");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}