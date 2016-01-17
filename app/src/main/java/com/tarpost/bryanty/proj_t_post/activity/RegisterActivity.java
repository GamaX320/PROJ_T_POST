package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private User user;
    private EditText etName, etEmail, etPassword, etPhone, etFaculty, etCourse, etDescription;
    private FloatingActionButton btSubmit;

    //Image components
    private ImageView ivAvatar;
    private ImageView ivCover;
    private Bitmap bitmap;
    private Uri fileUri;

    private ProgressDialog pdProgressAdd;

    private RequestQueue requestQueue;

    //http://localhost/tarpost/insertUserWithEmail.php
    private static final String ADD_USER_URL = "http://projx320.webege" +
            ".com/tarpost/php/insertUserWithEmail.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial input fields
        etName = (EditText)findViewById(R.id.userName);
        etEmail = (EditText)findViewById(R.id.userEmail);
        etPassword = (EditText)findViewById(R.id.userPassword);
        etPhone = (EditText)findViewById(R.id.userPhone);
        etFaculty = (EditText)findViewById(R.id.userFaculty);
        etCourse = (EditText)findViewById(R.id.userCourse);
        etDescription = (EditText)findViewById(R.id.userDescription);

        user = new User();
//        user.setName(etName.getText().toString());
//        user.setEmail(etEmail.getText().toString());
//        user.setPassword(etPassword.getText().toString());
//        user.setPhoneNo(etPhone.getText().toString());
//        user.setFaculty(etFaculty.getText().toString());
//        user.setCourse(etCourse.getText().toString());
//        user.setDescription(etDescription.getText().toString());
        btSubmit = (FloatingActionButton)findViewById(R.id.button_add_user);

        pdProgressAdd = new ProgressDialog(this);
        pdProgressAdd.setMessage("Adding...");
        pdProgressAdd.setCancelable(false);

    }

    public void addUser(final View v) {
        //Showing progress dialog
        pdProgressAdd.show();

        if(etName.getText().toString().equals("")){
            pdProgressAdd.dismiss();
            etName.setError(getResources().getString(R.string.text_error_required));
            etName.requestFocus();
            return;
        }

        if(etEmail.getText().toString().equals("")){
            pdProgressAdd.dismiss();
            etEmail.setError(getResources().getString(R.string.text_error_required));
            etEmail.requestFocus();
            return;
        }

        if(etPassword.getText().toString().equals("")){
            pdProgressAdd.dismiss();
            etPassword.setError(getResources().getString(R.string.text_error_required));
            etPassword.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_USER_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                etName.setText("");
                etEmail.setText("");
                etPassword.setText("");
                etPhone.setText("");
                etFaculty.setText("");
                etCourse.setText("");
                etDescription.setText("");

                Toast.makeText(getApplicationContext(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();

                Log.d("response", "Register Response: " + response.toString());

                //make v final
                Snackbar snackbar = Snackbar.make(v, "Data Inserted Successfully", Snackbar .LENGTH_LONG);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());

                Snackbar snackbar = Snackbar.make(v, "Failed to insert", Snackbar
                        .LENGTH_LONG);

                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               Map<String, String> params = new HashMap<String, String>();
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("password", etPassword.getText().toString());
                params.put("phoneNo", etPhone.getText().toString());
                params.put("faculty", etFaculty.getText().toString());
                params.put("course", etCourse.getText().toString());
                params.put("description", etDescription.getText().toString());

                //set image param
                //params.put("image", getStringImage(bitmap));

               return params;
           }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

}
