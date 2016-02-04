package com.tarpost.bryanty.proj_t_post.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.fragment.EventDetailsFragment;
import com.tarpost.bryanty.proj_t_post.fragment.EventDetailsMembersFragment;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.util.ArrayList;
import java.util.List;

public class EventMoreDetailsActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_more_details);

        toolbar = (Toolbar) findViewById(R.id.toolbarWithTab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager= (ViewPager)findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout= (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private void setupViewPager(ViewPager viewPager){
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());

        //Add each tab(fragment) on here
        adapter.addFragment(new EventDetailsFragment(), getResources().getString(R.string
                .text_event_details));
        adapter.addFragment(new EventDetailsMembersFragment(), getResources().getString(R.string
                .text_members));

        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_event);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_member);

    }

    //Tab Pager Adapter (set fragment and title of each tab )
    class TabViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

}
