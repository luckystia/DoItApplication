package com.example.todolist.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.todolist.R;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.adapter.TaskAdapterApi;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.TaskData;
import com.example.todolist.remote.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DATE = "date";
    public static final String TAG_ISI = "isi";
    public static final String TAG_CONTENT = "isi";
    public static final String TAG_TITLE = "name";
    public static MainActivity ma;
    private final ArrayList<TaskData> itemList = new ArrayList<>();
    TaskAdapterApi mAdapter;
    ImageView imageEditProfile;
    ApiService mApiService;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private CircleImageView avatar;
    private LinearLayout profileDisplay;
    private AlertDialog.Builder dialog;
    private TaskAdapter adapter;
    private DBHelper SQLite = new DBHelper(this);
    private TextView txtUsername, txtTanggal;
    private FloatingActionButton fab;
    private String avatarUrl;
    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.activeTask:
                    selectedFragment = new ActiveTaskFragment();
                    break;
                case R.id.completedTask:
                    selectedFragment = new CompletedTaskFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        profileDisplay = findViewById(R.id.profileDisplay);
        sessionManager = new SessionManager(MainActivity.this);
        txtUsername = findViewById(R.id.txt_username);
        txtTanggal = findViewById(R.id.txt_tgl);
        avatar = findViewById(R.id.avatar);

        bottomNavigationView = findViewById(R.id.BottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ActiveTaskFragment()).commit();


//        set Date Now
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        txtTanggal.setText(formattedDate);

        //check is login
        if (sessionManager.isLoggedIn() == false || sessionManager.getUserDetail().get("loggedToken").isEmpty()) {
            moveToLogin();
        }

        moveToDetailItemNotif();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        txtUsername.setText("Hello, " + sessionManager.getUserDetail().get("username"));
        profileDisplay.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FormProfileActivity.class));
        });
        avatarUrl = sessionManager.getUserDetail().get("avatar");
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        if (avatarUrl != null) {
            Glide.with(this).load("http://apitodolistfix.menkz.xyz/storage/" + avatarUrl).into(avatar);
        }
        fab = findViewById(R.id.floatPlus);
        recyclerView = findViewById(R.id.listCompleted);

//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
////                        final String idx = itemList.get(position).getId();
////                        final String name = itemList.get(position).getJudul();
////                        final String date = itemList.get(position).getDate();
////                        final String isi = itemList.get(position).getIsi();
////                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
////                        dialog.setTitle(name);
////                        dialog.setMessage(isi);
////                        dialog.setPositiveButton("Edit", (d, id1) -> {
////                            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
////                            intent.putExtra(TAG_ID, idx);
////                            intent.putExtra(TAG_NAME, name);
////                            intent.putExtra(TAG_DATE, date);
////                            intent.putExtra(TAG_ISI, isi);
////                            startActivity(intent);
////                        });
////                        dialog.setNegativeButton("Hapus", (d, id12) -> {
////                            DBHelper SQLite = new DBHelper(MainActivity.this);
////                            SQLite.deleteTask(Integer.parseInt(idx));
////                            itemList.clear();
//////                            getAllData();
////                        });
////                        dialog.show();
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                    }
//                })
//        );
        SQLite = new DBHelper(this);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
//        setUserData();
//        getAllData();

//        mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        mApiService = ApiClientLocal.getClient().create(ApiService.class);
//        ma=this;
//        if (sessionManager.isLoggedIn() == true) {
//            refresh();
//        }
//        refresh();
//        setUserData();
//        getAllData();

    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
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
//        setUserData();
        txtUsername.setText("Hello, " + sessionManager.getUserDetail().get("username"));

        avatarUrl = sessionManager.getUserDetail().get("avatar");
        if (avatarUrl != null) {
            Glide.with(this).load("http://apitodolistfix.menkz.xyz/storage/" + avatarUrl).into(avatar);
        }

        itemList.clear();
//        getAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    public void refresh() {
//        Call<GetTask> kontakCall = mApiService.getTasks(sessionManager.getUserDetail().get("loggedToken"));
//        kontakCall.enqueue(new Callback<GetTask>() {
//            @Override
//            public void onResponse(Call<GetTask> call, Response<GetTask>
//              response) {
//                List<Task> KontakList = response.body().getListDataTask();
//                Log.d("Retrofit Get", "Jumlah data Kontak: " +
//                  String.valueOf(KontakList.size()));
//                mAdapter = new TaskAdapterApi(KontakList);
//                recyclerView.setAdapter(mAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<GetTask> call, Throwable t) {
//                Log.e("Retrofit Get", t.toString());
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sessionManager.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

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

    private void moveToDetailItemNotif() {
        //get notification data info

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = bundle.getString("id");
            if (value != null) {
//                Toast.makeText(MainActivity.this, String.valueOf(bundle.hashCode()), Toast.LENGTH_SHORT).show();
                String id = bundle.getString("id");
                String title = bundle.getString("title");
                String date = bundle.getString("date");
                String content = bundle.getString("content");

                Intent intent = new Intent(this, AddEditActivity.class);
                intent.putExtra(MainActivity.TAG_ID, id);
                intent.putExtra(MainActivity.TAG_TITLE, title);
                intent.putExtra(MainActivity.TAG_DATE, date);
                intent.putExtra(MainActivity.TAG_CONTENT, content);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }


    }
}