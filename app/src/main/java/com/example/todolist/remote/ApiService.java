package com.example.todolist.remote;


import com.example.todolist.model.GetTask;
import com.example.todolist.model.PostPulDelTask;
import com.example.todolist.model.TaskData;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    //user
    //auth
    @POST("api/register")
    Call<User> registerUser(@Body UserData user);
    @DELETE("api/logout")
    Call<User> logout(@Header("Token-Login") String tokenLogin);
    @POST("api/login")
    Call<User> loginUser(@Body UserData user);
    //user
    @GET("api/user")
    Call<User> getUsers();
    @PATCH("api/user/{token}")
    Call<User> updateUser(@Path("token") String tokenLogin, @Body UserData user);
    @PATCH("api/user/notif-api-update/{token}")
    Call<User> updateNotifToken(@Path("token") String tokenLogin,  @Body UserData user);
    @DELETE("api/user/destroy/{id}")
    Call<User> deleteUser(@Path("id") int id);


    //task
    @GET("api/task")
    Call<GetTask> getTasks();

    @FormUrlEncoded
    @POST("api/task")
    Call<PostPulDelTask> postTask(@Field("title") String title,
                                    @Field("date") String nomor,
                                    @Field("content") String content);

    @FormUrlEncoded
    @PUT("api/task/{id}")
    Call<PostPulDelTask> putTask(@Field("id") String id,
                                     @Field("title") String title,
                                     @Field("date") String nomor,
                                     @Field("content") String content);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/task/{id}", hasBody = true)
    Call<PostPulDelTask> deleteTask(@Field("id") String id);


}
