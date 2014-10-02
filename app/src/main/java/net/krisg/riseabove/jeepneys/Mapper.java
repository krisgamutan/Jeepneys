package net.krisg.riseabove.jeepneys;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by KrisEmmanuel on 9/19/2014.
 */
public class Mapper {
    // Mapper class
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
    public static boolean isInsideRadius(LatLng center, double radiusMeters, LatLng point)
    {
        double dist = distVincenty(center, point);

        return dist < radiusMeters;
    }

    public static double distVincenty(LatLng p1, LatLng p2)
    {
        return distVincenty(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
    }


    /**
     * Calculates geodetic distance between two points specified by latitude/longitude using Vincenty inverse formula
     * for ellipsoids
     *
     * @param lat1
     *            first point latitude in decimal degrees
     * @param lon1
     *            first point longitude in decimal degrees
     * @param lat2
     *            second point latitude in decimal degrees
     * @param lon2
     *            second point longitude in decimal degrees
     * @returns distance in meters between points with 5.10<sup>-4</sup> precision
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally posted here</a>
     */
    public static double distVincenty(double lat1, double lon1, double lat2, double lon2) {
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 100;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM))
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double dist = b * A * (sigma - deltaSigma);

        return dist;
    }
}
