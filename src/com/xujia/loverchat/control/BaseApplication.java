package com.xujia.loverchat.control;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
private  static Context globalContext;
private String userName;
private String password;

@Override
    public void onCreate() {
        // TODO Auto-generated method stub
      globalContext = getApplicationContext();
      
      //初始化环信
      HXSDKHelper.getInstance().onInit(globalContext);
    }
    
    public static Context getGlobalContext()    {
            return globalContext;
    }
}
