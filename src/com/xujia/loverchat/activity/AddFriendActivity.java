
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.model.UserDao;
import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.Utils;

import java.util.List;

public class AddFriendActivity extends Activity implements View.OnClickListener {
private LinearLayout addFriend1,addFriend2;
private Button addFriend;
private EditText addFriendUser;
private EMContactListener contactListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        addFriend1 = (LinearLayout) findViewById(R.id.add_friend_show);
        addFriend2 = (LinearLayout) findViewById(R.id.add_friend_view);
        addFriend1.setOnClickListener(this);
        addFriend = (Button) findViewById(R.id.add_friend_action);
        addFriendUser = (EditText) findViewById(R.id.add_friend_name);
        addFriend.setOnClickListener(this);
        contactListener =  new MyContactListener();
        //监听联系人变化
        EMContactManager.getInstance().setContactListener(contactListener);
    }

    
    @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            EMContactManager.getInstance().removeContactListener();
        }
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if(arg0.getId() == R.id.add_friend_show)    {
            if(!addFriend2.isShown())    {
                addFriend2.setVisibility(View.VISIBLE);
            }else {
                addFriend2.setVisibility(View.GONE);
            }
        }else if(arg0.getId() == R.id.add_friend_action)    {
            if(addFriend2.isShown())    {
               final String userName = addFriendUser.getText().toString();
                if(TextUtils.isEmpty(userName)) {
                    Utils.showToast("好友名称为空");
                    return;
                }
                new Thread(new Runnable() {
                    public void run() {
                try {
                    UserDao.getInstance().saveUser(userName);
                    EMContactManager.getInstance().addContact(userName, "");
                } catch (EaseMobException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }}}).start();
                Utils.showToast("发送成功");
                addFriendUser.setText("");
                addFriend2.setVisibility(View.GONE);
                ConversationsActivity.activityInstance. refershUI();
            }
        }
    }
    //接收联系人变化的广播
    class MyContactListener implements EMContactListener {

     @Override
     public void onContactAdded(List<String> arg0) {
         // TODO Auto-generated method stub
         Log.d("ChatActivity", "onContactAdded");
         for(String userName : arg0)
         {
           
             UserDao.getInstance().saveUser(userName);
           // ConversationsActivity.activityInstance. refershUI();
         }
     }

     @Override
     public void onContactAgreed(String arg0) {
         // TODO Auto-generated method stub
       
         Utils.printLog("onContactAgreed  "+arg0);
             UserDao.getInstance().saveUser(arg0);
            //ConversationsActivity.activityInstance. refershUI();
       
     }

     @Override
     public void onContactDeleted(List<String> arg0) {
         // TODO Auto-generated method stub
     
         Utils.printLog("onContactDeleted  ");
         for(String userName : arg0)
         {
             UserDao.getInstance().deleteUser(userName);
            // ConversationsActivity.activityInstance.refershUI();
         }
     }

     @Override
     public void onContactInvited(String arg0, String arg1) {
         // TODO Auto-generated method stub
         Utils.printLog( "onContactInvited " +arg0);
         showFriendInvitation(arg0);
     }

     @Override
     public void onContactRefused(String arg0) {
         // TODO Auto-generated method stub
         UserDao.getInstance().deleteUser(arg0);
        // ConversationsActivity.activityInstance.refershUI();
     }
        
    }
    //显示拒绝或者统一好友邀请
    public void showFriendInvitation(final String username)   {
        if (!AddFriendActivity.this.isFinishing()) {
        android.app.AlertDialog.Builder friendInvitationBuilder = new android.app.AlertDialog.Builder(AddFriendActivity.this);
        friendInvitationBuilder.setTitle(R.string.friend_invitation_title);
        friendInvitationBuilder.setMessage(username+getResources().getString(R.string.friend_invitation_message));
        friendInvitationBuilder.setCancelable(false);
        friendInvitationBuilder.setPositiveButton(getResources().getString(R.string.friend_invitation_accept), new DialogInterface.OnClickListener() {
         
         @Override
         public void onClick(DialogInterface arg0, int arg1) {
             // TODO Auto-generated method stub
             try {
                 EMChatManager.getInstance().acceptInvitation(username);
                 arg0.dismiss();
             } catch (EaseMobException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
     });
        friendInvitationBuilder.setNegativeButton(R.string.friend_invitation_refuse, new DialogInterface.OnClickListener() {
         
         @Override
         public void onClick(DialogInterface arg0, int arg1) {
             // TODO Auto-generated method stub
             try {
                 EMChatManager.getInstance().refuseInvitation(username);
                 arg0.dismiss();
             } catch (EaseMobException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
     });
        friendInvitationBuilder.create().show();
        }
    }
}
