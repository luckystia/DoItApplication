package com.example.todolist.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.todolist.model.user.UserData;

import java.util.HashMap;

public class SessionManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(UserData userData){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(NAME,userData.getName());
        editor.putString(EMAIL,userData.getEmail());
        editor.putString(USERNAME,userData.getUsername());
        editor.commit();
    }

    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(USERNAME, sharedPreferences.getString(USERNAME, null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }

    public Boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }
}
