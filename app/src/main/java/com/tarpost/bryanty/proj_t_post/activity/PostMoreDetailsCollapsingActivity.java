package com.tarpost.bryanty.proj_t_post.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarpost.bryanty.proj_t_post.R;

import org.w3c.dom.Text;

public class PostMoreDetailsCollapsingActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivAppbarImage;
    private TextView tvTitle, tvContent;

    String title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_more_details_collapsing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWithImageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = "Hello World";
        content = "This is hello world content...";

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R
                .id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarStyle);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarStyle);

        ivAppbarImage = (ImageView)findViewById(R.id.app_bar_with_imageview_post);

        tvTitle = (TextView)findViewById(R.id.postDetailsCollapsing_title);
        tvContent = (TextView)findViewById(R.id.postDetailsCollapsing_content);

        tvTitle.setText(title);
        tvContent.setText(content);
    }

}
