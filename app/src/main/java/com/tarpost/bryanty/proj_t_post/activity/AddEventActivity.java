package com.tarpost.bryanty.proj_t_post.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventActivity  extends ActionBarActivity {

    private Toolbar toolbar;
    private FloatingActionButton btSubmit;
    private EditText eventLocation;

    private TextView startDateButton, endDateButton, startTimeButton, endTimeButton;
    static final int START_DATE_DIALOG_ID = 100;
    static final int END_DATE_DIALOG_ID = 101;
    static final int START_TIME_DIALOG_ID = 200;
    static final int END_TIME_DIALOG_ID = 201;
    int year_x, month_x, day_x;
    int hour_x,minute_x,second_x;

    private Event event;

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
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            endDateButton.setText(DateUtil.convertYearMonthDayToString(year_x, month_x, day_x));
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            startTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            endTimeButton.setText(DateUtil.convertHourMinuteToString(hour_x, minute_x));
        }
    };

    //Search location latitude and longitude based on name user enter
    public void searchLocation(View view){
        Geocoder geocoder = new Geocoder(AddEventActivity.this, Locale.getDefault());
        List<Address> addresses;
        try{

            addresses = geocoder.getFromLocationName(eventLocation.getText().toString(), 5);

            if(addresses.size() > 0){
                Log.v("location", "location getLatitude > " + addresses.get(0).getLatitude());
                Log.v("location","location getLongitude > "+addresses.get(0)
                        .getLongitude());

                event.setLocationLat(addresses.get(0).getLatitude());
                event.setLocationLng(addresses.get(0).getLongitude());
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
