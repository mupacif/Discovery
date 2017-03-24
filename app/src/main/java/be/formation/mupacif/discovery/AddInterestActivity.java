package be.formation.mupacif.discovery;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import be.formation.mupacif.discovery.databinding.ActivityAddInterestBinding;
import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;

public class AddInterestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ActivityAddInterestBinding dataBindind;
    Place place;
    public static final int PLACE_PICKER_REQUEST = 1;
    private TextView dateTv;
    Calendar calendar;
    TextView locationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        dataBindind = DataBindingUtil.setContentView(this, R.layout.activity_add_interest);
        dateTv = (TextView)findViewById(R.id.tv_addInterest_chooseDate);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
        dateTv.setText(dateFormat.format(calendar.getTime()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onClickToMap(View view) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                dataBindind.btAddinterestAddLocation.setText(place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void save(View view) {

        Location location = new Location(place.getName().toString(), place.getLatLng());
        Interest interest =
                new Interest(
                        dataBindind.etAddinterestTitle.getText().toString(),
                        dataBindind.etAddinterestDescription.getText().toString(),
                        location,
                        calendar);

        Toast.makeText(this, interest + "", Toast.LENGTH_SHORT).show();


        ((InterestApplication) getApplication()).getDataManager().insert(interest);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();

        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        AddInterestActivity.super.onBackPressed();
                    }
                }).create().show();

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void pickDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void dateSet(int year, int month, int dayOfMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMM yyyy");
                dateTv.setText(dateFormat.format(calendar.getTime()));
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");


    }




    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public interface OnDateSetListener
        {
            void dateSet(int year, int month, int dayOfMonth);
        }

        OnDateSetListener dateSetListener;

        public void setDateSetListener(OnDateSetListener dateSetListener)
        {
            this.dateSetListener = dateSetListener;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.i("DatePickerDialog", "date set");

            this.dateSetListener.dateSet(year,month,dayOfMonth);

        }
    }
}
