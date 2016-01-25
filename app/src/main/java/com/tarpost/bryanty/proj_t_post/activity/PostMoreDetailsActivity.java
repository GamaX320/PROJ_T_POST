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
    private CircleImageView rivAvatar;

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
        rivAvatar= (CircleImageView)findViewById(R.id.postDetails_avatar);

        Bundle bundle= getIntent().getExtras();
        Post post= bundle.getParcelable("detailsPost");

        tvUserName.setText(post.getCreatorId());
        tvTitle.setText(post.getTitle());
        tvContent.setText(post.getContent());

        if(post.getImageUrl() != null && !post.getImageUrl().isEmpty()){
            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
            nivImage.setImageUrl(post.getImageUrl(),imageLoader);
        }

    }

}
