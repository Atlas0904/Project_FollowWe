package com.as.atlas.googlemapfollowwe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by atlas on 2016/7/13.
 */
public class Utils {

    private static final String TAG = Utils.class.getName();
    private static final String ENCODE_UTF8 = "utf-8";
    private static final String PREFIX_GOOGLE_MAP_API_FOR_ADDRESS = "http://maps.google.com.tw/maps/api/geocode/json?address=";
    private static final String PREFIX_GOOGLE_MAP_API_FOR_PLACE_ID = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PREFIX_GOOGLE_MAP_API_FOR_DURATION = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String RESPONSE_STATUS = "status";
    private static final String RESPONSE_STATUS_OK = "OK";
    private static final String RESPONSE_RESULTS = "results";
    private static final String RESPONSE_GEOMETRY = "geometry";
    private static final String RESPONSE_LOCATION = "location";
    private static final String RESPONSE_LOCATION_LAT = "lat";
    private static final String RESPONSE_LOCATION_LNG = "lng";
    private static final String RESPONSE_PlACEID = "place_id";
    private static final int OUTSTREAM_BUFFER_SIZE = 1024;

    public static String getPlaceIdFromGoogleMapAPI(LatLng latLng, int radiusMeter, String type, String name) {
        // Ex: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=AIzaSyAwwa6RWndLD4cOld2QNkoo8Tte9mJveFo
        // https://developers.google.com/places/place-id?hl=zh-tw
        // 08-11 11:32:05.408 22649-26315/com.as.atlas.googlemapfollowwe D/com.as.atlas.googlemapfollowwe.Utils: getPlaceIdFromGoogleMapAPI url:https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=AIzaSyA7wFTdRKCHaYbfuqLrk3uLa_KdlERsIaI&sensor=true
        // 08-11 11:32:06.198 22649-26315/com.as.atlas.googlemapfollowwe D/com.as.atlas.googlemapfollowwe.Utils: getPlaceIdFromGoogleMapAPI: {"html_attributions":[],"results":[{"geometry":{"location":{"lat":-33.86755700000001,"lng":151.201527},"viewport":{"northeast":{"lat":-33.86752310000001,"lng":151.2020721},"southwest":{"lat":-33.8675683,"lng":151.2013453}}},"icon":"https:\/\/maps.gstatic.com\/mapfiles\/place_api\/icons\/generic_business-71.png","id":"ce4ffe228ab7ad49bb050defe68b3d28cc879c4a","name":"Sydney Showboats","opening_hours":{"open_now":true,"weekday_text":[]},"photos":[{"height":750,"html_attributions":["<a href=\"https:\/\/maps.google.com\/maps\/contrib\/107415973755376511005\/photos\">Sydney Showboats<\/a>"],"photo_reference":"CoQBcwAAAOD3vuQdE6Id7HEKYw7ZHj7yCcFWryk0Rpm6PWi6_rtzaQ0yddDZVOuKcEE4xee4Lk3uDtYd2ilr0G9S_sDu0NUphabLhV9d-K0ooHbuKRO8t3aWcZ6s7J57uMRWXy0edsRCGG11-Rp840J4fHBRsol5yZG7Pm8ad_PLXtiyeCbaEhBnQQIeFK8jSlUx8m3xP6B6GhT9hAV_dDz8E2arQQoIOSRGmggYkw","width":1181}],"place_id":"ChIJjRuIiTiuEmsRCHhYnrWiSok","rating":4.3,"reference":"CnRkAAAA9SgqLOkbrrs2hABg3tpoMXnmxqlDHUBPt2ysS5DrjapW_TolqNWczOgnIYl6f12EuxSOXg99zivhMvAWjpsChT9mwLgPWQYJFctgi_sRJceHca3-T6_DtB09_qOXl0kHLFDg0GjaXwQa_H6Yk784IhIQKSbS_hRhOJoplWpkIftLyhoUzGsDe_tweTZ6JAc8132sqgLD8mA","scope":"GOOGLE","types":["travel_agency","restaurant","food","point_of_interest","establishment"],"vicinity":"King Street Wharf 5, Lime Street, Sydney"},{"geometry":{"location":{"lat":-33.867591,"lng":151.201196}},"icon":"https:\/\/maps.gstatic.com\/mapfiles\/place_api\/icons\/generic_business-71.png","id":"a97f9fb468bcd26b68a23072a55af82d4b325e0d","name":"Australian Cruise Group","opening_hours":{"open_now":true,"weekday_text":[]},"photos":[{"height":328,"html_attributions":["<a href=\"https:\/\/maps.google.com\/maps\/contrib\/110751364053842618118\/photos\">Australian Cruise Group<\/a>"],"photo_reference":"CoQBcwAAAEgoQOLBWDh7khIrQ3pUmwVJlFUf76cdI24yUipirImAnytUKljUx_GhKi_9OBF5ruZNLe1Ddb4NEzNSlAMbg-yjG2ALewYMoU8xdi4A3BFga2-NY91DviH6j_QVmDD1vd9Woj3gtJKYmhj1O8TNZVz4AXo6ZIyx6eCj2vASSXieEhBQl4HNsbR-oB0eF_rbTPFLGhQsq09nFO7TCzmSx2rJGOWz8BOvfQ","width":329}],"place_id":"ChIJrTLr-GyuEmsRBfy61i59si0","reference":"CnRqAAAAgVUbdL70-iUGf_BffwI3Op-qa2o4RcLimxvEuz6dY4rw7YDIHqDE0NuWjeZ5TG622cfDQsZRNgoiHn-ZXp3REqxubEDmLpuc-Iga3IzD4YnX062kx28og0QXXdcB2v3jWxMS9-A5xw5y72o5xQk2whIQcDDw-Aq8pDi6wUeHGEI9UBoUkQ1o39hEahSN7ntTvg1H30dm2i0","scope":"GOOGLE","types":["travel_agency","restaurant","food","point_of_interest","establishment"],"vicinity":"32 The Promenade, King Street Wharf 5, Sydney"},{"geometry":{"location":{"lat":-33.8686058,"lng":151.2018207},"viewport":{"northeast":{"lat":-33.8684986,"lng":151.20212835},"southwest":{"lat":-33.8689274,"lng":151.20171815}}},"icon":"https:\/\/maps.gstatic.com\/mapfiles\/place_api\/icons\/generic_business-71.png","id":"21a0b251c9b8392186142c798263e289fe45b4aa","name":"Rhythmboat Cruises","photos":[{"height":480,"html_attributions":["<a href=\"https:\/\/maps.google.com\/maps\/contrib\/104066891898402903288\/photos\">Rhythmboat Cruises<\/a>"],"photo_reference":"CoQBcwAAAKik6e90-l5sUMlgT9490RVYLdAQUrIAMbhNI1IOY_6_vnGU8AoALb7XuzFrbd4UWhDrkUWl3PuhC4QJD3mNtDu2itp6330w_lNLPk5isbbxfK59vO8eP4N9hS8TtA464XZGOyQDn_G0CDRO-cUo9X13k0Iy1jzyWo96jqTN2l54EhAQebmJvjWVa2Mt3n7P3HOGGhQtFROsj85B3hx4o8bOw1bD1Dw0rA","width":640}],"place_id":"ChIJyWEHuEmuEmsRm9hTkapTCrk","rating":4.2,"reference":"CnRmAAAAuyjsLx3XstUJNqAK0S0oD1O7kbJjE6Y1l8UbbRa5NYcRZgoioZ3yvZIppHJzL-mZqtEa4ANh1G1-g2gcoMniJcgu1i8QD0rEg1HpSFo-bp3gJn3Q5tTyp7JKv3KCqveoJAp56J2LW6RH-YyiHx3ohRIQS4y3mabZEv2Xi3lkqy18ZxoUtj8xhhSYAzngDIjD90gfe-GNHDs","scope":"GOOGLE","types":["travel_agency","restaurant","food","point_of_interest","establishment"],"vicinity":"King Street Wharf, King Street, Sydney"},{"geometry":{"location":{"lat":-33.8709434,"lng":151.1903114},"viewport":{"northeast":{"lat":-33.87039965,"lng":151.1906609},"southwest":{"lat":-33.87112465000001,"lng":151.1901949}}},"icon":"https:\/\/maps.gstatic.com\/mapfiles\/place_api\/icons\/generic_business-71.png","id":"3458f23c154e574552e0722773a46f384816b241","name":"Vagabond Cruises","opening_hours":{"open_now

        String key = "AIzaSyA7wFTdRKCHaYbfuqLrk3uLa_KdlERsIaI";
        String url = PREFIX_GOOGLE_MAP_API_FOR_PLACE_ID + "location=" + latLng.latitude + "," + latLng.longitude +
                "&radius=" + radiusMeter +
                "&type=" + type +
                "&name=" + name +
                "&key=" + key +
                "&sensor=true";
        Log.d(TAG, "getPlaceIdFromGoogleMapAPI url:" + url);
        byte[] bytes = Utils.urlToByte(url);
        try {
            JSONObject obj = new JSONObject(new String(bytes));
            Log.d(TAG, "getPlaceIdFromGoogleMapAPI: " + obj.toString());

            if (obj.getString(RESPONSE_STATUS).equals(RESPONSE_STATUS_OK)) {
                JSONObject location = obj.getJSONArray(RESPONSE_RESULTS)
                        .getJSONObject(0)
                        .getJSONObject(RESPONSE_GEOMETRY)
                        .getJSONObject(RESPONSE_LOCATION);

                double lat = location.getDouble(RESPONSE_LOCATION_LAT);
                double lng = location.getDouble(RESPONSE_LOCATION_LNG);

                String placeId = obj.getJSONArray(RESPONSE_RESULTS)
                        .getJSONObject(0)
                        .getString(RESPONSE_PlACEID);

                return placeId;
            }

        } catch (JSONException e) {
            Log.d(TAG, "getPlaceIdFromGoogleMapAPI Exception e:" + e);
            e.printStackTrace();
        }

        return "";
    }

    /*
    Example:
    https://maps.googleapis.com/maps/api/distancematrix/json?
    units=metric
    &mode=walking
    &origins=館前東路26號
    &destinations=新店中興路3段88號
    &key=AIzaSyCv7_YK7RSB6x-2Ad5uJepXBocohE3YoWM

    units=metric (default) returns distances in kilometers and meters.
    units=imperial returns distances in miles and feet.
    */
    public static Place getDurationOfTravel(String mode, LatLng from, LatLng to) {
        Place place = new Place();

//        try {
//            from = URLEncoder.encode(from, ENCODE_UTF8);
//            to = URLEncoder.encode(to, ENCODE_UTF8);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        String key = "AIzaSyD2VBdHH2ad0xfhnvEXDF62XxRm4s-KD58";
        String url = PREFIX_GOOGLE_MAP_API_FOR_DURATION +
                "units=metric&" +
                "&mode=" + mode +
                "&origins=" + from.latitude + "," + from.longitude +
                "&destinations=" + to.latitude + "," + to.longitude +
                "&key=" + key;
        Log.d(TAG, "getDurationOfTravel: url=" + url);
        byte[] bytes = Utils.urlToByte(url);
        try {
            JSONObject obj = new JSONObject(new String(bytes));
            Log.d(TAG, "getDurationOfTravel: obj" + obj.toString());

            if (obj.getString(RESPONSE_STATUS).equals(RESPONSE_STATUS_OK)) {
                JSONObject duration = obj.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("duration");

                String durationString  = duration.getString("text");
                int durationInt = duration.getInt("value");

                JSONObject distance = obj.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance");
                String distanceString = distance.getString("text");
                int distanceInt = distance.getInt("value");

                place.duration = durationInt;
                place.distance = distanceInt;

            }

        } catch (JSONException e) {
            Log.d(TAG, "getDurationOfTravel Exception e:" + e);
            e.printStackTrace();
        }

        Log.d(TAG, "getDurationOfTravel: place=" + place);
        return place;

    }

    public static double[] getLatLngFromGoogleMapAPI(String addr) {

        try {
            addr = URLEncoder.encode(addr, ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String apiUrl = PREFIX_GOOGLE_MAP_API_FOR_ADDRESS + addr;
        byte[] bytes = Utils.urlToByte(apiUrl);

        if (bytes == null)  return null;

        try {
            JSONObject obj = new JSONObject(new String(bytes));
            Log.d("Atlas", "getLatLngFromGoogleMapAPI() obj:" + obj);

            if (obj.getString(RESPONSE_STATUS).equals(RESPONSE_STATUS_OK)) {
                JSONObject location = obj.getJSONArray(RESPONSE_RESULTS)
                        .getJSONObject(0)
                        .getJSONObject(RESPONSE_GEOMETRY)
                        .getJSONObject(RESPONSE_LOCATION);

                double lat = location.getDouble(RESPONSE_LOCATION_LAT);
                double lng = location.getDouble(RESPONSE_LOCATION_LNG);

                return new double[] {lat, lng};
            }

        } catch (JSONException e) {
            Log.d("Atlas", "getLatLngFromGoogleMapAPI() e:" + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static LatLng doublePairToLatLng(double[] place) {
        if (place == null)  return null;
        return new LatLng(place[0], place[1]);
    }

    public static byte[] urlToByte(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[OUTSTREAM_BUFFER_SIZE];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            return byteArrayOutputStream.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.d(TAG, "getBitmapFromURL done.");
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "getBitmapFromURL done.");
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.d(TAG, "getBitmapFromURL exception e:" + e);
            return null;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, float newWidth, float newHeight) {
        if (bitmap == null)  return  null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(newWidth/ width, newHeight/ height);

        // recreate the new Bitmap and set it back
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getCurrentTimeStamp() {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static String getCurrentTimeStampWithoutDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static String getReadableTime(Date d) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return sdfDate.format(d);
    }

    public static long getTimeDiffMs(Date d1, Date d2) {
        return d2.getTime() - d1.getTime();
    }

    public static long getTimeDiffSec(Date d1, Date d2) {
        return (getTimeDiffMs(d1,d2)/1000) % 60;
    }

    // Copy from http://fecbob.pixnet.net/blog/post/43546822-%5Bjava%5D-%E7%B6%93%E7%B7%AF%E5%BA%A6%E8%B7%9D%E9%9B%A2%E8%A8%88%E7%AE%97
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(LatLng p1, LatLng p2) {
        if (p1 == null || p2 == null)  return 0;
        return getDistance(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double EARTH_RADIUS = 6378137;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = rad(lat1) - rad(lat2);
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

}
