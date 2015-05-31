
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.model.UserDao;

import java.util.List;

public class ConversationsActivity extends Activity {
    private RelativeLayout conversionFriend;

    private String userName = null;
    private TextView userNameText,unReadMessageText,unReadMessageTimeText;
    private static final int REFERSH_UI =0;
    public static ConversationsActivity activityInstance;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        conversionFriend = (RelativeLayout) findViewById(R.id.conversion_freind);
        userNameText = (TextView) findViewById(R.id.conversion_name);
        unReadMessageText = (TextView) findViewById(R.id.conversion_unread);
        unReadMessageTimeText = (TextView) findViewById(R.id.conversation_time);
        activityInstance = this;
        refershUI();
    }
public void refershUI() {
   
    if((userName=UserDao.getInstance().getUser())!= null)
    {
        conversionFriend.setVisibility(View.VISIBLE);
        userNameText.setText(userName);
        EMConversation conversation = EMChatManager.getInstance().getConversation(userName);
        if(conversation.getUnreadMsgCount()!=0) {
            //有未读消息 获取最后一条消息展示出来
           List<EMMessage> messages = conversation.getAllMessages();
           EMMessage message = messages.get(messages.size()-1);
           TextMessageBody messBody =   (TextMessageBody) message.getBody();
           unReadMessageText.setText(messBody.getMessage());
           unReadMessageTimeText.setText(String.valueOf(message.getMsgTime()));
        }else {
            unReadMessageText.setText("");
            unReadMessageTimeText.setText("");
        }
     
    }else   {
        conversionFriend.setVisibility(View.INVISIBLE);
    }
  
   
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversations, menu);
        return true;
    }

   

    
}
