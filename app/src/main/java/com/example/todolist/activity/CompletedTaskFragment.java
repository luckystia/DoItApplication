package com.example.todolist.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.adapter.TaskAdapterApi;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.GetTask;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskData;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedTaskFragment extends Fragment {

	private RecyclerView recyclerView;
	private AlertDialog.Builder dialog;
	private final ArrayList<TaskData> itemList = new ArrayList<>();
	private TaskAdapter adapter;
	private DBHelper SQLite = new DBHelper(getContext());
	private TextView txtUsername, txtTanggal;
	private FloatingActionButton fab;
	private SessionManager sessionManager;


	ApiService mApiService;
	private RecyclerView.LayoutManager mLayoutManager;
	private TaskAdapterApi mAdapter;
	public static MainActivity ma;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.completed_task_fragment, container, false);

		sessionManager = new SessionManager(getContext());

		recyclerView = view.findViewById(R.id.listCompleted);

		mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		mApiService = ApiClientLocal.getClient().create(ApiService.class);
		if (sessionManager.isLoggedIn() == true) {
//			refresh();
			mAdapter = new TaskAdapterApi(false, getContext());
			recyclerView.setAdapter(mAdapter);
		}
		return view;
	}

//	public void refresh() {
//		Call<GetTask> kontakCall = mApiService.getTasksCompleted(sessionManager.getUserDetail().get("loggedToken"));
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
