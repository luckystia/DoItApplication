package com.example.todolist.model.user;

import com.google.gson.annotations.SerializedName;

public class UserData {

	@SerializedName("password")
	private String password;

	@SerializedName("password_confirmation")
	private String passwordConfirmation;

	@SerializedName("name")
	private String name;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	@SerializedName("notif_token")
	private String notif_token;

	public void setNotif_token(String notif_token){
		this.notif_token = notif_token;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setPasswordConfirmation(String passwordConfirmation){
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getPasswordConfirmation(){
		return passwordConfirmation;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}
}