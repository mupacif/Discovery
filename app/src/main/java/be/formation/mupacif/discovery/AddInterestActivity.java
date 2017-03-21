package be.formation.mupacif.discovery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import be.formation.mupacif.discovery.databinding.ActivityAddInterestBinding;
import be.formation.mupacif.discovery.db.InterestDAO;
import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;

public class AddInterestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ActivityAddInterestBinding dataBindind;
    Place place;
    public static final int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        dataBindind = DataBindingUtil.setContentView(this, R.layout.activity_add_interest);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


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

    public void onClickToMap(View view)
    {

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
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
    public void save(View view)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(dataBindind.dpAddinterestDate.getYear(),dataBindind.dpAddinterestDate.getMonth(),dataBindind.dpAddinterestDate.getDayOfMonth());
        Location location = new Location(place.getName().toString(), place.getLatLng());
        Interest interest =
                new Interest(
                        dataBindind.etAddinterestTitle.getText().toString(),
                        dataBindind.etAddinterestDescription.getText().toString(),
                        location,
                calendar);

        Toast.makeText(this,interest+"",Toast.LENGTH_SHORT).show();

        ((InterestApplication)getApplication()).getDataManager().insert(interest);
        finish();
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
}
