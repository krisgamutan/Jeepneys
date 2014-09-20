package net.krisg.riseabove.jeepneys;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by KrisEmmanuel on 9/19/2014.
 */
public class Mapper {
    public static final double calculateDistance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
        if ((Math.abs(paramDouble2 - paramDouble4) < 1.0E-07D) && (Math.abs(paramDouble1 - paramDouble3) < 1.0E-07D))
            return 0.0D;
        double d = paramDouble2 - paramDouble4;
        return 1.609344D * (1.1515D * (60.0D * rad2deg(Math.acos(Math.sin(deg2rad(paramDouble1)) * Math.sin(deg2rad(paramDouble3)) + Math.cos(deg2rad(paramDouble1)) * Math.cos(deg2rad(paramDouble3)) * Math.cos(deg2rad(d))))));
    }

    public static final double deg2rad(double deg)
    {
        return Math.PI * deg / 180.0D;
    }

    public static final double rad2deg(double rad)
    {
        return 180.0D * rad / Math.PI;
    }
    public static float distFrom(float lat1, float lng1, float lat2, float lng2 )
    {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();
    }
    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        //double valueResult= Radius*c;
        //double km=valueResult/1;

        //DecimalFormat newFormat = new DecimalFormat("####");

        //int kmInDec =  Integer.valueOf(newFormat.format(km));
        //double meter=valueResult%1000;
        //int  meterInDec= Integer.valueOf(newFormat.format(meter));

        //Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c; // km
    }
}
