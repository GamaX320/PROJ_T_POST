package com.tarpost.bryanty.proj_t_post.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Post;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostMoreDetailsActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TextView tvUserName, tvTitle, tvContent;
    private NetworkImageView nivTempAvatar, nivImage;
    private CircleImageView civAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_more_details);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvUserName= (TextView)findViewById(R.id.postDetails_userName);
        tvTitle= (TextView)findViewById(R.id.postDetails_title);
        tvContent= (TextView)findViewById(R.id.postDetails_content);
        nivImage= (NetworkImageView)findViewById(R.id.postDetails_image);
        nivTempAvatar= (NetworkImageView)findViewById(R.id.postDetails_tempAvatar);
        civAvatar= (CircleImageView)findViewById(R.id.postDetails_avatar);

        Bundle bundle= getIntent().getExtras();
        Post post= bundle.getParcelable("detailsPost");

        tvUserName.setText(post.getCreatorName());
        tvTitle.setText(post.getTitle());
        tvContent.setText(post.getContent());

        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        if(post.getImageUrl() != null && !post.getImageUrl().isEmpty()){
            nivImage.setImageUrl(post.getImageUrl(),imageLoader);
        }

        if(post.getCreatorAvatarUrl() != null && !post.getCreatorAvatarUrl().isEmpty()){

            nivTempAvatar.setImageUrl(post.getCreatorAvatarUrl(),imageLoader);

            imageLoader.get(post.getCreatorAvatarUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        civAvatar.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }else {
            nivTempAvatar.setImageUrl(null, imageLoader);
            civAvatar.setImageResource(R.drawable.avatar);
        }

    }

}
