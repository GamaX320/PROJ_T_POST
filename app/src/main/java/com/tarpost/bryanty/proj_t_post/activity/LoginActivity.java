package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LoginActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private String email,password;
    private EditText etEmail, etPassword;
    private Button btLogin;
    private TextView btSignUp;

    //Image components
    private ImageView imageView;
    private Button btImage;
    private String imageEncode, imageName;
    private Bitmap bitmap;
    private Uri fileUri;

    private ProgressDialog pdProgressAdd;

    private RequestQueue requestQueue;

    //http://localhost/tarpost/addInformation.php
    private static final String LOGIN_URL = "http://projx320.webege.com/tarpost/php/checkUserLogin.php";

    //http://projx320.byethost4.com/tarpost/addInformation.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initial input fields
        etEmail = (EditText)findViewById(R.id.email);
        etPassword= (EditText)findViewById(R.id.password);

        //initial button
        btSignUp = (TextView)findViewById(R.id.button_signup);
        btLogin = (Button)findViewById(R.id.button_login);

//        //request queue
        pdProgressAdd = new ProgressDialog(this);
        pdProgressAdd.setMessage("Login...");
        pdProgressAdd.setCancelable(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View v) {

        //Showing progress dialog
        pdProgressAdd.show();

        // Adding request to request queue
        String testUrl = "http://projx320.webege.com/tarpost/php/checkUserLogin2" +
                ".php"+"?email="+etEmail.getText().toString()+"&password="+etPassword.getText().toString();
        JsonObjectRequest jsonRequest = new JsonObjectRequest( testUrl, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            int success = response.getInt("success");

                            if(success == 1){
                                pdProgressAdd.dismiss();
                                etEmail.setText("");
                                etPassword.setText("");
                                Toast.makeText(getApplicationContext(), getResources().getString
                                        (R.string.text_success_login), Toast
                                        .LENGTH_SHORT)
                                        .show();

                                Log.d("response", "Register Response: " + response.toString());
                                //Check user login
                                JSONArray jsonArray = response.getJSONArray("user");
                                Log.d("response", "JSONArray: " + jsonArray.toString());
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                SharedPreferences sharedPreferences = getApplicationContext()
                                        .getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putBoolean("login",true);
                                editor.putString("userId", jsonObject.getString("userId"));
                                editor.putString("userEmail",jsonObject.getString("email"));
                                editor.putString("userName",jsonObject.getString("name"));
                                editor.putString("userAvatar",jsonObject.getString("avatarPic"));
                                editor.putString("userCover",jsonObject.getString("coverPic"));
                                editor.commit();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            }else{
                                //Wrong email or password or no such account
                                String message = response.getString("message");
//                                String message = getResources().getString(R.string
//                                        .text_error_login);

                                if(message.equals("No Items Found")){
                                     message = getResources().getString(R.string
                                        .text_error_login);
                                }

                                pdProgressAdd.dismiss();
                                etEmail.setText("");
                                etPassword.setText("");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                Snackbar.make(getCurrentFocus(), message, Snackbar
                                        .LENGTH_LONG).show();

                                Log.d("response", "Register Response: " + response.toString());
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_SHORT)
                        .show();
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response" ,"Error Response: " + error.toString());
            }
        });

        MyApplication.getInstance().addToReqQueue(jsonRequest);
    }

    public void signUp(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
