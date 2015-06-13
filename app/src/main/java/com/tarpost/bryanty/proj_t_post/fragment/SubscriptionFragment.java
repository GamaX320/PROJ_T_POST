package com.tarpost.bryanty.proj_t_post.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tarpost.bryanty.proj_t_post.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {


    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }


}
