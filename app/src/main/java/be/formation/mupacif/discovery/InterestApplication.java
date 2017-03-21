package be.formation.mupacif.discovery;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import be.formation.mupacif.discovery.db.InterestDAO;
import be.formation.mupacif.discovery.model.Interest;

/**
 * Created by student on 21-03-17.
 */

public class InterestApplication extends Application {
    private DataManager dataManager;

    @Override
    public void onCreate() {
        dataManager = new DataManager(this);
        super.onCreate();

    }

    public final DataManager getDataManager()
    {
        return dataManager;
    }

    public class DataManager
    {
        InterestDAO interestDAO;

        public DataManager(Context context) {
            this.interestDAO = new InterestDAO(context);
        }

        public void insert(Interest i)
        {
            interestDAO.openWritable();
            interestDAO.insert(i);
            interestDAO.close();
        }

/*        public void update(Interest i)
        {
            interestDAO.openWritable();
            interestDAO.update(i);
            interestDAO.close();
        }*/

        public void delete(Interest i)
        {
            interestDAO.openWritable();
            interestDAO.delete(i);
            interestDAO.close();
        }

        public  Cursor getAllInterests()
        {

            Cursor interest = null;
            try {
                interest = interestDAO.getAllInterest();
            }catch (Exception e)
            {
                e.printStackTrace();
                Log.e("Loader","error sql");
            }

            return interest;
        }

        public Interest getInterest(long id)
        {

            interestDAO.openReadable();
            Interest interest = interestDAO.getInterestById(id);
            interestDAO.close();
            return interest;

        }
    }
}
