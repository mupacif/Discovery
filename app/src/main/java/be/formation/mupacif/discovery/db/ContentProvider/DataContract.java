package be.formation.mupacif.discovery.db.ContentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by student on 20-03-17.
 */

public class DataContract {
    public static final String AUTHORITY  = "be.formation.mupacif.discovery";

    public static final  Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

}
