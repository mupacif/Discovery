package be.formation.mupacif.discovery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.mobsandgeeks.saripaar.annotation.Url;


import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import be.formation.mupacif.discovery.Utils.GeoCodeAsync;
import be.formation.mupacif.discovery.db.LocalDatabase.InterestDAO;
import be.formation.mupacif.discovery.db.LocalDatabase.LocationDAO;
import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;

import static android.provider.BaseColumns._ID;
import static be.formation.mupacif.discovery.Utils.Utils.GeocoderUtil.buildUrl;
import static be.formation.mupacif.discovery.Utils.Utils.GeocoderUtil.getName;
import static be.formation.mupacif.discovery.db.LocalDatabase.InterestDAO.COL_DATE;
import static be.formation.mupacif.discovery.db.LocalDatabase.InterestDAO.COL_DESCRIPTION;
import static be.formation.mupacif.discovery.db.LocalDatabase.InterestDAO.COL_LOCATION;
import static be.formation.mupacif.discovery.db.LocalDatabase.InterestDAO.COL_TITLE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CustomCursorAdapter.EventListener, GoogleApiClient.ConnectionCallbacks,LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private CustomCursorAdapter adapter;
    //in case we have other loaders
    private static final int INTEREST_LOADER_ID = 0;
    private Paint p = new Paint();
    private List<Interest> interests;
    private GoogleApiClient googleApi;
    private android.location.Location position;
    private LocationRequest locationRequest;


//    region onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.rv_interest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomCursorAdapter(this);
        adapter.setEventListener(this);
        recyclerView.setAdapter(adapter);

        googleApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        getSupportLoaderManager().initLoader(INTEREST_LOADER_ID, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                boolean notDeleted = false;
                Snackbar snackbar = Snackbar
                        .make(recyclerView, "EVENT REMOVED", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                adapter.notifyDataSetChanged();
                            }
                        });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            int id = (int) viewHolder.itemView.getTag();

                            ((InterestApplication) getApplication()).getDataManager().delete(id);
                            getSupportLoaderManager().restartLoader(INTEREST_LOADER_ID, null, MainActivity.this);
                        }
                    }
                });
                snackbar.show();


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.argb(255, 170, 255, 0));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                   /*     icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);*/
                    } else {

                        int tmpColor = Math.round((int) ((dX / -2000.0) * 100));
                        p.setColor(Color.argb(255, 172 + tmpColor, 162 - tmpColor, 162 - tmpColor));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        }).attachToRecyclerView(recyclerView);
    }
    //endregion

    /**
     * When user click on fab boutton
     * @param view
     */
    public void onClickAddInterest(View view) {
        Intent intent = new Intent(this, AddInterestActivity.class);
        startActivity(intent);

    }

    /**
     * Async loader for sqlite
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor data = null;

            /**
             * If data already loaded
             */
            @Override
            protected void onStartLoading() {
                //if some data were already loaded
                Log.e(TAG, "Starting of loading");
                if (data != null)
                    deliverResult(data);
                else
                    //new reload
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(InterestDAO.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(Cursor data) {
                Log.e(TAG, "Loading finished, cursor size:" + data.getCount());
                this.data = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        //relaunch the loader to gather all interests
        getSupportLoaderManager().restartLoader(INTEREST_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApi.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApi.isConnected())
            googleApi.disconnect();
    }
    /**
     * When loader finish its work
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.e(TAG, "onLoadFinished: data size:" + data.getCount());
        List<Interest> interestsList = new ArrayList<>();
        while (data.moveToNext()) {
            interestsList.add(getInterestbyCursor(data));
        }
        interests = interestsList;
        data.moveToFirst();
        adapter.swapCursor(data);
    }

    public Interest getInterestbyCursor(Cursor c) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(c.getLong(c.getColumnIndex(COL_DATE)));
        Long idPlace = c.getLong(c.getColumnIndex(COL_LOCATION));
        LocationDAO locationDAO = new LocationDAO(this);
        locationDAO.openReadable();
        Location location = locationDAO.getLocationById(idPlace);
        Interest interest = new Interest(
                c.getLong(c.getColumnIndex(_ID)),
                c.getString(c.getColumnIndex(COL_TITLE)),
                c.getString(c.getColumnIndex(COL_DESCRIPTION)),
                location,
                calendar
        );
        locationDAO.close();
        return interest;
    }

    /**
     * Called when loader is reset
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG, "reset loader");
        adapter.swapCursor(null);
    }


    /***
     * Triggered when user click on recyclerView list
     * @param position elemetn position
     */
    @Override
    public void onInterestClick(int position) {
        Interest interest = interests.get(position);

        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("interest",interest);  //// FIXME: 30-03-17 serialisables seems not be working
        intent.putExtra("title", interest.getTitle());
//        String date = new SimpleDateFormat("EEEE, d MMM yyyy").format(interest.getDate());
//        Toast.makeText(this,"date:"+date,Toast.LENGTH_SHORT).show();
//        intent.putExtra("date", date);
        intent.putExtra("description", interest.getDescription());
        intent.putExtra("location", interest.getLocation().getName());
        startActivity(intent);
    }


    //region geolocalisation
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10000);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi,locationRequest,this);
            // Si on a pas encore la permission mais qu'on est sur une API >= 23
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },1);
        }






        //get last known position

        Log.i(TAG,"connected");
        position = LocationServices.FusedLocationApi.getLastLocation(googleApi);
        Log.i(TAG,"pos:"+position);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        position = location;
        Toast.makeText(this,"location changed pos:"+position,Toast.LENGTH_SHORT).show();

        final URL url = buildUrl(new LatLng(position.getLatitude(),position.getLongitude()));
        GeoCodeAsync geoCodeAsync = new GeoCodeAsync();
        geoCodeAsync.setCallback(new GeoCodeAsync.GeoCodeListener() {
            @Override
            public void getJson(String jsonData) {
                try {
                    Toast.makeText(MainActivity.this,getName(jsonData),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        geoCodeAsync.execute(url);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //endregion

}
