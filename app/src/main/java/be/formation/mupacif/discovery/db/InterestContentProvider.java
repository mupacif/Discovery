package be.formation.mupacif.discovery.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import be.formation.mupacif.discovery.model.Interest;

/**
 * Created by student on 20-03-17.
 */

public class InterestContentProvider extends ContentProvider {
    public static final int INTEREST = 100;
    public static final int INTEREST_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private InterestDAO interestDAO;

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, InterestDAO.PATH_INTERESTS, INTEREST);
        uriMatcher.addURI(DataContract.AUTHORITY, InterestDAO.PATH_INTERESTS + "/#", INTEREST_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        interestDAO = new InterestDAO(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        interestDAO.openWritable();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case INTEREST:
                long id = interestDAO.insert()
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
