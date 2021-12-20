package com.example.todolist.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.activity.AddEditActivity;
import com.example.todolist.helper.DBHelper;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.todolist.activity.MainActivity.TAG_DATE;
import static com.example.todolist.activity.MainActivity.TAG_ID;
//import static com.example.todolist.activity.MainActivity.TAG_CONTENT;
//import static com.example.todolist.activity.MainActivity.TAG_TITLE;

public class TaskAdapterApi extends RecyclerView.Adapter<TaskAdapterApi.TaskViewHolder> {
	private List<Task> items;

	public TaskAdapterApi(List<Task> items) {
		this.items = items;
	}

	@Override
	public TaskAdapterApi.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		View view = layoutInflater.inflate(R.layout.list_row, parent, false);
		return new TaskViewHolder(view);
	}


	@Override
	public void onBindViewHolder(TaskAdapterApi.TaskViewHolder holder, int position) {
		holder.id.setText(items.get(position).getId());
		holder.name.setText(items.get(position).getTitle());
		holder.date.setText(items.get(position).getDate());
	}

	@Override
	public int getItemCount() {
		return (items != null) ? items.size() : 0;
	}

	public class TaskViewHolder extends RecyclerView.ViewHolder {
		TextView id, name, date;

		public TaskViewHolder(View itemView) {
			super(itemView);
			id = itemView.findViewById(R.id.id);
			name = itemView.findViewById(R.id.name);
			date = itemView.findViewById(R.id.date);
		}
	}

}
