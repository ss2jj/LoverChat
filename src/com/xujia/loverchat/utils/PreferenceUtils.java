package com.xujia.loverchat.utils;

import android.content.SharedPreferences;

import com.xujia.loverchat.control.BaseApplication;



public class PreferenceUtils {

    public static String getValue(String key,String defaultName,String preName)   {
        SharedPreferences pre = BaseApplication.getGlobalContext().getSharedPreferences(preName, 0);
        return pre.getString(key, defaultName);
    }
    
   public  static void saveValue(String key,String value,String preName)   {
       SharedPreferences pre = BaseApplication.getGlobalContext().getSharedPreferences(preName, 0);
       SharedPreferences.Editor editor = pre.edit();
       editor.putString(key, value);
       editor.commit();
   }
}
