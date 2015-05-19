
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends Activity {
private TabHost tabHost;
private ListView menus;
private List<Map<String,Object>> menuList;
private LocalActivityManager mLocalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        mLocalActivity = new LocalActivityManager(this, false);
        mLocalActivity.dispatchCreate(savedInstanceState);
        initTabHost();
        menus = (ListView) findViewById(R.id.menus);
        menuList = getData();
        menus.setAdapter(new SimpleAdapter(this, menuList, R.layout.menu_list,new String[]{"item","image"}, new int[]{R.id.menu_text,R.id.menu_imageView1}));
    }
    
    private List<Map<String,Object>> getData()  {
        List<Map<String,Object>> menuLists =  new ArrayList<Map<String,Object>>();
        Map<String,Object> menu;
        menu =  new HashMap<String, Object>();
        menu.put("item", "设置1");
        menu.put("image", R.drawable.ic_launcher);
        menuLists.add(menu);
        menu =  new HashMap<String, Object>();
        menu.put("item", "设置2");
        menu.put("image", R.drawable.ic_launcher);
        menuLists.add(menu);
        menu =  new HashMap<String, Object>();
        menu.put("item", "设置3");
        menu.put("image", R.drawable.ic_launcher);
        menuLists.add(menu);                       
        return menuLists;
    }
    private void initTabHost()  {
        if(tabHost != null) {
          
            tabHost.setup(mLocalActivity);
            tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab1").setContent(new Intent(this,ConversationsActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab2").setContent(new Intent(this,AddFriendActivity.class)));
            tabHost.setCurrentTab(0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

}
