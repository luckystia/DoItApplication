package com.example.todolist.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.activity.AddEditActivity;
import com.example.todolist.activity.MainActivity;
import com.example.todolist.helper.CustomDIalog;
import com.example.todolist.helper.DateFormatter;
import com.example.todolist.helper.SessionManager;
import com.example.todolist.model.GetTask;
import com.example.todolist.model.PostPutDelTask;
import com.example.todolist.model.Task;
import com.example.todolist.remote.ApiClientLocal;
import com.example.todolist.remote.ApiService;

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import static com.example.todolist.activity.MainActivity.TAG_CONTENT;
//import static com.example.todolist.activity.MainActivity.TAG_TITLE;

public class TaskAdapterApi extends RecyclerView.Adapter<TaskAdapterApi.TaskViewHolder> {
    ApiService apiService;
    private List<Task> items;
    private SessionManager sessionManager;
    private final String token;
    private final Context context;
    private final CustomDIalog customDIalog;
    private final Boolean active;
    private LinearLayout emptyActive;

    public TaskAdapterApi(Boolean active, Context context) {

        sessionManager = new SessionManager(context);
        this.token = sessionManager.getUserDetail().get("loggedToken");
        this.context = context;
        apiService = ApiClientLocal.getClient().create(ApiService.class);
        this.active = active;
        Activity activity = (Activity) context;
        customDIalog = new CustomDIalog(activity);

//		Toast.makeText(context, "Empty Active Task!", Toast.LENGTH_SHORT).show();

        if (active) {
            customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
            getActiveData();
        } else {
            customDIalog.startAlertDialog("loading", "loading", R.layout.custom_dialog_loading);
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

//		if (items.isEmpty()){
//			Toast.makeText(context, "Empty Active Task!", Toast.LENGTH_SHORT).show();
//		}

//		if (items.get(0).getId().isEmpty()){
//			holder.emptyActiveView.setVisibility(View.VISIBLE);
//		}
//
//		holder.emptyActiveView.setVisibility(View.VISIBLE);

        holder.id.setText(items.get(position).getId());
        holder.name.setText(items.get(position).getTitle());

        try {
            String date = DateFormatter.formatTanggal(items.get(position).getDate());
            holder.date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Boolean checked;
		//			holder.list_row.setVisibility(View.GONE);
		//			holder.rootView.setLayoutParams(holder.params);
		checked = items.get(position).getCompleted().equals("1");
        holder.completed.setChecked(checked);
        holder.completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean rbCheck = holder.completed.isChecked();
                sessionManager = new SessionManager(view.getContext());
                if (rbCheck) {
                    completeTask(sessionManager.getUserDetail().get("loggedToken"), items.get(position).getId());
                    holder.completed.setChecked(true);
                    if (active) {
                        getActiveData();
                        notifyDataSetChanged();
                    } else {
                        getCompletedData();
                        notifyDataSetChanged();
                    }
                } else {
                    uncompleteTask(sessionManager.getUserDetail().get("loggedToken"), items.get(position).getId());
                    holder.completed.setChecked(false);
                    if (active) {
                        getActiveData();
                        notifyDataSetChanged();
                    } else {
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

    public void uncompleteTask(String token, String id) {
        apiService = ApiClientLocal.getClient().create(ApiService.class);
        Call<PostPutDelTask> postUncompletedTask = apiService.uncompleteTask(token, id);
        postUncompletedTask.enqueue(new Callback<PostPutDelTask>() {
            @Override
            public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PostPutDelTask> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void completeTask(String token, String id) {
        apiService = ApiClientLocal.getClient().create(ApiService.class);
        Call<PostPutDelTask> postCompletedTask = apiService.completeTask(token, id);
        postCompletedTask.enqueue(new Callback<PostPutDelTask>() {
            @Override
            public void onResponse(Call<PostPutDelTask> call, Response<PostPutDelTask> response) {
                notifyDataSetChanged();
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

                if (response.code() == 401) {
                    customDIalog.startAlertDialog("dialog_info", "You Are Not Logged In", R.layout.info_layout_dialog);
                } else {
                    customDIalog.dismissDialog();
                    items = response.body().getListDataTask();
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<GetTask> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCompletedData() {
//		apiService = ApiClientLocal.getClient().create(ApiService.class);
//		List<Task> returnTask;
        Call<GetTask> kontakCall = apiService.getTasksCompleted(token);
        kontakCall.enqueue(new Callback<GetTask>() {
            @Override
            public void onResponse(Call<GetTask> call, Response<GetTask>
                    response) {
                if (response.code() == 401) {
                    customDIalog.startAlertDialog("dialog_info", "You Are Not Logged In", R.layout.info_layout_dialog);
                } else {
                    customDIalog.dismissDialog();
                    List<Task> returnItems = response.body().getListDataTask();
                    items = returnItems;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetTask> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
            }
        });
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, date;
        CheckBox completed;
        LinearLayout list_row;
        LinearLayout emptyActive;
//		public LinearLayout.LayoutParams params;
//		public LinearLayout rootView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            completed = itemView.findViewById(R.id.rbCompleted);
            list_row = itemView.findViewById(R.id.list_row_item);
            emptyActive = itemView.findViewById(R.id.emptyActiveView);

        }
    }

}
