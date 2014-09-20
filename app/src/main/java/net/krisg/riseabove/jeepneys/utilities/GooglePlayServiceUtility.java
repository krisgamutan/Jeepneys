package net.krisg.riseabove.jeepneys.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
/**
 * Created by KrisEmmanuel on 9/18/2014.
 */
public class GooglePlayServiceUtility {

    public static final int GPS_ERRORDIALOG_REQUEST = 9002;

    private static final String LOGTAG="ServiceUtil";

    public static boolean isPlayServiceAvailable(Context context) {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        switch (isAvailable) {
            case ConnectionResult.SUCCESS:
                Log.i(LOGTAG, "Connected to service");
                return true;
            case ConnectionResult.SERVICE_MISSING:
                Log.i(LOGTAG, "Service missing");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.i(LOGTAG, "Update required");
                break;
            case ConnectionResult.SERVICE_INVALID:
                Log.i(LOGTAG, "Service invalid");
                break;

            default:
                Log.i(LOGTAG, "Different value: " + isAvailable);
                break;
        }
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
                (Activity)context, GPS_ERRORDIALOG_REQUEST);
        dialog.show();
        return false;
    }
}
