package com.example.todolist.remote;

public class ApiUtils {
    private  ApiUtils(){

    }

    public  static  final String urlApi = "http://apitodolistfix.menkz.xyz/";
    public  static ApiService getUsetService(){
        return  RetrofitClient.getClient(urlApi).create(ApiService.class);
    }
}
