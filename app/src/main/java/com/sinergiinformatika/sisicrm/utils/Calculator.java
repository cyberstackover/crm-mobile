package com.sinergiinformatika.sisicrm.utils;

import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;

/**
 * Created by wendi on 29-Dec-14.
 */
public class Calculator {

    private static final String TAG = Calculator.class.getSimpleName();

    //referensi http://www.dzone.com/snippets/distance-calculation-using-3
    public static Double distance(double latitude1, double longitude1, double latitude2, double longitude2) {

      /*  if(Constants.DEBUG){
            Log.d("Calculator.distance", "latitude1 = " + latitude1);
            Log.d("Calculator.distance", "longitude1 = "+longitude1);
            Log.d("Calculator.distance", "latitude2 = "+latitude2);
            Log.d("Calculator.distance", "longitude2 = "+longitude2);
        }
*/
        try {
            double theta = longitude1 - longitude2;
            double dist = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2)) + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1609.34; //meter
            return (dist);
        } catch (Exception e) {
            Log.e("Calculator.distance", e.getMessage(), e);

        }

        return null;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    //TODO perlu direname

    /**
     * konversi dari ston ke sak
     *
     * @param productWeight
     * @param value
     * @return
     */
    public static double convert(String productWeight, double value){

        try {
            return ((value * 1000) / Integer.parseInt(productWeight));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return 0;
    }

}
