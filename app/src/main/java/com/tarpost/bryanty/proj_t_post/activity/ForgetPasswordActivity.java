package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String email;
    private EditText etEmail;
    private Button btSend;

    private ProgressDialog pdProgress;

    //http://localhost/tarpost/addInformation.php
    private static final String FORGET_PASSWORD_URL = "http://projx320.webege" +
            ".com/tarpost/php/forgotPassword.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        etEmail = (EditText)findViewById(R.id.forgetEmail);
        btSend = (Button)findViewById(R.id.button_send_new_password);

        pdProgress = new ProgressDialog(this);
        pdProgress.setMessage("Reset Password...");
        pdProgress.setCancelable(false);

    }

    public void sendPassword(View v) {

        //Showing progress dialog
        pdProgress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FORGET_PASSWORD_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgress.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_update_password), Toast
                        .LENGTH_LONG).show();

                Log.d("response", "Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgress.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string
                        .text_message_update_password_failed), Toast
                        .LENGTH_LONG)
                        .show();

                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

}
