package com.voidabhi.travelapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.voidabhi.travelapp.R;


/**
 * Created by ABHIJEET on 24-01-2015.
 */
public class NetUtils {

    // utility to check if the network connection is available or not
    public static boolean isOnline(Context c) {
        NetworkInfo netInfo = null;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Checks if google play services can be used
    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status,activity, 0).show();
            return false;
        }
    }

    // Shows toast taking  message resource id
    public static void showToast(Activity activity,int resid){

        showToast(activity,activity.getResources().getString(resid));
    }

    // Shows toast taking  string message 
    public static void showToast(Activity activity,String message){

        View layout = activity.getLayoutInflater().inflate(R.layout.toast,null);

        TextView textView = (TextView) layout.findViewById(R.id.toast_message);
        textView.setText(message);

        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }
}
