package be.formation.mupacif.discovery.Utils;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * Created by mupac_000 on 29-03-17.
 */

public class Utils {

    public static String get_SHA_512_SecurePassword(String passwordToHash, String   salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }




    public static String getSecureSalt() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public static class GeocoderUtil
    {
        private static final String JSON_GLOBAL_WRAPPER = "results";
        final static String BASE_URL =
                "https://maps.googleapis.com/maps/api/geocode/json";
        final static String PARAMETER_LATLNG = "latlng";
        final static String APP_KEY_PARAM = "key";
        final static String APP_KEY_VALUE = "AIzaSyB5gGvJl88lqnBNEat_ynBcr8Wycy2qeKA";

        public static URL buildUrl(LatLng latLng) {
            String latlngStr = latLng.latitude+","+latLng.longitude;
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAMETER_LATLNG, latlngStr)
                    .appendQueryParameter(APP_KEY_PARAM, APP_KEY_VALUE)
                    .build();

            URL url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return url;
        }

        public static String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }

        public static String getName(String jsonStr) throws JSONException {
            JSONObject jsonData = new JSONObject(jsonStr);
            String resultJson = jsonData.getJSONObject(JSON_GLOBAL_WRAPPER).getString("formatted_address");
//               String out = new JSONObject(resultJson).getString("formatted_address");

            return resultJson;
        }
    }


}
