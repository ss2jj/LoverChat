
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.model.ExpressionAdapter;
import com.xujia.loverchat.model.ExpressionPagerAdapter;
import com.xujia.loverchat.model.MessageAdapter;
import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.SmileUtils;
import com.xujia.loverchat.view.ExpandGridView;
import com.xujia.loverchat.view.PasteEditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends Activity {
private TextView userNameText;
private String userName;
private InputMethodManager manager;
private LinearLayout speak,more,face,container;
private RelativeLayout edit,editTextLayout,recordContainer;
private Button speakButton,editButton,sendButton,moreButton;
private ImageView normalEmotion,checkEmotion,micView;
private TextView recordTip;
private PasteEditText mEditTextContent;
private Drawable micImages[];
private AnimationDrawable micAnim;
private EMConversation conversation;
private ListView listview;
private BroadcastReceiver receiver;
private MessageAdapter adapter;
private  List<String> reslist;
private ViewPager expressionViewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_conversation_list);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        initData();
    }
  
    private void initData() {
        // TODO Auto-generated method stub
        userName = getIntent().getStringExtra("username");
        userNameText.setText(userName);
        conversation =  EMChatManager.getInstance().getConversation(userName);
     // 把此会话的未读数置为0      
        conversation.resetUnreadMsgCount();
        ConversationsActivity.activityInstance.hander.sendEmptyMessage(1);
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < Consts.PAGESIZE) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, Consts.PAGESIZE);
          
        }
        adapter = new MessageAdapter(this, userName);
        listview.setAdapter(adapter);
        listview.setOnScrollListener(new ListScrollListener());
        int count = listview.getCount();
        if (count > 0) {
            listview.setSelection(count - 1);
        }
        
        listview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                normalEmotion.setVisibility(View.VISIBLE);
                checkEmotion.setVisibility(View.INVISIBLE);
                face.setVisibility(View.GONE);
                container.setVisibility(View.GONE);
                return false;
            }
        });
        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(5);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个消息送达的BroadcastReceiver
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
        deliveryAckMessageIntentFilter.setPriority(5);
        registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

    }

    private void initView() {
        userNameText =  (TextView) findViewById(R.id.title_user);
        speak = (LinearLayout) findViewById(R.id.btn_press_to_speak);
        edit = (RelativeLayout)findViewById(R.id.edittext_layout);
        editButton = (Button) findViewById(R.id.btn_set_mode_keyboard);
        speakButton = (Button) findViewById(R.id.btn_set_mode_voice);
        more = (LinearLayout) findViewById(R.id.more);
        face = (LinearLayout) findViewById(R.id.ll_face_container);
        container = (LinearLayout) findViewById(R.id.ll_btn_container);
        normalEmotion = (ImageView) findViewById(R.id.iv_emoticons_normal);
        checkEmotion = (ImageView) findViewById(R.id.iv_emoticons_checked);
        editTextLayout = (RelativeLayout) findViewById(R.id.edittext_layout);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        sendButton = (Button) findViewById(R.id.btn_send);
        moreButton= (Button) findViewById(R.id.btn_more);
        recordContainer = (RelativeLayout) findViewById(R.id.recording_container);
        micView = (ImageView) findViewById(R.id.mic_image);
        recordTip = (TextView) findViewById(R.id.recording_hint);
        micAnim = (AnimationDrawable) micView.getBackground();
        listview = (ListView) findViewById(R.id.list);
        speak.setOnTouchListener(new SpeakListener());
        editTextLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        // 表情list
        reslist = getExpressionRes(35);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        editTextLayout.requestFocus();
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    editTextLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if(!TextUtils.isEmpty(arg0))    {
                    sendButton.setVisibility(View.VISIBLE);
                    moreButton.setVisibility(View.GONE);
                }else {
                    sendButton.setVisibility(View.GONE);
                    moreButton.setVisibility(View.VISIBLE);
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                
            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                normalEmotion.setVisibility(View.VISIBLE);
                checkEmotion.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void setModeVoice(View view) {
        hideKeyboard();
        speak.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        speakButton.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
    }
    public void setModeKeyboard(View view)  {
        speak.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        speakButton.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
    }
    public void editClick(View view)    {
        
    }
    public void more(View view) {
        hideKeyboard();
        if(checkEmotion.isShown()) {
            normalEmotion.setVisibility(View.VISIBLE);
            checkEmotion.setVisibility(View.GONE);
        }
        
        if(more.isShown())  {
            more.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            if(speak.isShown()) {   //当更多面板展开时 需要切换回输入模式
                speak.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                speakButton.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
            }
           
                container.setVisibility(View.VISIBLE);
                more.setVisibility(View.VISIBLE);
                face.setVisibility(View.GONE);
        }
    }
    
    /**
     * 获取表情的gridview的子view
     * 
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (speakButton.getVisibility() != View.VISIBLE) {

                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.easemob.chatuidemo.utils.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ConversationListActivity.this, (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }
    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }
    public void onClick(View view)  {
        if(view.getId() == R.id.iv_emoticons_normal)    {
            hideKeyboard();
            normalEmotion.setVisibility(View.GONE);
            checkEmotion.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            face.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }else if(view.getId() == R.id.iv_emoticons_checked)  {
            normalEmotion.setVisibility(View.VISIBLE);
            checkEmotion.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            face.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
        }else if(view.getId() == R.id.btn_send) {
            String s = mEditTextContent.getText().toString();
            sendText(s);
        }
    }
    
    /**
     * 发送文本消息
     * 
     * @param content
     *            message content
     * @param isResend
     *            boolean resend
     */
    private void sendText(String content) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
           
            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(userName);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refresh();
            listview.setSelection(listview.getCount() - 1);
            mEditTextContent.setText("");

            setResult(RESULT_OK);

        }
    }
    public void back(View view) {
        Intent intent = new Intent(ConversationListActivity.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
               /** if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                    loadmorePB.setVisibility(View.VISIBLE);
                    // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
                    List<EMMessage> messages;
                    try {
                        // 获取更多messges，调用此方法的时候从db获取的messages
                        // sdk会自动存入到此conversation中
                     
                            messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                  
                    } catch (Exception e1) {
                       // loadmorePB.setVisibility(View.GONE);
                        return;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    if (messages.size() != 0) {
                        // 刷新ui
                        adapter.notifyDataSetChanged();
                        listView.setSelection(messages.size() - 1);
                        if (messages.size() != pagesize)
                            haveMoreData = false;
                    } else {
                        haveMoreData = false;
                    }
                    loadmorePB.setVisibility(View.GONE);
                    isloading = false;

                }**/
                break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

   class SpeakListener implements View.OnTouchListener  {

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recordContainer.setVisibility(View.VISIBLE);
                recordTip.setText(getString(R.string.move_up_to_cancel));
                recordTip.setBackgroundColor(Color.TRANSPARENT);
                if(!micAnim.isRunning()){  
                    micAnim.start();  
                    }  
                return true;
            case MotionEvent.ACTION_MOVE:
                if (arg1.getY() < 0) {
                    recordTip.setText(getString(R.string.release_to_cancel));
                    recordTip.setBackgroundResource(R.drawable.recording_text_hint_bg);
                } else {
                    recordTip.setText(getString(R.string.move_up_to_cancel));
                    recordTip.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if(micAnim.isRunning()){  
                    micAnim.stop();  
                    }  
                recordContainer.setVisibility(View.INVISIBLE);
                return true;
            default:
                recordContainer.setVisibility(View.INVISIBLE);
                return false;
        }
        
    }
       
   }
   
   /**
    * 消息广播接收者
    * 
    */
   private class NewMessageBroadcastReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
           // 记得把广播给终结掉
           abortBroadcast();

           String username = intent.getStringExtra("from");
           String msgid = intent.getStringExtra("msgid");
           // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
           EMMessage message = EMChatManager.getInstance().getMessage(msgid);
           // 如果是群聊消息，获取到group id
           if (message.getChatType() == ChatType.GroupChat) {
               username = message.getTo();
           }
           if (!username.equals(userName)) {
               // 消息不是发给当前会话，return
              // notifyNewMessage(message);
               return;
           }
           // conversation =
           // EMChatManager.getInstance().getConversation(toChatUsername);
           // 通知adapter有新消息，更新ui
           adapter.refresh();
           listview.setSelection(listview.getCount() - 1);

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
           adapter.notifyDataSetChanged();

       }
   };

   /**
    * 消息送达BroadcastReceiver
    */
   private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
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
                   msg.isDelivered = true;
               }
           }

           adapter.notifyDataSetChanged();
       }
   };
}
