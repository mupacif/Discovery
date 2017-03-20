package be.formation.mupacif.discovery.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import be.formation.mupacif.discovery.model.Interest;
import be.formation.mupacif.discovery.model.Location;


public class DbHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "Interest.db";
    public static final int DB_VERSION = 2;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(LocationDAO.CREATE_TABLE);
        db.execSQL(InterestDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(InterestDAO.UPGRADE_TABLE);
        db.execSQL(LocationDAO.UPGRADE_TABLE);
        onCreate(db);
    }
}
