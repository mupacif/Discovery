package be.formation.mupacif.discovery;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import be.formation.mupacif.discovery.db.InterestDAO;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    CustomCursorAdapter adapter;
    //in case we have other loaders
    private static final int INTEREST_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_interest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomCursorAdapter(this);
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(INTEREST_LOADER_ID,null,this);
    }
    public void onClickAddInterest(View view)
    {
        Intent intent = new Intent(this,AddInterestActivity.class);
        startActivity(intent);

    }

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
                Log.e(TAG,"Starting of loading");
                if(data!=null)
                        deliverResult(data);
                else
                //new reload
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try
                {
                    return getContentResolver().query(InterestDAO.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                }catch (Exception e)
                {
                    Log.e(TAG,"Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(Cursor data) {
                Log.e(TAG,"Loading finished, cursor size:"+data.getCount());
                this.data = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        //relaunch the loader to gather all interests
        getSupportLoaderManager().restartLoader(INTEREST_LOADER_ID,null,this);
    }

    /**
     * When loader finish its work
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

       Log.e(TAG,"onLoadFinished: data size:"+data.getCount());
        adapter.swapCursor(data);
    }

    /**
     * Called when loader is reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG,"reset loader");
        adapter.swapCursor(null);
    }
}
