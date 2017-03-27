package be.formation.mupacif.discovery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.Toast;

import be.formation.mupacif.discovery.db.InterestDAO;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    CustomCursorAdapter adapter;
    //in case we have other loaders
    private static final int INTEREST_LOADER_ID = 0;
    private Paint p = new Paint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.rv_interest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomCursorAdapter(this);
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(INTEREST_LOADER_ID,null,this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT)
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                boolean notDeleted=false;
                Snackbar snackbar = Snackbar
                        .make(recyclerView, "EVENT REMOVED", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                 adapter.notifyDataSetChanged();
                            }
                        });
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if(event == DISMISS_EVENT_TIMEOUT)
                        {
                            int id = (int) viewHolder.itemView.getTag();

                            ((InterestApplication)getApplication()).getDataManager().delete(id);
                            getSupportLoaderManager().restartLoader(INTEREST_LOADER_ID,null, MainActivity.this);
                        }
                    }
                });
                snackbar.show();


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.argb(255,170,255,0));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                   /*     icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);*/
                    } else {

                        int tmpColor=Math.round((int)((dX/-2000.0)*100));
                        p.setColor(Color.argb(255,172+tmpColor,162-tmpColor,162-tmpColor));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        }).attachToRecyclerView(recyclerView);
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
