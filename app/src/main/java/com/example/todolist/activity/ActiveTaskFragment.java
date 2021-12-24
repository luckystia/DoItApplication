package com.example.todolist.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todolist.R;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.adapter.TaskAdapterApi;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.TaskData;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ActiveTaskFragment extends Fragment {

    public static MainActivity ma;
    private final ArrayList<TaskData> itemList = new ArrayList<>();
    ApiService mApiService;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog;
    private TaskAdapter adapter;
    private final DBHelper SQLite = new DBHelper(getContext());
    private TextView txtUsername, txtTanggal;
    private FloatingActionButton fab;
    private SessionManager sessionManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskAdapterApi mAdapter;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_task_fragment, container, false);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);

        sessionManager = new SessionManager(getContext());

        recyclerView = view.findViewById(R.id.listActive);

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mApiService = ApiClientLocal.getClient().create(ApiService.class);
        if (sessionManager.isLoggedIn() == true) {
//			refresh();
            mAdapter = new TaskAdapterApi(true, getContext());
            recyclerView.setAdapter(mAdapter);

            pullToRefresh.setColorSchemeColors(R.color.dark_blue);
            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mAdapter.getActiveData();
                    pullToRefresh.setRefreshing(false);
                }
            });
        }
        return view;
    }

//	public void refresh() {
//		Call<GetTask> kontakCall = mApiService.getTasksActive(sessionManager.getUserDetail().get("loggedToken"));
//		kontakCall.enqueue(new Callback<GetTask>() {
//			@Override
//			public void onResponse(Call<GetTask> call, Response<GetTask>
//				response) {
//				List<Task> KontakList = response.body().getListDataTask();
//				Log.d("Retrofit Get", "Jumlah data Kontak: " +
//					String.valueOf(KontakList.size()));
//				mAdapter = new TaskAdapterApi(KontakList);
//				recyclerView.setAdapter(mAdapter);
//			}
//
//			@Override
//			public void onFailure(Call<GetTask> call, Throwable t) {
//				Log.e("Retrofit Get", t.toString());
//			}
//		});
//	}
}
