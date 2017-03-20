package be.formation.mupacif.discovery.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;



public class LocationDAO {
    public static final String TABLE_NAME="location";
    public static final String COL_ID="_id";
    public static final String COL_NAME="name";
    public static final String COL_LAT="lat";
    public static final String COL_LNG="lng";

    public static final String CREATE_TABLE = "CREATE TABLE"+ TABLE_NAME
    +"("+ COL_ID+"INTEGER PRIMARY KEY AUTOINCREMENT,"
            +COL_NAME+" TEXT NOT NULL,"
            +COL_LAT+" REAL NOT NULL,"
            +COL_LNG+" REAL NOT NULL"
            +");";

    public static final String UPGRADE_TABLE = "DROP TABLE "+TABLE_NAME;

    Context context;
    SQLiteDatabase database;
    DbHelper dbHelper;

    public LocationDAO(Context context)
    {
        this.context = context;
    }

    public LocationDAO openWritable()
    {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public LocationDAO openReadable()
    {
        dbHelper = new DbHelper(context);
        database = dbHelper.getReadableDatabase();
        return this;
    }

    public long insert(Location location)
    {
        ContentValues cv= new ContentValues();
        cv.put(COL_NAME, location.getName());
        cv.put(COL_LAT, location.getLocation().latitude);
        cv.put(COL_LNG, location.getLocation().longitude);

        return database.insert(TABLE_NAME,null,cv);
    }

    public Cursor getAllLocation()
    {
        Cursor c = database.query(TABLE_NAME,null,null,null,null,null,null);
        if(c!=null)
        {
            c.moveToFirst();
            return c;
        }else
            return null;
    }


    public Cursor getLocationCursorById(long id)
    {
        Cursor c = database.query(TABLE_NAME,null,COL_ID+"="+id,null,null,null,null);
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            return c;
        }else
            return null;
    }

    public Location getLocationById(long id){
        Cursor c= getLocationCursorById(id);
        Location location;
        if(c.getCount()>0)
        {
            location = getLocationByCursor(c);

            return location;
        }

        return null;

    }

    public Location getLocationByCursor(Cursor c)
    {
        Location location = new Location(
                c.getLong(c.getColumnIndex(COL_ID)),
                c.getString(c.getColumnIndex(COL_NAME)),
                new LatLng(c.getDouble(c.getColumnIndex(COL_LAT)),
                c.getDouble(c.getColumnIndex(COL_LNG)))
         );

        return location;
    }


    public void delete(Location location)
    {
        database.delete(TABLE_NAME, COL_ID+"="+location.getId(),null);
    }

    public void close()
    {
        database.close();
        dbHelper.close();
    }

}