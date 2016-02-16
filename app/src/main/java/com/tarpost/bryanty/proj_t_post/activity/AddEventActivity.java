package com.tarpost.bryanty.proj_t_post.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddEventActivity  extends ActionBarActivity {

    private Toolbar toolbar;
    private FloatingActionButton btSubmit;
    private EditText eventTitle, eventContent, eventLocation;

    private TextView startDateButton, endDateButton, startTimeButton, endTimeButton;
    static final int START_DATE_DIALOG_ID = 100;
    static final int END_DATE_DIALOG_ID = 101;
    static final int START_TIME_DIALOG_ID = 200;
    static final int END_TIME_DIALOG_ID = 201;
    int year_x, month_x, day_x;
    int hour_x,minute_x,second_x;

    private Event event;
    private Date startDateTime;
    private Date endDateTime;

    //Image components
    private ImageView imageView;
    private Button btImage;
    private String imageEncode, imageName;
    private Bitmap bitmap;
    private Uri fileUri;

    private ProgressDialog pdProgressAdd;

    private static final String ADD_EVENT_URL = "http://projx320.webege" +
            ".com/tarpost/php/insertEvent.php";

    private static final String UPDATE_EVENT_URL = "http://projx320.webege" +
            ".com/tarpost/php/updateEvent.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startDateButton = (TextView)findViewById(R.id.eventStartDate);
        startTimeButton = (TextView)findViewById(R.id.eventStartTime);
        endDateButton = (TextView)findViewById(R.id.eventEndDate);
        endTimeButton = (TextView)findViewById(R.id.eventEndTime);

        eventLocation = (EditText)findViewById(R.id.eventLocation);

        //set default date picker today
        Calendar c = Calendar.getInstance();
        year_x = c.get(Calendar.YEAR);
        month_x = c.get(Calendar.MONTH);
        day_x = c.get(Calendar.DAY_OF_MONTH);

        hour_x = c.get(Calendar.HOUR);
        minute_x = c.get(Calendar.MINUTE);
        second_x = c.get(Calendar.SECOND);

        startDateButton.setText(DateUtil.convertYearMonthDayToString(year_x, month_x, day_x));
        endDateButton.setText(DateUtil.convertYearMonthDayToString(year_x, month_x, day_x));
        startTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));
        endTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));

        event = new Event();
        startDateTime= new Date();
        endDateTime = new Date();

        startDateTime.setYear(startDateTime.getYear()+1900);
        endDateTime.setYear(endDateTime.getYear()+1900);

        //initial components
        eventTitle = (EditText)findViewById(R.id.eventTitle);
        eventContent = (EditText)findViewById(R.id.eventContent);
        imageView = (ImageView)findViewById(R.id.eventImageView);

//        pdProgressAdd = new ProgressDialog(this);
//        pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_adding));
//        pdProgressAdd.setCancelable(false);

        //check mode NEW or MODIFY
        Bundle bundle= getIntent().getExtras();
        mode= bundle.getString("mode");

        if(mode.equals("NEW")){
            pdProgressAdd = new ProgressDialog(this);
            pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_adding));
            pdProgressAdd.setCancelable(false);

        }else if(mode.equals("MODIFY")){
            pdProgressAdd = new ProgressDialog(this);
            pdProgressAdd.setMessage(getResources().getString(R.string.text_dialog_updating));
            pdProgressAdd.setCancelable(false);

            getEvent();
        }

    }

    public void uploadImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() !=
                null) {

            //get the image
            fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

                //set the selected image to image view
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void datePicker(View view){

        if(view.getId() == R.id.eventStartDate){
            showDialog(START_DATE_DIALOG_ID);
        }else if(view.getId() == R.id.eventEndDate){
            showDialog(END_DATE_DIALOG_ID);
        }

    }

    public void timePicker(View view){

        if(view.getId() == R.id.eventStartTime){
            showDialog(START_TIME_DIALOG_ID);
        }else if(view.getId() == R.id.eventEndTime){
            showDialog(END_TIME_DIALOG_ID);
        }

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, startDatePickerListener, year_x, month_x, day_x);

            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, endDatePickerListener, year_x, month_x, day_x);

            case START_TIME_DIALOG_ID:
                return new TimePickerDialog(this, startTimePickerListener,hour_x, minute_x, true);

            case END_TIME_DIALOG_ID:
                return new TimePickerDialog(this, endTimePickerListener,hour_x, minute_x, true);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            startDateButton.setText(DateUtil.convertYearMonthDayToString(year_x, month_x, day_x));
            startDateTime.setYear(year_x);
            startDateTime.setMonth(month_x);
            startDateTime.setDate(day_x);
            Log.v("startDate", "current start date> " + startDateTime);
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            endDateButton.setText(DateUtil.convertYearMonthDayToString(year_x, month_x, day_x));
            endDateTime.setYear(year_x);
            endDateTime.setMonth(month_x);
            endDateTime.setDate(day_x);
            Log.v("endDate", "current end date> " + endDateTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            startTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));
            startDateTime.setHours(hour_x);
            startDateTime.setMinutes(minute_x);
            startDateTime.setSeconds(0);
            Log.v("startDate", "current start time> " + startDateTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            endTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));
            endDateTime.setHours(hour_x);
            endDateTime.setMinutes(minute_x);
            endDateTime.setSeconds(0);
            Log.v("endDate", "current end time> " + endDateTime);
        }
    };

    //Search location latitude and longitude based on name user enter
    public void searchLocation(View view){
        Geocoder geocoder = new Geocoder(AddEventActivity.this, Locale.getDefault());
        final List<Address> addresses;
        try{
            addresses = geocoder.getFromLocationName(eventLocation.getText().toString(), 5);

            if(addresses.size() > 0){
                Log.v("location", "location getLatitude > " + addresses.get(0).getLatitude());
                Log.v("location", "location getLongitude > " + addresses.get(0)
                        .getLongitude());

                final List<String> addressList = new ArrayList<String>();
                for(int i = 0; i < addresses.size() ; i++){
//                    addressList.add(addresses.get(i).getLocality());
                    addressList.add(addresses.get(i).getLocality()+" "
                            +addresses.get(i).getAdminArea()+" "
                            +addresses.get(i).getCountryName()+" "
                            +addresses.get(i).getPostalCode()+" ");
                }

                CharSequence[] charAddresses = addressList.toArray(new CharSequence[addressList.size
                        ()]);

                new MaterialDialog.Builder(this)
                        .title(R.string.text_location)
                        .items(charAddresses)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                event.setLocationLat(addresses.get(which).getLatitude());
                                event.setLocationLng(addresses.get(which).getLongitude());
                                eventLocation.setText(text.toString());
//                                for(int j = 0 ; j < addresses.size() ; j++){
//                                    if(addresses.get(j).getLocality().equals(text)){
//                                        event.setLocationLat(addresses.get(j).getLatitude());
//                                        event.setLocationLng(addresses.get(j).getLongitude());
//
//                                        Log.v("location", "location2 getLatitude > " + addresses
//                                                .get(j).getLatitude());
//                                        Log.v("location", "location2 getLongitude > " + addresses
//                                                .get(j)
//                                                .getLongitude());
//
//                                        eventLocation.setText(addresses.get(j).getLocality().toString());
//                                    }
//                                }
                            }
                        })
                        .positiveText(R.string.text_dialog_confirm_cancel)
                        .show();

            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void eventAction(View view){
        //Showing progress dialog
        pdProgressAdd.show();

        if(eventTitle.getText().toString().equals("")){
            pdProgressAdd.dismiss();
            eventTitle.setError(getResources().getString(R.string.text_error_required));
            eventTitle.requestFocus();
            return;
        }

        if(eventContent.getText().toString().equals("")){
            pdProgressAdd.dismiss();
            eventContent.setError(getResources().getString(R.string.text_error_required));
            eventContent.requestFocus();
            return;
        }

        if(mode.equals("NEW")){
            addEvent();
        }else if(mode.equals("MODIFY")){
            updateEvent();
        }


    }

    //add event onClick listener
//    public void addEvent(View view){
    public void addEvent(){
        pdProgressAdd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_EVENT_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                eventTitle.setText("");
                eventContent.setText("");
                eventLocation.setText("");
                Log.d("response" ,"Add Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil(getApplication().getApplicationContext());

                params.put("creatorId", userUtil.getUserId());
                params.put("title", eventTitle.getText().toString());
                params.put("content", eventContent.getText().toString());

                //set image param
                if(bitmap != null){
                    params.put("image", getStringImage(bitmap));
                }

                params.put("startDateTime", DateUtil.convertDateToStringWithout1900(startDateTime));
                params.put("endDateTime", DateUtil.convertDateToStringWithout1900(endDateTime));

                if(event.getLocationLat() != null){
                    params.put("locationLat", event.getLocationLat().toString());
                }

                if(event.getLocationLng() != null){
                    params.put("locationLng", event.getLocationLng().toString());
                }

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

    public void updateEvent(){
        pdProgressAdd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_EVENT_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdProgressAdd.dismiss();
                eventTitle.setText("");
                eventContent.setText("");
                eventLocation.setText("");

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_message_update_success), Toast
                        .LENGTH_LONG).show();

                finish();
                Log.d("response" ,"Update Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdProgressAdd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to insert", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Reason failed > "+error, Toast
                        .LENGTH_SHORT).show();
                Log.d("response" ,"Error Response: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                UserUtil userUtil = new UserUtil(getApplication().getApplicationContext());

                params.put("creatorId", userUtil.getUserId());
                params.put("eventId", event.getEventId().toString());
                params.put("title", eventTitle.getText().toString());
                params.put("content", eventContent.getText().toString());

                //set image param
                if(bitmap != null){
                    params.put("image", getStringImage(bitmap));
                }

                params.put("startDateTime", DateUtil.convertDateToStringWithout1900(startDateTime));
                params.put("endDateTime", DateUtil.convertDateToStringWithout1900(endDateTime));

                //TODO: set location latitude and longtitude
                if(event.getLocationLat() != null){
                    params.put("locationLat", event.getLocationLat().toString());
                }

                if(event.getLocationLng() != null){
                    params.put("locationLng", event.getLocationLng().toString());
                }

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(stringRequest);
    }

    private void getEvent(){
        Bundle bundle= getIntent().getExtras();
        event= bundle.getParcelable("detailsEvent");

        eventTitle.setText(event.getTitle());
        eventContent.setText(event.getContent());
//        if(event.getStartDateTime() != null){
//            startDateButton.setText(DateUtil.convertYearMonthDayToString(event.getStartDateTime().getYear(),
//                    event.getStartDateTime().getMonth(), event.getStartDateTime().getDate()));
//            startTimeButton.setText(DateUtil.convertHourMinuteToString(event.getStartDateTime()
//                    .getHours(), event.getStartDateTime().getMinutes()));
//            startDateTime = event.getStartDateTime();
//        }
//        if(event.getEndDateTime() != null){
//            endDateButton.setText(DateUtil.convertYearMonthDayToString(event.getEndDateTime().getYear(),
//                    event.getEndDateTime().getMonth(), event.getEndDateTime().getDate()));
//            endTimeButton.setText(DateUtil.convertHourMinuteToString(event.getEndDateTime()
//                    .getHours(), event.getEndDateTime().getMinutes()));
//            endDateTime = event.getEndDateTime();
//
//        }

        if(event.getStartDateTimeStr() != null){
            Date startDateTime = DateUtil.convertStringToDate(event.getStartDateTimeStr());
            startDateButton.setText(DateUtil.convertYearMonthDayToStringWith1900(startDateTime.getYear(),
                    startDateTime.getMonth(), startDateTime.getDate()));
            startTimeButton.setText(DateUtil.convertHourMinuteToString(startDateTime.getHours(),
                    startDateTime.getMinutes()));
            this.startDateTime = startDateTime;
        }

        if(event.getEndDateTimeStr() != null){
            Date endDateTime = DateUtil.convertStringToDate(event.getEndDateTimeStr());
            endDateButton.setText(DateUtil.convertYearMonthDayToStringWith1900(endDateTime.getYear(),
                    endDateTime.getMonth(), endDateTime.getDate()));
            endTimeButton.setText(DateUtil.convertHourMinuteToString(endDateTime.getHours(),
                    endDateTime.getMinutes()));
            this.endDateTime = endDateTime;
        }

        eventLocation.setText(event.getLocation());



    }
}
