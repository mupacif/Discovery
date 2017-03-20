package be.formation.mupacif.discovery.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.Calendar;

import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;

import static be.formation.mupacif.discovery.db.DataContract.BASE_CONTENT_URI;


public class InterestDAO  extends ContentProvider implements BaseColumns{

    public static final String TABLE_NAME = "interest";


    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "date";
    public static final String COL_DESCRIPTION="description";

    public static final String CREATE_TABLE ="CREATE TABLE"+ TABLE_NAME
                                +"("+ _ID+"INTEGER PRIMARY KEY AUTOINCREMENT,"
                                +COL_TITLE+" TEXT NOT NULL,"
                                +COL_DATE+" INTEGER NOT NULL,"
                                +COL_LOCATION+" INTEGER,"
                                +COL_DESCRIPTION+" TEXT"
                                +");";

    public static final String UPGRADE_TABLE = "DROP TABLE "+TABLE_NAME;

    //path for interests directory
    public static final String PATH_INTERESTS = "interests";

    //    region contentProvider data
    //URI for the table through for the Content Resolver
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTERESTS).build();

    public static final int INTEREST = 100;
    public static final int INTEREST_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();



    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, InterestDAO.PATH_INTERESTS, INTEREST);
        uriMatcher.addURI(DataContract.AUTHORITY, InterestDAO.PATH_INTERESTS + "/#", INTEREST_WITH_ID);

        return uriMatcher;
    }


    //endregion

    Context context;
    SQLiteDatabase database;
    DbHelper dbHelper;
    LocationDAO locationDAO;


    public InterestDAO(Context context)
    {
        this.context = context;
        locationDAO = new LocationDAO(context);
    }

    public InterestDAO openWritable()
    {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        locationDAO.openWritable();
        return this;
    }

    public InterestDAO openReadable()
    {
        dbHelper = new DbHelper(context);
        database = dbHelper.getReadableDatabase();
        locationDAO.openReadable();
        return this;
    }

    public long insert(Interest interest)
    {
            ContentValues cv= new ContentValues();
            cv.put(COL_TITLE, interest.getTitle());
            cv.put(COL_DATE, interest.getDate().getTimeInMillis());
            cv.put(COL_LOCATION, locationDAO.insert(interest.getLocation()));
            cv.put(COL_DESCRIPTION, interest.getDescription());
        return database.insert(TABLE_NAME,null,cv);
    }

    /**
     * Insert with a uri
     * @param uri
     * @param interest
     * @return inserted element uri
     */
    public Uri insert(Uri uri,
                      Interest interest)
    {
        ContentValues cv= new ContentValues();
        cv.put(COL_TITLE, interest.getTitle());
        cv.put(COL_DATE, interest.getDate().getTimeInMillis());
        cv.put(COL_LOCATION, locationDAO.insert(interest.getLocation()));
        cv.put(COL_DESCRIPTION, interest.getDescription());
        return insert(uri,cv);
    }


    /**
     * Get all Interests cursor
     * @return cursor with all Intersts within
     */
    public Cursor getAllInterest()
    {
        Cursor c = database.query(TABLE_NAME,null,null,null,null,null,null);
        if(c!=null)
        {
            c.moveToFirst();
            return c;
        }else
            return null;
    }


    public Cursor getInterestCursorById(long id)
    {
        Cursor c = database.query(TABLE_NAME,null,_ID+"="+id,null,null,null,null);
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            return c;
        }else
            return null;
    }

    public Interest getInterestById(long id){
        Cursor c= getInterestCursorById(id);
        Interest movie;
        if(c.getCount()>0)
        {
            movie = getInterestByCursor(c);

            return movie;
        }

        return null;

    }

    public Interest getInterestByCursor(Cursor c)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(c.getLong(c.getColumnIndex(COL_DATE)));
        Location place =locationDAO.getLocationById(c.getLong(c.getColumnIndex(COL_LOCATION)));
        Interest interest = new Interest(
                c.getLong(c.getColumnIndex(_ID)),
                c.getString(c.getColumnIndex(COL_TITLE)),
                c.getString(c.getColumnIndex(COL_DESCRIPTION)),
                place,
                calendar
        );
        locationDAO.close();
        return interest;
    }



    public void delete(Interest interest)
    {
        database.delete(TABLE_NAME, _ID+"="+interest.getId(),null);
    }

    public void close()
    {
        database.close();
        dbHelper.close();
        locationDAO.close();
    }

// region ContendProvider
    @Override
    public boolean onCreate() {
        this.context = getContext();
        locationDAO = new LocationDAO(context);
        dbHelper = new DbHelper(context);
        return true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        openReadable();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case INTEREST:
                retCursor =  database.query(InterestDAO.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        close();
        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        openWritable();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case INTEREST:
                long id = database.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    //endregion
}
