package com.tarpost.bryanty.proj_t_post.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Course;
import com.tarpost.bryanty.proj_t_post.object.Faculty;
import com.tarpost.bryanty.proj_t_post.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CircleImageView userProfile;
    private LinearLayout userCover;
    private EditText userName,userPassword,userEmail, userPhone, userFaculty, userCourse,
            userDescription;
    private ProgressDialog pdProgress;
    private Spinner spFaculty,spCourse;

    private Bitmap avatarBitmap;
    private Bitmap coverBitmap;
    private Uri fileUri;

    private static final String UPDATE_USER_URL = "http://projx320.webege" +
            ".com/tarpost/php/updateUser.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static final String GET_FACULTY = "http://projx320.webege" +
            ".com/tarpost/php/getAllFaculty.php";
    private List<Faculty> facultyList;

    private static final String GET_COURSE = "http://projx320.webege" +
            ".com/tarpost/php/getAllFacultyCourse.php";
    private List<Course> courseList;
    private String facultyId;
    private String courseId;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_action);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbarWithImageViewProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R
                .id.collapsing_toolbar_profile);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarStyle);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarStyle);

        userProfile = (CircleImageView)findViewById(R.id.toolbar_userProfile);
        userCover = (LinearLayout)findViewById(R.id.toolbar_userBackground);
        userName = (EditText)findViewById(R.id.profileUserNameAction);
        userPassword = (EditText)findViewById(R.id.profileUserPasswordAction);
        userEmail = (EditText)findViewById(R.id.profileUserEmailAction);
        userPhone = (EditText)findViewById(R.id.profileUserPhoneAction);
        userFaculty = (EditText)findViewById(R.id.profileUserFacultyAction);
        userCourse = (EditText)findViewById(R.id.profileUserCourseAction);
        userDescription = (EditText)findViewById(R.id.profileUserDescriptionAction);
        spFaculty = (Spinner)findViewById(R.id.profileUserFacultySpinnerAction);
        spCourse = (Spinner)findViewById(R.id.profileUserCourseSpinnerAction);
        facultyList = new ArrayList<Faculty>();
        courseList = new ArrayList<Course>();

        Bundle bundle= getIntent().getExtras();
        user= bundle.getParcelable("editUser");

        pdProgress = new ProgressDialog(this);
        pdProgress.setMessage(getResources().getString(R.string.text_dialog_updating));
        pdProgress.setCancelable(false);

        setValues();
    }

    private void  setValues(){
        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        if(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()){
            NetworkImageView userProfileTemp = (NetworkImageView)findViewById(R.id
                    .toolbar_userProfile_temp);

            userProfileTemp.setImageUrl(user.getAvatarUrl(), imageLoader);
            imageLoader.get(user.getAvatarUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        userProfile.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        //Upload image
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 20);
            }
        });

        if(user.getCoverUrl() != null && !user.getCoverUrl().isEmpty()){
            NetworkImageView userCoverTemp = (NetworkImageView)findViewById(R.id
                    .toolbar_userBackground_temp);

            userCoverTemp.setImageUrl(user.getCoverUrl(), imageLoader);

            imageLoader.get(user.getCoverUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                                response.getBitmap());
                        userCover.setBackgroundDrawable(bitmapDrawable);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        //Upload image
        userCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        collapsingToolbarLayout.setTitle(user.getName());
        userName.setText(user.getName());
        userPassword.setText(user.getPassword());
        userEmail.setText(user.getEmail());
        userPhone.setText(user.getPhoneNo());
//        userFaculty.setText(user.getFaculty());
//        userCourse.setText(user.getCourse());
        userDescription.setText(user.getDescription());

        facultyId = user.getFacultyId();
        courseId = user.getCourseId();

        getFaculty();
        spFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("faculty", "selected faculty id: " + facultyList.get(position).getFacultyId());

                facultyId = facultyList.get(position).getFacultyId();
                courseList = new ArrayList<Course>();
                spCourse.setAdapter(null);
                getCourse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setFacultyData(){
        List<String> labels= new ArrayList<String>();

        for(Faculty faculty : facultyList){
            labels.add(faculty.getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFaculty.setAdapter(spinnerAdapter);

    }

    public void getCourse(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_COURSE+"?facultyId="+facultyId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("course");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Course course = new Course();
                            course.setCourseId(jsonObject.getString("courseId"));
                            course.setFacultyId(jsonObject.getString("facultyId"));
                            course.setName(jsonObject.getString("name"));

                            courseList.add(course);
                        }

                        if(courseList != null && courseList.size() >0){
                            setCourseData();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

    }

    public void setCourseData(){
        List<String> labels= new ArrayList<String>();

        for(Course course : courseList){
            labels.add(course.getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCourse.setAdapter(spinnerAdapter);

        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("faculty", "selected course id: " + courseList.get(position).getCourseId());
                courseId = courseList.get(position).getCourseId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void getFaculty(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_FACULTY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("faculty");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Faculty faculty = new Faculty();
                            faculty.setFacultyId(jsonObject.getString("facultyId"));
                            faculty.setName(jsonObject.getString("name"));

                            facultyList.add(faculty);
                        }

                        if(facultyList != null && facultyList.size() >0){
                            setFacultyData();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

    }

    //Upload image
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
                userCover.setBackgroundDrawable(background);
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
                userProfile.setImageBitmap(avatarBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Upload image
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void updateUser(View view){
        //Showing progress dialog
        pdProgress.show();

        if(userName.getText().toString().equals("")){
            pdProgress.dismiss();
            userName.setError(getResources().getString(R.string.text_error_required));
            userName.requestFocus();
            return;
        }

        if(userEmail.getText().toString().equals("")){
            pdProgress.dismiss();
            userEmail.setError(getResources().getString(R.string.text_error_required));
            userEmail.requestFocus();
            return;
        }

        if(userPassword.getText().toString().equals("")){
            pdProgress.dismiss();
            userPassword.setError(getResources().getString(R.string.text_error_required));
            userPassword.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_USER_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgress.dismiss();
                userName.setText("");
                userEmail.setText("");
                userPassword.setText("");
                userPhone.setText("");
                userFaculty.setText("");
                userCourse.setText("");
                userDescription.setText("");

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_update_success), Toast
                        .LENGTH_LONG).show();

                Log.d("response", "Register Response: " + response.toString());

                Intent intent = new Intent(UserProfileActionActivity.this, UserProfileActivity
                        .class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgress.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string
                        .text_message_update_success), Toast.LENGTH_LONG).show();

                Log.d("response", "Error Response: " + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", user.getUserId());
                params.put("name", userName.getText().toString());
                params.put("email", userEmail.getText().toString());
                params.put("password", userPassword.getText().toString());
                params.put("phoneNo", userPhone.getText().toString());
//                params.put("faculty", userFaculty.getText().toString());
//                params.put("course", userCourse.getText().toString());
                params.put("faculty", facultyId);
                params.put("course", courseId);
                params.put("description", userDescription.getText().toString());

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
