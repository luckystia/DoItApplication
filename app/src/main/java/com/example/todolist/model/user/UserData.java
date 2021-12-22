package com.example.todolist.model.user;

import com.google.gson.annotations.SerializedName;

public class UserData {

	private String loggedToken;

	@SerializedName("old_password")
	private String old_password;

	@SerializedName("new_password")
	private String new_password;

	@SerializedName("password")
	private String password;

	@SerializedName("password_confirmation")
	private String passwordConfirmation;

	@SerializedName("name")
	private String name;

	@SerializedName("username")
	private String username;

	@SerializedName("notif_token")
	private String notif_token;

	public String getLoggedToken() {
		return loggedToken;
	}

	public void setLoggedToken(String loggedToken) {
		this.loggedToken = loggedToken;
	}

	public String getOld_password() {
		return old_password;
	}

	public void setOld_password(String old_password) {
		this.old_password = old_password;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

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

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}
}