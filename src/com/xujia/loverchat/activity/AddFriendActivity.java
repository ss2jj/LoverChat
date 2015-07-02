
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChat;
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

import java.util.HashMap;
import java.util.List;

public class AddFriendActivity extends Activity implements View.OnClickListener {
private LinearLayout addFriend1,addFriend2,valaditeFriend;
private TextView validateFriendName;
private Button addFriend;
private EditText addFriendUser;
private EMContactListener contactListener;
public  static  AddFriendActivity activityInstance;
public Handler hander =  new Handler()  {
    public void handleMessage(android.os.Message msg) {
         refershUI();
    };
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
       
        addFriend1 = (LinearLayout) findViewById(R.id.add_friend_show);
        addFriend2 = (LinearLayout) findViewById(R.id.add_friend_view);
        valaditeFriend = (LinearLayout)findViewById(R.id.validate_friend_view);
        validateFriendName = (TextView)findViewById(R.id.validate_friend_name);
        addFriend1.setOnClickListener(this);
        addFriend = (Button) findViewById(R.id.add_friend_action);
        addFriendUser = (EditText) findViewById(R.id.add_friend_name);
        addFriend.setOnClickListener(this);
        refershUI();
        activityInstance = this;
        //contactListener =  new MyContactListener();
        //监听联系人变化
     
      //  EMChat.getInstance().setAppInited(); 
    }
    @Override
        protected void onResume() {
            // TODO Auto-generated method stub
        Utils.printLog("Addfriedn onresume");
             refershUI();
            super.onResume();
        }
    
    private void  refershUI() {
               runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // TODO Auto-generated method stub
                    HashMap<String,String> user = UserDao.getInstance().getUser();
                    if(user!= null && user.get(Consts.USERNAME) != null && user.get(Consts.VALIDATE).equals("no"))  {
                        valaditeFriend.setVisibility(View.VISIBLE);
                        validateFriendName.setText(user.get(Consts.USERNAME));
                    }else {
                        valaditeFriend.setVisibility(View.GONE); 
                    }
                }
            });
          
            }
       
    
    @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            activityInstance = null;
           // EMContactManager.getInstance().removeContactListener();
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
                  
                    EMContactManager.getInstance().addContact(userName, "");
                    UserDao.getInstance().saveUser(userName,"no");
                    refershUI();
                } catch (EaseMobException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }}}).start();
                Utils.printLog("add a frined message send success");
                addFriendUser.setText("");
                addFriend2.setVisibility(View.GONE);
            }
        }
    }
    
}
