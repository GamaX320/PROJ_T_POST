package com.tarpost.bryanty.proj_t_post.slide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;

/**
 * Created by BRYANTY on 12-Feb-16.
 */
public class Intro extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(Slide.newInstance(R.layout.fragment_intro1));
        addSlide(Slide.newInstance(R.layout.fragment_intro2));
        addSlide(Slide.newInstance(R.layout.fragment_intro3));
        addSlide(Slide.newInstance(R.layout.fragment_intro4));
    }

    private void loadMainActivity(){
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstTime",false);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        Toast.makeText(getApplicationContext(),
                getString(R.string.text_skip), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
