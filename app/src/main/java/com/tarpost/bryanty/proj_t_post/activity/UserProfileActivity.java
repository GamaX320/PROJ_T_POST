package com.tarpost.bryanty.proj_t_post.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.object.Post;
import com.tarpost.bryanty.proj_t_post.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String userId;
    private CircleImageView userProfile;
    private LinearLayout userCover;
    private EditText userEmail, userPhone, userFaculty, userCourse, userDescription;

    private User user;

    private static final String GET_USER = "http://projx320.webege" +
            ".com/tarpost/php/getUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbarWithImageViewProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();
        userId= bundle.getString("userId");
        //userId= "A000000004";

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R
                .id.collapsing_toolbar_profile);
        collapsingToolbarLayout.setTitle("Sample");
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarStyle);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarStyle);

        userProfile = (CircleImageView)findViewById(R.id.toolbar_userProfile);
        userCover = (LinearLayout)findViewById(R.id.toolbar_userBackground);
        userEmail = (EditText)findViewById(R.id.profileUserEmail);
        userPhone = (EditText)findViewById(R.id.profileUserPhone);
        userFaculty = (EditText)findViewById(R.id.profileUserFaculty);
        userCourse = (EditText)findViewById(R.id.profileUserCourse);
        userDescription = (EditText)findViewById(R.id.profileUserDescription);

        getData();

    }

    private void getData(){
        user = new User();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_USER+"?userId="+userId
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("user");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            user.setUserId(jsonObject.getString("userId"));
                            user.setName(jsonObject.getString("name"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setPassword(jsonObject.getString("password"));
                            user.setPhoneNo(jsonObject.getString("phoneNo"));
                            user.setAvatarUrl(jsonObject.getString("avatarPic"));
                            user.setCoverUrl(jsonObject.getString("coverPic"));
                            user.setFaculty(jsonObject.getString("faculty"));
                            user.setCourse(jsonObject.getString("course"));
                            user.setDescription(jsonObject.getString("description"));
                            user.setStatus(jsonObject.getString("status"));

                            user.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString
                                    ("updateDateTime")));

                            setValues();

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

    private void setValues(){

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

        collapsingToolbarLayout.setTitle(user.getName());
        userEmail.setText(user.getEmail());
        userPhone.setText(user.getPhoneNo());
        userFaculty.setText(user.getFaculty());
        userCourse.setText(user.getCourse());
        userDescription.setText(user.getDescription());
    }

    //Edit button onClick listener
    public void editProfile(View view){
        Intent intent = new Intent(this, UserProfileActionActivity.class);
        intent.putExtra("editUser", user);
        startActivity(intent);
    }

}
