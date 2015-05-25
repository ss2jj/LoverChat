
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.view.DragLayout;
import com.xujia.loverchat.view.DragLayout.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends Activity implements View.OnClickListener{
private TabHost tabHost;
private ListView menus,more;
private List<Map<String,Object>> menuList,more_list;
private LocalActivityManager mLocalActivity;
private ImageButton leftMenu,rightMore;
private DragLayout dragLayout;
private PopupWindow popWindow;
private View popwindowView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        menus = (ListView) findViewById(R.id.menus);
        leftMenu = (ImageButton) findViewById(R.id.leftmenu_btn);
        rightMore  = (ImageButton) findViewById(R.id.rightmore_btn);
        dragLayout = (DragLayout) findViewById(R.id.dl); 
        leftMenu.setOnClickListener(this);
        rightMore.setOnClickListener(this);
        initTabHost(savedInstanceState);
        initleftMenu();
        initRightMore();
    }
    //左边滑动菜单按钮
    private void initleftMenu() {
        menuList = getData();
        menus.setAdapter(new SimpleAdapter(this, menuList, R.layout.menu_list,new String[]{"item","image"}, new int[]{R.id.menu_text,R.id.menu_imageView1}));
    }
    //右边更多按钮菜单处理
    private void initRightMore()    {
        popwindowView = getLayoutInflater().inflate(R.layout.popwindow_more, null);
        more = (ListView) popwindowView.findViewById(R.id.more_listview);
        more_list = getMoreItem();
        more.setAdapter(new SimpleAdapter(this,more_list,R.layout.more_menu_list,new String[]{"item","image"},new int[]{R.id.more_text,R.id.more_imageView1}));
        popWindow =  new PopupWindow(popwindowView);//添加popwindow
        popWindow.setFocusable(true);
        more.measure(View.MeasureSpec.UNSPECIFIED,  
                View.MeasureSpec.UNSPECIFIED); //设置popwindow大小为自定义
        popWindow.setWidth(more.getMeasuredWidth());  
        popWindow.setHeight((more.getMeasuredHeight() + 20)  //设置popwindow的大小
                * 3);  
        popWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popWindow.setOutsideTouchable(true); //设置popwindow外触摸关闭popwindow
    }
    private List<Map<String,Object>> getData()  {
        List<Map<String,Object>> menuLists =  new ArrayList<Map<String,Object>>();
        Map<String,Object> menu;
        menu =  new HashMap<String, Object>();
        menu.put("item", getResources().getString(R.string.item_alarm));
        menu.put("image", R.drawable.item_alarm);
        menuLists.add(menu);
        menu =  new HashMap<String, Object>();
        menu.put("item", getResources().getString(R.string.item_interaction));
        menu.put("image", R.drawable.item_interation);
        menuLists.add(menu);
        menu =  new HashMap<String, Object>();
        menu.put("item", getResources().getString(R.string.item_date));
        menu.put("image", R.drawable.item_date);
        menuLists.add(menu);      
        menu =  new HashMap<String, Object>();
        menu.put("item", getResources().getString(R.string.item_idea));
        menu.put("image", R.drawable.item_idea);
        menuLists.add(menu);   
        menu =  new HashMap<String, Object>();
        menu.put("item", getResources().getString(R.string.item_logout));
        menu.put("image", R.drawable.item_logout);
        menuLists.add(menu);   
        return menuLists;
    }
    private List<Map<String,Object>> getMoreItem()  {
        List<Map<String,Object>> moreItems =  new ArrayList<Map<String,Object>>();
        Map<String,Object> moreItem;
        moreItem =  new HashMap<String, Object>();
        moreItem.put("item", getResources().getString(R.string.item_add));
        moreItem.put("image", R.drawable.ofm_add_icon);
        moreItems.add(moreItem);
        moreItem =  new HashMap<String, Object>();
        moreItem.put("item", getResources().getString(R.string.item_sao));
        moreItem.put("image", R.drawable.ofm_qrcode_icon);
        moreItems.add(moreItem);
        moreItem =  new HashMap<String, Object>();
        moreItem.put("item", getResources().getString(R.string.item_my));
        moreItem.put("image", R.drawable.actionbar_camera_icon);
        moreItems.add(moreItem);
        return moreItems;
    }
    private void initTabHost(Bundle savedInstanceState)  {
        mLocalActivity = new LocalActivityManager(this, false);
        mLocalActivity.dispatchCreate(savedInstanceState);
        if(tabHost != null) {
            tabHost.setup(mLocalActivity); //tabhost里的content为intent时需要传递localactivity
            tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.tab_chat)).setContent(new Intent(this,ConversationsActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.tab_friend)).setContent(new Intent(this,AddFriendActivity.class)));
            tabHost.setCurrentTab(0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if(arg0.getId() == R.id.leftmenu_btn)   {
            if(dragLayout.getStatus() ==  Status.Close) {
                dragLayout.open(); 
            }
        }else if(arg0.getId() == R.id.rightmore_btn)    {
            if(popWindow.isShowing())   {
                popWindow.dismiss();
            }else   {
                View parent = findViewById(R.id.rightmore_btn);
                int location[] =  new int[2];
                parent.getLocationOnScreen(location);
                popWindow.showAtLocation(parent,Gravity.TOP|Gravity.RIGHT,0,location[1]+53);//popwindow显示在特定位置
            }
        }
    }

}
