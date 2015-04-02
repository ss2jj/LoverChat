
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        
    }

    //µÇÂ½°´Å¥
    public void login(View  view) {
        
    }
    //×¢²á°´Å¥
    public void register(View  view)  {
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
