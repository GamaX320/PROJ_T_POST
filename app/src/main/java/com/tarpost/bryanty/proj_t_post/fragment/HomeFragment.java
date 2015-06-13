package com.tarpost.bryanty.proj_t_post.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.adapter.InformationAdapter;
import com.tarpost.bryanty.proj_t_post.object.Information;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView rv_information;
    private InformationAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        //initial recycler view information item
        rv_information= (RecyclerView)view.findViewById(R.id.recyclerViewHome);
//        rv_information.setHasFixedSize(true);
//        adapter= new InformationAdapter(getActivity(), getData());
//        rv_information.setAdapter(adapter);
//
//        rv_information.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupRecyclerView(rv_information);
        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);
        rv.setAdapter( new InformationAdapter(getActivity(), getData()));
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //get all drawer item data
    private List<Information> getData(){
        List<Information> item= new ArrayList<>();
        int[] icons= {R.drawable.ic_home, R.drawable.ic_post, R.drawable.ic_bookmark, R.drawable.ic_subscription, R.drawable.ic_settings};
        String[] titles= getResources().getStringArray(R.array.navigation_drawer_item);
        String[] content= getResources().getStringArray(R.array.navigation_drawer_item);

        for(int a=0; a< icons.length && a< titles.length; a++){
            Information currentItem= new Information();
            currentItem.userAvatar= icons[a];
            currentItem.title= titles[a];
            currentItem.content= content[a];
            item.add(currentItem);
        }

        return item;
    }

}
