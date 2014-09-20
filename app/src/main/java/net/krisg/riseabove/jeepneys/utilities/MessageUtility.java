package net.krisg.riseabove.jeepneys.utilities;
import android.content.Context;
import android.widget.Toast;
/**
 * Created by KrisEmmanuel on 9/18/2014.
 */
public class MessageUtility {
    public static void displayToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
