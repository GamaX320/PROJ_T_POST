package com.tarpost.bryanty.proj_t_post.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tarpost.bryanty.proj_t_post.activity.LoginActivity;
import com.tarpost.bryanty.proj_t_post.sqlite.DbHelper;

/**
 * Created by BRYANTY on 27-Jan-2016.
 */
public class UserUtil {

    private Boolean login;
    private String userId, userEmail, userName, userAvatar, userCover;

    public UserUtil(){
    }

    //parameter context - getActivity().getApplicationContext()
    public UserUtil(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLogin", Context.MODE_PRIVATE);

        this.login = sharedPreferences.getBoolean("login", false);
        this.userId = sharedPreferences.getString("userId",null);
        this.userEmail = sharedPreferences.getString("userEmail",null);
        this.userName = sharedPreferences.getString("userName",null);
        this.userAvatar = sharedPreferences.getString("userAvatar",null);
        this.userCover = sharedPreferences.getString("userCover",null);
    }

    //Clear all the sessions when user is logout
    public static void userLogout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("login", false);
        editor.putString("userId", null);
        editor.putString("userEmail",null);
        editor.putString("userName", null);
        editor.putString("userAvatar", null);
        editor.putString("userCover", null);
        editor.commit();

        //Delete Sqlite database (Offline sync)
        context.deleteDatabase("tarpost_offline");

        //Navigate to login activity
        Intent intent = new Intent(context, LoginActivity.class);
        //Close all the activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Add new flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserCover() {
        return userCover;
    }

    public void setUserCover(String userCover) {
        this.userCover = userCover;
    }
}
