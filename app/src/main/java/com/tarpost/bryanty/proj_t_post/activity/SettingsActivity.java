package com.tarpost.bryanty.proj_t_post.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.tarpost.bryanty.proj_t_post.R;

/**
 * Created by BRYANTY on 04-Feb-16.
 */
//public class SettingsActivity extends PreferenceActivity {
//    private Toolbar toolbar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//
//        //initial toolbar
//        toolbar= (Toolbar)findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        Preference button = (Preference)findPreference("Click Me");
//        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                //code for what you want it to do
//                Toast.makeText(getApplication(), "clicked", Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
//    }
//}
public class SettingsActivity extends PreferenceFragment {
    private Toolbar toolbar;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("themeColor", 1);
//        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
//
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                                  String key) {
//                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.text_info_theme),
//                        Toast.LENGTH_LONG)
//                        .show();
//            }
//        });

        Preference button = (Preference)findPreference("clearOfflineData");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}
