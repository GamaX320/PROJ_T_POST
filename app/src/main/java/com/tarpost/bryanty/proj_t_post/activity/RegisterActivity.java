package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private User user;
    private EditText etName, etEmail, etPassword, etPhone, etFaculty, etCourse, etDescription;
    private FloatingActionButton btSubmit;

    //Image components
    private CircleImageView ivAvatar;
    private LinearLayout ivCover;
    private Bitmap avatarBitmap;
    private Bitmap coverBitmap;
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

        ivAvatar = (CircleImageView)findViewById(R.id.circleImageView_userAvatar);
        ivCover = (LinearLayout)findViewById(R.id.linerLayout_userCover);

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
        pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_adding));
        pdProgressAdd.setCancelable(false);

    }

    public void uploadImage(View v){

        if(v.getId() == R.id.linerLayout_userCover){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

        }else if(v.getId() == R.id.circleImageView_userAvatar){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 20);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() !=
                null) {
            //Cover image
            //get the image
            fileUri = data.getData();
            try {
                coverBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                //set the selected image to image view
                //ivCover.setImageBitmap(coverBitmap);
                BitmapDrawable background = new BitmapDrawable(coverBitmap);
                ivCover.setBackgroundDrawable(background);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == 20 && resultCode == RESULT_OK && data != null && data.getData() !=
                null){
            //Avatar image
            //get the image
            fileUri = data.getData();
            try {
                avatarBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                //set the selected image to image view
                ivAvatar.setImageBitmap(avatarBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void addUser(View v) {
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

                //{"success":0,"message":"Duplicate email found"}
                String result = response.substring(11,12);
                if(result.equals("1")){

                    etName.setText("");
                    etEmail.setText("");
                    etPassword.setText("");
                    etPhone.setText("");
                    etFaculty.setText("");
                    etCourse.setText("");
                    etDescription.setText("");

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_insert_user_success), Toast
                            .LENGTH_LONG)
                            .show();
                }else if(result.equals("0")){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_insert_user_duplicate), Toast
                            .LENGTH_LONG)
                            .show();
                }


                Log.d("response", "Register Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_insert_user_failed), Toast
                        .LENGTH_SHORT)
                        .show();
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());

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
                if(avatarBitmap != null){
                    params.put("avatarPic", getStringImage(avatarBitmap));
                }

                if(coverBitmap != null){
                    params.put("coverPic", getStringImage(coverBitmap));
                }

               return params;
           }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

}
