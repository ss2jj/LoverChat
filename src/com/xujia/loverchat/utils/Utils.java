package com.xujia.loverchat.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.xujia.loverchat.control.BaseApplication;

public class Utils {

    
    /*
     * 显示toast
     */
    public static void showToast(String message)    {
        Toast.makeText(BaseApplication.getGlobalContext(), message, 1000).show();
    }
    
    //判断当前网络是否有可用
    public static boolean isNetworkActive() {
        boolean isWork = false;
        ConnectivityManager manager = (ConnectivityManager) BaseApplication.getGlobalContext().getSystemService(BaseApplication.getGlobalContext().CONNECTIVITY_SERVICE);
        NetworkInfo networks  = manager.getActiveNetworkInfo();
         if(networks != null)   {
             isWork = networks.isAvailable();
         }
        return isWork;
    }
}
