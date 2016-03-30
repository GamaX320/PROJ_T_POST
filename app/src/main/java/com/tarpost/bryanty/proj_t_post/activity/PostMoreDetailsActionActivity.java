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
import android.widget.Button;
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
import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.fragment.MyPostsFragment;
import com.tarpost.bryanty.proj_t_post.object.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PostMoreDetailsActionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout imageCover;
    private EditText postTitle,postContent;
    private NetworkImageView imageCoverTemp;
    private ProgressDialog pdProgress;

    private String mode;

    //Image components
    private ImageView imageView;
    private Button btImage;
    private String imageEncode, imageName;
    private Bitmap bitmap;
    private Uri fileUri;

    //http://localhost/tarpost/addInformation.php
    private static final String ADD_POST_URL = "http://projx320.webege.com/tarpost/php/insertPost" +
            ".php";
    private static final String UPDATE_POST_URL = "http://projx320.webege" +
            ".com/tarpost/php/updatePost" +
            ".php";

    private static final String GET_POST_URL = "http://projx320.webege" +
            ".com/tarpost/php/getPost.php";


    //http://projx320.byethost4.com/tarpost/addInformation.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_more_details_action);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbarWithImageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R
                .id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarStyle);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarStyle);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.text_create_post));

        postTitle = (EditText)findViewById(R.id.postTitleAction);
        postContent = (EditText)findViewById(R.id.postContentAction);
        imageCover = (LinearLayout)findViewById(R.id.toolbar_imageBackground);

        Bundle bundle= getIntent().getExtras();
        mode= bundle.getString("mode");

        if(mode.equals("NEW")){
            pdProgress = new ProgressDialog(this);
            pdProgress.setMessage(getResources().getString(R.string.text_dialog_adding));
            pdProgress.setCancelable(false);

        }else if(mode.equals("MODIFY")){
            pdProgress = new ProgressDialog(this);
            pdProgress.setMessage(getResources().getString(R.string.text_dialog_updating));
            pdProgress.setCancelable(false);

            getPost();
        }

    }

    //Upload image
    public void uploadImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                //set the selected image to image view
                //ivCover.setImageBitmap(coverBitmap);
                BitmapDrawable background = new BitmapDrawable(bitmap);
                imageCover.setBackgroundDrawable(background);
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

    public void postAction(View view){
        //Showing progress dialog
        pdProgress.show();

        if(postTitle.getText().toString().equals("")){
            pdProgress.dismiss();
            postTitle.setError(getResources().getString(R.string.text_error_required));
            postTitle.requestFocus();
            return;
        }

        if(postContent.getText().toString().equals("")){
            pdProgress.dismiss();
            postContent.setError(getResources().getString(R.string.text_error_required));
            postContent.requestFocus();
            return;
        }

        if(mode.equals("NEW")){
            addPost();
        }else if(mode.equals("MODIFY")){
            updatePost();
        }


    }

    private void addPost(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_POST_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgress.dismiss();
                postTitle.setText("");
                postContent.setText("");
                imageCover.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_insert_success), Toast
                        .LENGTH_LONG).show();

                Log.d("response", "Response: " + response.toString());

                //on the spot change
                Intent intent = new Intent(PostMoreDetailsActionActivity.this, MainActivity.class);
                intent.putExtra("newPost", true);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgress.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string
                        .text_message_insert_failed), Toast
                        .LENGTH_LONG)
                        .show();

                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil
                        (PostMoreDetailsActionActivity.this.getApplicationContext());
                params.put("creatorId", userUtil.getUserId());
                params.put("title", postTitle.getText().toString());
                params.put("content", postContent.getText().toString());

                //set image param
                if(bitmap != null){
                    params.put("image", getStringImage(bitmap));
                }


                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

    private void updatePost(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_POST_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgress.dismiss();
//                postTitle.setText("");
//                postContent.setText("");
//                imageCover.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_update_success), Toast
                        .LENGTH_LONG).show();

                Log.d("response", "Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgress.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string
                        .text_message_update_failed), Toast
                        .LENGTH_LONG)
                        .show();

                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil
                        (PostMoreDetailsActionActivity.this.getApplicationContext());
                params.put("creatorId", userUtil.getUserId());
                params.put("title", postTitle.getText().toString());
                params.put("content", postContent.getText().toString());

                params.put("postId", post.getPostId().toString());

                //set image param
                if(bitmap != null){
                    params.put("image", getStringImage(bitmap));
                }


                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

    private void getPost(){
        Bundle bundle= getIntent().getExtras();
        post= bundle.getParcelable("detailsPost");

        collapsingToolbarLayout.setTitle(getResources().getString(R.string.text_modify_post));
        postTitle.setText(post.getTitle());
        postContent.setText(post.getContent());

        if(post.getImageUrl() != null && !post.getImageUrl().isEmpty()){
            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
            imageCoverTemp = (NetworkImageView)findViewById(R.id.toolbar_imageBackground_temp);
            imageCoverTemp.setImageUrl(post.getImageUrl(),imageLoader);

            imageLoader.get(post.getImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                                response.getBitmap());
                        imageCover.setBackgroundDrawable(bitmapDrawable);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }else{
            imageCover.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        }

    }

}
