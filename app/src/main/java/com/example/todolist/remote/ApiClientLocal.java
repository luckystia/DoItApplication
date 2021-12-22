package com.example.todolist.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 2/3/17.
 */

public class ApiClientLocal {
//	public static final String BASE_URL = "http://10.0.2.2:8000/";
	public static final String BASE_URL = "http://apitodolistfix.menkz.xyz/";
	private static Retrofit retrofit = null;
	public static Retrofit getClient() {
		if (retrofit==null) {
			retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		}
		return retrofit;
	}
}