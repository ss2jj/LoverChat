
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.xujia.loverchat.R;
import com.xujia.loverchat.control.HXSDKHelper;

public class MainActivity extends Activity {
private static final int ANIM_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        TextView versionText = (TextView) findViewById(R.id.app_version);
        versionText.setText(getVersion());
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_activity);
        AlphaAnimation ap = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_main);
        rootLayout.setAnimation(ap);
      
      
       
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(HXSDKHelper.getInstance().isLogined())   {
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().getAllConversations();
                    long cost =  System.currentTimeMillis() -start;
                    try {
                        if(ANIM_TIME-cost > 0)
                        Thread.sleep(ANIM_TIME-cost);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //startActivity();
                   // finish();
                }else   {
                    try {
                    
                        Thread.sleep(ANIM_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }
    private String getVersion() {
        String version = "";
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            version = pi.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
