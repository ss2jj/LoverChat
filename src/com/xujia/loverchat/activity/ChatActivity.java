
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.control.HXSDKHelper;
import com.xujia.loverchat.model.UserDao;
import com.xujia.loverchat.utils.Utils;
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
private static String TAG = "ChatActivity";
private NewMessageBoradReceiver newMessageReceiver;
// 账号在别处登录
public boolean isConflict = false;
//账号被移除
private boolean isCurrentAccountRemoved = false;
private EMConnectionListener connectionListener;
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
        initListener();
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow){
            showConflictDialog();
        }else if(getIntent().getBooleanExtra("removed", false) && !isAccountRemovedDialogShow){
            showAccountRemovedDialog();
        }
      
    }
    
    @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            EMChatManager.getInstance().removeConnectionListener(connectionListener);
            unregisterReceiver(cmdMessageReceiver);
            unregisterReceiver(ackMessageReceiver);
            unregisterReceiver(newMessageReceiver);
        }
    //左边滑动菜单按钮处理
    private void initleftMenu() {
        menuList = getData();
        menus.setAdapter(new SimpleAdapter(this, menuList, R.layout.menu_list,new String[]{"item","image"}, new int[]{R.id.menu_text,R.id.menu_imageView1}));
        menus.setOnItemClickListener(new MyItemClickListener());
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
        popWindow.setBackgroundDrawable(new ColorDrawable(0000000000));//必须设置 否则下面的设置不生效
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
    private void initListener() {
        newMessageReceiver =  new NewMessageBoradReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(newMessageReceiver, intentFilter);
     // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
        
        //注册一个透传消息的BroadcastReceiver
        IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
        cmdMessageIntentFilter.setPriority(3);
        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
      
        // 注册一个监听连接状态的listener
        connectionListener =  new MyConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);
        EMChat.getInstance().setAppInited();
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
    
    //listview item 点击监听
    class  MyItemClickListener implements OnItemClickListener   {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
           switch (arg2) {
            case 0:
                
                break;
            case 1:
                
                break;
            case 2:
     
                break;
            case 3:
                break;
            case 4:
                Utils.printLog("logout");
                HXSDKHelper.getInstance().logout(null);
                Intent intent = new Intent(ChatActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        }
        
    }
    //接收新消息的广播
    class NewMessageBoradReceiver extends BroadcastReceiver  {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String from = arg1.getStringExtra("from");
            // 消息id
            String msgId = arg1.getStringExtra("msgid");
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            
            // fix: logout crash， 如果正在接收大量消息
            // 因为此时已经logout，消息队列已经被清空， broadcast延时收到，所以会出现message为空的情况
            if (message == null) {
                return;
            }
            abortBroadcast();
            //刷新未读消息页面
            ConversationsActivity.activityInstance. refershUI();
        }
        
    }
  
   
   /**
    * 消息回执BroadcastReceiver
    */
   private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

       @Override
       public void onReceive(Context context, Intent intent) {
           abortBroadcast();
           
           String msgid = intent.getStringExtra("msgid");
           String from = intent.getStringExtra("from");

           EMConversation conversation = EMChatManager.getInstance().getConversation(from);
           if (conversation != null) {
               // 把message设为已读
               EMMessage msg = conversation.getMessage(msgid);

               if (msg != null) {
                   msg.isAcked = true;
               }
           }
           
       }
   };
   
   
   
   /**
    * 透传消息BroadcastReceiver
    */
   private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
       
       @Override
       public void onReceive(Context context, Intent intent) {
           abortBroadcast();
           //获取cmd message对象
           String msgId = intent.getStringExtra("msgid");
           EMMessage message = intent.getParcelableExtra("message");
           //获取消息body
           CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
           String action = cmdMsgBody.action;//获取自定义action
           
           //获取扩展属性 此处省略
//         message.getStringAttribute("");
       }
   };

   private class MyConnectionListener implements EMConnectionListener {

       @Override
       public void onConnected() {
           runOnUiThread(new Runnable() {

               @Override
               public void run() {
                  // chatHistoryFragment.errorItem.setVisibility(View.GONE);
               }

           });
       }

       @Override
       public void onDisconnected(final int error) {
           runOnUiThread(new Runnable() {
               
               @Override
               public void run() {
                   if(error == EMError.USER_REMOVED){
                       // 显示帐号已经被移除
                       showAccountRemovedDialog();
                   }else if (error == EMError.CONNECTION_CONFLICT) {
                       // 显示帐号在其他设备登陆dialog
                       showConflictDialog();
                   } else {
                    //  chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
                       if (Utils.isNetworkActive())
                            Utils.showToast(getResources().getString(R.string.network_unavaible));
                   }
               }

           });
       }
   }
   private android.app.AlertDialog.Builder conflictBuilder;
   private android.app.AlertDialog.Builder accountRemovedBuilder;
   private boolean isConflictDialogShow;
   private boolean isAccountRemovedDialogShow;
   /**
    * 显示帐号在别处登录dialog
    */
   private void showConflictDialog() {
       isConflictDialogShow = true;
       HXSDKHelper.getInstance().logout(null);
       String st = getResources().getString(R.string.Logoff_notification);
       if (!ChatActivity.this.isFinishing()) {
           // clear up global variables
           try {
               if (conflictBuilder == null)
                   conflictBuilder = new android.app.AlertDialog.Builder(ChatActivity.this);
               conflictBuilder.setTitle(st);
               conflictBuilder.setMessage(R.string.connect_conflict);
               conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       conflictBuilder = null;
                       finish();
                       startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                   }
               });
               conflictBuilder.setCancelable(false);
               conflictBuilder.create().show();
               isConflict = true;
           } catch (Exception e) {
               //EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
           }

       }

   }
   
   /**
    * 帐号被移除的dialog
    */
   private void showAccountRemovedDialog() {
       isAccountRemovedDialogShow = true;
       HXSDKHelper.getInstance().logout(null);
       String st5 = getResources().getString(R.string.Remove_the_notification);
       if (!ChatActivity.this.isFinishing()) {
           // clear up global variables
           try {
               if (accountRemovedBuilder == null)
                   accountRemovedBuilder = new android.app.AlertDialog.Builder(ChatActivity.this);
               accountRemovedBuilder.setTitle(st5);
               accountRemovedBuilder.setMessage(R.string.em_user_remove);
               accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       accountRemovedBuilder = null;
                       finish();
                       startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                   }
               });
               accountRemovedBuilder.setCancelable(false);
               accountRemovedBuilder.create().show();
               isCurrentAccountRemoved = true;
           } catch (Exception e) {
               //EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
           }

       }

   }

   @Override
   protected void onNewIntent(Intent intent) {
       super.onNewIntent(intent);
       if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow){
           showConflictDialog();
       }else if(getIntent().getBooleanExtra("removed", false) && !isAccountRemovedDialogShow){
           showAccountRemovedDialog();
       }
   }
   @Override
   protected void onSaveInstanceState(Bundle outState) {
       outState.putBoolean("isConflict", isConflict);
       outState.putBoolean("removed", isCurrentAccountRemoved);
       super.onSaveInstanceState(outState);
   }

}
