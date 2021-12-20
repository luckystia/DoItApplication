package com.example.todolist.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTask {
	@SerializedName("status")
	String status;

	@SerializedName("result")
	List<Task> listDataTask;

	@SerializedName("message")
	String message;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Task> getListDataTask() {
		return listDataTask;
	}

	public void setListDataTask(List<Task> listDataTask) {
		this.listDataTask = listDataTask;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
