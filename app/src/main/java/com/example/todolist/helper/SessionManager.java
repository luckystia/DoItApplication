package com.example.todolist.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String LOGGED_TOKEN = "loggedToken";
    private static final String NAME = "name";
    private static final String USERNAME = "username";
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private ApiService apiService;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(UserData userData) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(LOGGED_TOKEN, userData.getLoggedToken());
        editor.putString(NAME, userData.getName());
        editor.putString(USERNAME, userData.getUsername());
        editor.commit();
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(LOGGED_TOKEN, sharedPreferences.getString(LOGGED_TOKEN, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(USERNAME, sharedPreferences.getString(USERNAME, null));

        return user;
    }

    public void logout() {
        apiService = ApiUtils.getUsetService();
        Call<User> call = apiService.logout(getUserDetail().get("loggedToken"));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d("logout", "berhasil");
                }
            }

            @Override
            public void onFailure(Call<User> call1, Throwable throwable) {
                Log.d("logout", "gagal");
            }
        });

        editor.clear();
        editor.commit();
    }

    public Boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }
}
