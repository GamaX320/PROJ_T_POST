package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.json.JSONParser;
import com.tarpost.bryanty.proj_t_post.json.RequestHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddInformationActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private String title,content;
    private EditText etInformationTitle, etInformationContent;
    private FloatingActionButton btSubmit;

    //Image components
    private ImageView imageView;
    private Button btImage;
    private String imageEncode, imageName;
    private Bitmap bitmap;
    private Uri fileUri;

    private TextView textView;

    private ProgressDialog pdProgressAdd;

    private RequestQueue requestQueue;

    //http://localhost/tarpost/addInformation.php
    private static final String ADD_INFORMATION_URL = "http://projx320.webege.com/tarpost/php/insertPost.php";

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

        btImage = (Button)findViewById(R.id.informationImage);
        imageView= (ImageView)findViewById(R.id.informationImageView);

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

    public void uploadImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() !=
                null) {

            //get the image
            fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                //set the selected image to image view
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("creatorId", "1");
                params.put("title", etInformationTitle.getText().toString());
                params.put("content", etInformationContent.getText().toString());

                //set image param
                params.put("image", getStringImage(bitmap));

                return params;
            }
        };


//        30000 = 30 seconds
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                15000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }
}
