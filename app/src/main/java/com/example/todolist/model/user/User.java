package com.example.todolist.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User{

	@SerializedName("data")
	private UserData userData;

	@SerializedName("message")
	private List<String> message;

	public void setData(UserData userData){
		this.userData = userData;
	}

	public UserData getData(){
		return userData;
	}

	public void setMessage(List<String> message){
		this.message = message;
	}

	public List<String> getMessage(){
		return message;
	}
}