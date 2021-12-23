package com.example.todolist.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.activity.AddEditActivity;
import com.example.todolist.activity.MainActivity;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.helper.DateFormatter;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.GetTask;
import com.example.todolist.model.PostPutDelTask;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskData;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.todolist.activity.MainActivity.TAG_DATE;
import static com.example.todolist.activity.MainActivity.TAG_ID;
import static com.example.todolist.activity.MainActivity.ma;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import static com.example.todolist.activity.MainActivity.TAG_CONTENT;
//import static com.example.todolist.activity.MainActivity.TAG_TITLE;

public class TaskAdapterApi extends RecyclerView.Adapter<TaskAdapterApi.TaskViewHolder> {
	ApiService apiService;
	private List<Task> items;
	private SessionManager sessionManager;
	private String token;
	private Context context;
	private Boolean active;

	public TaskAdapterApi(Boolean active, Context context) {

			sessionManager = new SessionManager(context);
			this.token = sessionManager.getUserDetail().get("loggedToken");
			this.context = context;
			apiService =  ApiClientLocal.getClient().create(ApiService.class);
			this.active = active;

		if (active){
			getActiveData();
		}else{
			getCompletedData();
		}
//		this.items = items;
	}

	@Override
	public TaskAdapterApi.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		View view = layoutInflater.inflate(R.layout.list_row, parent, false);
		return new TaskViewHolder(view);
	}


	@Override
	public void onBindViewHolder(TaskAdapterApi.TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
		holder.id.setText(items.get(position).getId());
		holder.name.setText(items.get(position).getTitle());

		try {
			String date = DateFormatter.formatTanggal(items.get(position).getDate());
			holder.date.setText(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Boolean checked;
		if(items.get(position).getCompleted().equals("1")){
			checked = true;
//			holder.list_row.setVisibility(View.GONE);
//			holder.rootView.setLayoutParams(holder.params);
		}else{
			checked = false;
		}
		holder.completed.setChecked(checked);
		holder.completed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Boolean rbCheck = holder.completed.isChecked();
				sessionManager = new SessionManager(view.getContext());
				if (rbCheck){
					completeTask(sessionManager.getUserDetail().get("loggedToken"), items.get(position).getId());
					holder.completed.setChecked(true);
					if (active){
						getActiveData();
						notifyDataSetChanged();
					}else{
						getCompletedData();
						notifyDataSetChanged();
					}
				}else {
					uncompleteTask(sessionManager.getUserDetail().get("loggedToken") ,items.get(position).getId());
					holder.completed.setChecked(false);
					if (active){
						getActiveData();
						notifyDataSetChanged();
					}else{
						getCompletedData();
						notifyDataSetChanged();
					}
				}

			}
		});
		holder.list_row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent toUpdate = new Intent(view.getContext(), AddEditActivity.class);
				toUpdate.putExtra(MainActivity.TAG_ID, items.get(position).getId());
				toUpdate.putExtra(MainActivity.TAG_TITLE, items.get(position).getTitle());
				toUpdate.putExtra(MainActivity.TAG_DATE, items.get(position).getDate());
				toUpdate.putExtra(MainActivity.TAG_CONTENT, items.get(position).getContent());
				view.getContext().startActivity(toUpdate);
			}
		});
	}

	@Override
	public int getItemCount() {
		return (items != null) ? items.size() : 0;
	}

	public class TaskViewHolder extends RecyclerView.ViewHolder {
		TextView id, name, date;
		CheckBox completed;
		LinearLayout list_row;
//		public LinearLayout.LayoutParams params;
//		public LinearLayout rootView;

		public TaskViewHolder(View itemView) {
			super(itemView);
			id = itemView.findViewById(R.id.id);
			name = itemView.findViewById(R.id.name);
			date = itemView.findViewById(R.id.date);
			completed = itemView.findViewById(R.id.rbCompleted);
			list_row = itemView.findViewById(R.id.list_row_item);

//			params = new LinearLayout.LayoutParams(0, 0);
//			rootView = itemView.findViewById(R.id.list_row_item);
		}
	}

	public void uncompleteTask(String token, String id) {
		apiService = ApiClientLocal.getClient().create(ApiService.class);
		Call<PostPutDelTask> postUncompletedTask= apiService.uncompleteTask(token, id);
		postUncompletedTask.enqueue(new Callback<PostPutDelTask>() {
			@Override
			public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {

			}

			@Override
			public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void completeTask(String token, String id) {
		apiService = ApiClientLocal.getClient().create(ApiService.class);
		Call<PostPutDelTask> postCompletedTask= apiService.completeTask(token, id);
		postCompletedTask.enqueue(new Callback<PostPutDelTask>() {
			@Override
			public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {

			}

			@Override
			public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void getActiveData() {
//		apiService = ApiClientLocal.getClient().create(ApiService.class);
		Call<GetTask> kontakCall = apiService.getTasksActive(token);
		kontakCall.enqueue(new Callback<GetTask>() {
			@Override
			public void onResponse(Call<GetTask> call, Response<GetTask>
				response) {
				items = response.body().getListDataTask();
				notifyDataSetChanged();
			}

			@Override
			public void onFailure(Call<GetTask> call, Throwable t) {
				Log.e("Retrofit Get", t.toString());
				Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getCompletedData() {
//		apiService = ApiClientLocal.getClient().create(ApiService.class);
//		List<Task> returnTask;
		Call<GetTask> kontakCall = apiService.getTasksCompleted(token);
		kontakCall.enqueue(new Callback<GetTask>() {
			@Override
			public void onResponse(Call<GetTask> call, Response<GetTask>
				response) {
				List<Task> returnItems = response.body().getListDataTask();
				items = returnItems;
				notifyDataSetChanged();
			}

			@Override
			public void onFailure(Call<GetTask> call, Throwable t) {
				Log.e("Retrofit Get", t.toString());
			}
		});
	}

}
