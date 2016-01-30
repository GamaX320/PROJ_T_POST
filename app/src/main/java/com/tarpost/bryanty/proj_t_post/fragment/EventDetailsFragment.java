package com.tarpost.bryanty.proj_t_post.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Event;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BRYANTY on 26-Jan-2016.
 */
public class EventDetailsFragment extends Fragment {

    private TextView eventUsername;
    private CircleImageView eventAvatar;
    private NetworkImageView eventAvatarTemp, eventImage;
    private EditText eventTitle,eventContent,eventStartDateTime, eventEndDateTime,
            eventLocation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_event_details, container, false);

        Bundle bundle= getActivity().getIntent().getExtras();
        Event event= bundle.getParcelable("detailEvent");

        eventUsername = (TextView)view.findViewById(R.id.eventDetails_userName);
        eventAvatar = (CircleImageView)view.findViewById(R.id.eventDetails_avatar);
        eventAvatarTemp = (NetworkImageView)view.findViewById(R.id.eventDetails_tempAvatar);
        eventTitle = (EditText)view.findViewById(R.id.eventDetails_title);
        eventContent = (EditText)view.findViewById(R.id.eventDetails_content);
        eventStartDateTime = (EditText)view.findViewById(R.id.eventDetails_startDateTime);
        eventEndDateTime = (EditText)view.findViewById(R.id.eventDetails_endDateTime);
        eventLocation = (EditText)view.findViewById(R.id.eventDetails_location);
        eventImage= (NetworkImageView)view.findViewById(R.id.eventDetails_image);

        eventUsername.setText(event.getCreatorName());
        if(event.getAvatarUrl() != null && !event.getAvatarUrl().isEmpty()){
            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
            eventAvatarTemp.setImageUrl(event.getAvatarUrl(), imageLoader);

            imageLoader.get(event.getAvatarUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        eventAvatar.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        eventTitle.setText(event.getTitle());
        eventContent.setText(event.getContent());

        if(event.getStartDateTime() != null){
            eventStartDateTime.setText(event.getStartDateTime().toString());
        }

        if(event.getEndDateTime() != null){
            eventEndDateTime.setText(event.getEndDateTime().toString());
        }
        eventLocation.setText(event.getLocation());

        if(event.getImageUrl() != null && !event.getImageUrl().isEmpty()){
            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
            eventImage.setImageUrl(event.getImageUrl(), imageLoader);
        }

        return view;
    }
}
