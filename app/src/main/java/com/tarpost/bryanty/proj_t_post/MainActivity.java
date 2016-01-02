package com.tarpost.bryanty.proj_t_post;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tarpost.bryanty.proj_t_post.fragment.BookmarksFragment;
import com.tarpost.bryanty.proj_t_post.fragment.HomeFragment;
import com.tarpost.bryanty.proj_t_post.fragment.MyPostsFragment;
import com.tarpost.bryanty.proj_t_post.fragment.SubscriptionFragment;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_indicator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial drawer layout
        mDrawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);

        //initial navigation drawer layout
        mNavigationView= (NavigationView)findViewById(R.id.nav_view);
        if(mNavigationView!= null){
            //setup navigation drawer content
            setupNavigationDrawer(mNavigationView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch(id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //setup navigation drawer content
    private void setupNavigationDrawer(NavigationView nv){
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                getContent(menuItem.getItemId());
                return true;
            }
        });
    }

    //get selected content
    private void getContent(int position){
        Fragment fragment= null;
        FragmentManager fm= this.getFragmentManager();

        Log.v("position", "selected > "+position);
        switch (position){
            case R.id.navigation_item_home:
                fragment= new HomeFragment();
                break;
            case R.id.navigation_item_post:
                fragment= new MyPostsFragment();
                break;
            case R.id.navigation_item_bookmark:
                fragment= new BookmarksFragment();
                break;
            case R.id.navigation_item_subscription:
                fragment= new SubscriptionFragment();
                break;
        }

        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }
}
