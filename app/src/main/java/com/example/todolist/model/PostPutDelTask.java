package com.example.todolist.model;

import com.google.gson.annotations.SerializedName;

public class PostPutDelTask {
	@SerializedName("status")
	String status;

	@SerializedName("result")
	Task task;

	@SerializedName("message")
	String message;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
