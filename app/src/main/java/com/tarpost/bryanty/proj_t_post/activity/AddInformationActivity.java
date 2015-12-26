package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.json.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddInformationActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private String title,content;
    private EditText etInformationTitle, etInformationContent;
    private FloatingActionButton btSubmit;

    private TextView textView;

    private ProgressDialog pdProgressAdd;

    private RequestQueue requestQueue;

    //http://localhost/tarpost/addInformation.php
    private static final String ADD_INFORMATION_URL = "http://projx320.webege.com/tarpost/php/insertPostWithoutImage.php";

    //http://projx320.byethost4.com/tarpost/addInformation.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial input fields
        etInformationTitle = (EditText)findViewById(R.id.informationTitle);
        etInformationContent= (EditText)findViewById(R.id.informationContent);

        title = etInformationTitle.getText().toString();
        content = etInformationContent.getText().toString();

        //initial button
        btSubmit = (FloatingActionButton)findViewById(R.id.button_add_information);

//        //request queue
//        requestQueue = Volley.newRequestQueue(getApplicationContext());
        pdProgressAdd = new ProgressDialog(this);
        pdProgressAdd.setMessage("Adding...");
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

    public void addInformation(View v) {

        //Showing progress dialog
        pdProgressAdd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_INFORMATION_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                etInformationTitle.setText("");
                etInformationContent.setText("");
                Toast.makeText(getApplicationContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),""+title+" - "+content , Toast.LENGTH_SHORT)
                        .show();

                Log.d("response" ,"Register Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("creatorId", "1");
                params.put("title", "asd");
                params.put("content", "asd");

                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }
}
