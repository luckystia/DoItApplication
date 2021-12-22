package com.example.todolist.model;

import com.google.gson.annotations.SerializedName;

public class Task {

	@SerializedName("id")
	private String id;
	@SerializedName("title")
	private String title;
	@SerializedName("date")
	private String date;
	@SerializedName("content")
	private String content;
	@SerializedName("completed")
	private String completed;
	@SerializedName("created_at")
	private String created_at;
	@SerializedName("updated_at")
	private String updated_at;
	@SerializedName("user_id")
	private String user_id;

	public Task(){}

	public Task(String id, String title, String date, String content, String completed, String created_at, String updated_at, String user_id) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.content = content;
		this.completed = completed;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.user_id = user_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}
}
