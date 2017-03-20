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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_interest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomCursorAdapter(this);
        recyclerView.setAdapter(adapter);
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
        };
    }

    /**
     * When loader finish its work
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    /**
     * Called when loader is reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
    }
}
