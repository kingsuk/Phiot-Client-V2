package Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ProjectConfig {
    public static String Base_Url = "https://phiot.azurewebsites.net/api/";
    public static String SharedPreferenceName = "PhiOT";

    public static String PhiOT_Base_Url = "http://192.168.4.22/";

    public static boolean buttonLoading = false;

    public static boolean ValidateEmail(String email)
    {
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return  matcher.find();
    }

    public static void StaticLog(String logMessage)
    {
        Log.i("kingsukm",logMessage);
    }

    public static void StaticToast(Context applicationContext, String message)
    {
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show();
    }
}
