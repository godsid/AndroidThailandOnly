package info.srihawong.thailandonly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
*/
import java.io.IOException;

/**
 * Created by Banpot.S on 11/2/2557.
 */
public class Util {
    public static String TAG = "tui";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final long TOAST_LENGTH_LONG_TIME = (long)3500;
    public static final long TOAST_LENGHT_SGORT_TIME = (long)2000;

    //GoogleCloudMessaging gcm;
    String regid;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID;

    public static void Log(String msg){
        Log.d(TAG,msg);
    }
/*
    public Boolean checkPlayServices(Context context){

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "This device is supported GooglePlayServices.");
            } else {
                Log.i(TAG, "This device is not supported GooglePlayServices.");
                return false;
            }
        }else{
            Log.i(TAG, "GooglePlayServicesAvailable Success.");

        }
        return true;
    }
    */
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }else{
            Log.i(TAG,"Registration ID " + registrationId);
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.

        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        Log.i(TAG,"Saving regId is " + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppVersionName(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            //throw new RuntimeException("Could not get package name: " + e);
            return "";
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
/*
    private void registerInBackground(final Context context) {
        new AsyncTask(){

            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                try{
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, regid);

                }catch (IOException ex){
                    msg = "Error :" + ex.getMessage();

                }
                //Log.d(TAG,"Registered"+msg);
                return msg;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d(TAG,o.toString());
            }


        }.execute(null,null,null);
    }
*/
    private void sendRegistrationIdToBackend(){

    }
    public static void dial(Context context ,String phoneNumber){
        Intent callActivity = new Intent(Intent.ACTION_CALL);
        callActivity.setData(Uri.parse(phoneNumber));
        callActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(callActivity);
    }



}


