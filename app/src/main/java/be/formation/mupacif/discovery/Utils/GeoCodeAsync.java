package be.formation.mupacif.discovery.Utils;

import android.os.AsyncTask;

import com.mobsandgeeks.saripaar.annotation.Url;

import java.io.IOException;
import java.net.URL;

import static be.formation.mupacif.discovery.Utils.Utils.GeocoderUtil.getResponseFromHttpUrl;



public class GeoCodeAsync extends AsyncTask<URL,Void,String>{


    private GeoCodeListener callback;
    public interface GeoCodeListener
    {
        public void getJson(String jsonData);
    }
    public void setCallback(GeoCodeListener callback)
    {
        this.callback=callback;
    }
    @Override
    protected String doInBackground(URL... urls) {
        URL searchUrl = urls[0];
        String results = null;
        try {
            results = getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.getJson(result);
    }
}
