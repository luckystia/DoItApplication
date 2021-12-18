package com.example.todolist.remote;


import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    //user
    @GET("api/user")
    Call<User> getUsers();
    @POST("api/register")
    Call<User> registerUser(@Body UserData user);
    @POST("api/login")
    Call<User> loginUser(@Body UserData user);
    @PATCH("api/user/{id}")
    Call<User> updateUser(@Path("id") int id,  @Body UserData user);
    @DELETE("api/user/destroy/{id}")
    Call<User> deleteUser(@Path("id") int id);

}
