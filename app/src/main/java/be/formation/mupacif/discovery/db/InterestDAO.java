package be.formation.mupacif.discovery.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;




public class InterestDAO {

    public static final String TABLE_NAME = "interest";

    public static final String COL_ID ="_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_LOCATION = "date";
    public static final String COL_DESCRIPTION="description";

    public static final String CREATE_TABLE ="CREATE TABLE"+ TABLE_NAME
                                +"("+ COL_ID+"INTEGER PRIMARY KEY AUTOINCREMENT,"
                                +COL_TITLE+" TEXT NOT NULL,"
                                +COL_DATE+" INTEGER NOT NULL,"
                                +COL_LOCATION+" INTEGER,"
                                +COL_DESCRIPTION+" TEXT"
                                +");";

    public static final String UPGRADE_TABLE = "DROP TABLE "+TABLE_NAME;

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
        Cursor c = database.query(TABLE_NAME,null,COL_ID+"="+id,null,null,null,null);
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
        Location place =locationDAO.getLocationById(c.getLong(c.getColumnIndex(COL_LOCATION));
        Interest interest = new Interest(
                c.getLong(c.getColumnIndex(COL_ID)),
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
        database.delete(TABLE_NAME, COL_ID+"="+interest.getId(),null);
    }

    public void close()
    {
        database.close();
        dbHelper.close();
        locationDAO.close();
    }




}
