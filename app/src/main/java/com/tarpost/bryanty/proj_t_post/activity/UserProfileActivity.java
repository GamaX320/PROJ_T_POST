package com.tarpost.bryanty.proj_t_post.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Post;
import com.tarpost.bryanty.proj_t_post.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String userId;
    private CircleImageView userProfile;
    private LinearLayout userCover;
    private EditText userEmail, userPhone, userFaculty, userCourse, userDescription;
    private FloatingActionButton userProfileEdit, userProfileFollowing;
    private ProgressDialog pdProgressAdd;

    private User user;

    private static final String GET_USER = "http://projx320.webege" +
            ".com/tarpost/php/getUser.php";

    private static final String ADD_SUBSCRIPTION = "http://projx320.webege" +
            ".com/tarpost/php/insertSubscription.php";

    private static final String DELETE_SUBSCRIPTION = "http://projx320.webege" +
            ".com/tarpost/php/deleteSubscription.php";

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

        userProfileEdit = (FloatingActionButton)findViewById(R.id.profileUserEdit);
        userProfileFollowing = (FloatingActionButton)findViewById(R.id.profileUserFollowing);

        UserUtil userUtil = new UserUtil(this.getApplicationContext());
        //not the current login user account
        if(! userUtil.getUserId().equals(userId)){
            //not current login user (edit button hide)
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams)userProfileEdit
                    .getLayoutParams();
            p.setAnchorId(View.NO_ID);
            userProfileEdit.setLayoutParams(p);
            userProfileEdit.setVisibility(View.GONE);
        }else{
            //is current login user (following button hide)
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams)userProfileFollowing
                    .getLayoutParams();
            p.setAnchorId(View.NO_ID);
            userProfileFollowing.setLayoutParams(p);
            userProfileFollowing.setVisibility(View.GONE);
        }

        getData();

    }

    private void getData(){
        user = new User();

        UserUtil userUtil = new UserUtil(this.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_USER+"?userId="+userId+"&currentUserId="+userUtil.getUserId()
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
                            user.setFollowing(jsonObject.getString("following"));

                            user.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString
                                    ("updateDateTime")));

                            if(user.getPhoneNo().equals("null")){
                                user.setPhoneNo(null);
                            }

                            if(user.getFaculty().equals("null")){
                                user.setFaculty(null);
                            }

                            if(user.getCourse().equals("null")){
                                user.setCourse(null);
                            }

                            if(user.getDescription().equals("null")){
                                user.setDescription(null);
                            }

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

        NetworkImageView userProfileTemp = (NetworkImageView)findViewById(R.id
                .toolbar_userProfile_temp);
        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        if(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()){
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
        }else{
            userProfileTemp.setImageUrl(null, imageLoader);
            userProfile.setImageResource(R.drawable.avatar);
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

        if(user.getFollowing().equals("1")){
            userProfileFollowing.setImageResource(R.mipmap.ic_following_off);
        }else{
            userProfileFollowing.setImageResource(R.mipmap.ic_following_on);
        }
    }

    //Edit button onClick listener
    public void editProfile(View view){
        Intent intent = new Intent(this, UserProfileActionActivity.class);
        intent.putExtra("editUser", user);
        startActivity(intent);
    }

    //Subscribe user button onClick listener
    public void subscribeUser(View view){

        pdProgressAdd = new ProgressDialog(this);
        pdProgressAdd.setCancelable(false);


        if(user.getFollowing().equals("1")){
            //Unfollowing the user
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.text_dialog_confirm_title))
                    .setMessage(getResources().getString(R.string.text_dialog_confirm_content))
                    .setPositiveButton(R.string.text_dialog_confirm_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_removing));
                            pdProgressAdd.show();
                            unFollowingUser();

                        }
                    })
                    .setNegativeButton(R.string.text_dialog_confirm_no, null).show();

        }else{
            //following the user
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.text_dialog_confirm_title))
                    .setMessage(getResources().getString(R.string.text_dialog_confirm_content))
                    .setPositiveButton(R.string.text_dialog_confirm_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_adding));
                            pdProgressAdd.show();
                            followingUser();

                        }
                    })
                    .setNegativeButton(R.string.text_dialog_confirm_no, null).show();
        }

    }

    private void followingUser(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_SUBSCRIPTION
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                userProfileFollowing.setImageResource(R.mipmap.ic_following_off);

                Toast.makeText(UserProfileActivity.this,getResources().getString(R.string
                        .text_message_subscribe),Toast.LENGTH_LONG).show();

                Log.d("response", "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(UserProfileActivity.this,getResources().getString(R.string
                        .text_message_insert_failed),Toast.LENGTH_LONG).show();

                Log.d("response", "Error Response: " + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil(getApplication().getApplicationContext());

                params.put("userId", userUtil.getUserId());
                params.put("subscripId", userId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);

    }

    private void unFollowingUser(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_SUBSCRIPTION
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                userProfileFollowing.setImageResource(R.mipmap.ic_following_on);

                Toast.makeText(UserProfileActivity.this,getResources().getString(R.string
                        .text_message_unsubscribe),Toast.LENGTH_LONG).show();

                Log.d("response", "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(UserProfileActivity.this,getResources().getString(R.string
                        .text_message_update_failed),Toast.LENGTH_LONG).show();

                Log.d("response", "Error Response: " + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil(getApplication().getApplicationContext());

                params.put("userId", userUtil.getUserId());
                params.put("subscripId", userId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);

    }

}
