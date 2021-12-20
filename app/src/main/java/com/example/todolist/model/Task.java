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

	public Task(){}

	public Task(String id, String title, String date, String content) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.content = content;
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
}
