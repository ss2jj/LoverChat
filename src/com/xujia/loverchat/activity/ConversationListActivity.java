
package com.xujia.loverchat.activity;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.xujia.loverchat.R;
import com.xujia.loverchat.control.VoicePlayClickListener;
import com.xujia.loverchat.model.ExpressionAdapter;
import com.xujia.loverchat.model.ExpressionPagerAdapter;
import com.xujia.loverchat.model.MessageAdapter;
import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.ImageUtils;
import com.xujia.loverchat.utils.PreferenceUtils;
import com.xujia.loverchat.utils.SmileUtils;
import com.xujia.loverchat.utils.Utils;
import com.xujia.loverchat.view.ExpandGridView;
import com.xujia.loverchat.view.PasteEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
private VoiceRecorder voiceRecorder;
public String playMsgId;
private PowerManager.WakeLock wakeLock;
private ClipboardManager clipboard;
static int resendPos;
private ProgressBar loadmorePB;
public static final String COPY_IMAGE = "EASEMOBIMG";
private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
public static final int REQUEST_CODE_CONTEXT_MENU = 3;
private static final int REQUEST_CODE_MAP = 4;
public static final int REQUEST_CODE_TEXT = 5;
public static final int REQUEST_CODE_VOICE = 6;
public static final int REQUEST_CODE_PICTURE = 7;
public static final int REQUEST_CODE_LOCATION = 8;
public static final int REQUEST_CODE_NET_DISK = 9;
public static final int REQUEST_CODE_FILE = 10;
public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
public static final int REQUEST_CODE_PICK_VIDEO = 12;
public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
public static final int REQUEST_CODE_VIDEO = 14;
public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
public static final int REQUEST_CODE_SEND_USER_CARD = 17;
public static final int REQUEST_CODE_CAMERA = 18;
public static final int REQUEST_CODE_LOCAL = 19;
public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
public static final int REQUEST_CODE_GROUP_DETAIL = 21;
public static final int REQUEST_CODE_SELECT_VIDEO = 23;
public static final int REQUEST_CODE_SELECT_FILE = 24;
public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

public static final int RESULT_CODE_COPY = 1;
public static final int RESULT_CODE_DELETE = 2;
public static final int RESULT_CODE_FORWARD = 3;
public static final int RESULT_CODE_OPEN = 4;
public static final int RESULT_CODE_DWONLOAD = 5;
public static final int RESULT_CODE_TO_CLOUD = 6;
public static final int RESULT_CODE_EXIT_GROUP = 7;
private File cameraFile;
private boolean isloading;
private boolean haveMoreData = true;
private Handler micImageHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
        // 切换msg切换图片
       // micImage.setImageDrawable(micImages[msg.what]);
    }
};
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
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        
        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
        if (forward_msg_id != null) {
            // 显示发送要转发的消息
            forwardMessage(forward_msg_id);
        }
    }
    /**
     * 转发消息
     * 
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
        case TXT:
            // 获取消息内容，发送消息
            String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
            sendText(content);
            break;
        case IMAGE:
            // 发送图片
            String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
            if (filePath != null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    // 不存在大图发送缩略图
                    filePath = ImageUtils.getThumbnailImagePath(filePath);
                }
                sendPicture(filePath);
            }
            break;
        default:
            break;
        }
    }
    
    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
            case RESULT_CODE_COPY: // 复制消息
                EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                break;
            case RESULT_CODE_DELETE: // 删除消息
                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                conversation.removeMessage(deleteMsg.getMsgId());
                adapter.refresh();
                listview.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
                break;

            case RESULT_CODE_FORWARD: // 转发消息
                //EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
              //  Intent intent = new Intent(this, ForwardMessageActivity.class);
                //intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
                //startActivity(intent);

                break;

            default:
                break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(userName);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
                    }
                    fos = new FileOutputStream(file);

                    bitmap.compress(CompressFormat.JPEG, 100, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE 
                    || requestCode == REQUEST_CODE_VIDEO ) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }

                }
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
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
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
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
        
        voiceRecorder = new VoiceRecorder(micImageHandler);
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
                            Class clz = Class.forName("com.xujia.loverchat.utils.SmileUtils");
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
    
    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refresh();
        listview.setSelection(resendPos);
    }
    /**
     * 根据图库图片uri发送图片
     * 
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }
    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath, final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
        
            String to = userName;
            message.setReceipt(to);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            conversation.addMessage(message);
            listview.setAdapter(adapter);
            adapter.refresh();
            listview.setSelection(listview.getCount() - 1);
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!Utils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(getApplicationContext(), st, 0).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), PreferenceUtils.getValue(Consts.userName, null, Consts.userPre)
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
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
        String st1 = getResources().getString(R.string.not_connect_to_server);
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
        }else if (view.getId() == R.id.btn_take_picture) {
            selectPicFromCamera();// 点击照相图标
        } else if (view.getId() == R.id.btn_picture) {
            selectPicFromLocal(); // 点击图片图标
        }else if (view.getId() == R.id.btn_video) {
            // 点击摄像图标
            Intent intent = new Intent(ConversationListActivity.this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
        }else if (view.getId() == R.id.btn_voice_call) { // 点击语音电话图标
            if (!EMChatManager.getInstance().isConnected())
                Toast.makeText(this, st1, 0).show();
            else
                startActivity(new Intent(ConversationListActivity.this, VoiceCallActivity.class).putExtra("username", userName)
                        .putExtra("isComingCall", false));
        }else if (view.getId() == R.id.btn_video_call) { //视频通话
            if (!EMChatManager.getInstance().isConnected())
                Toast.makeText(this, st1, 0).show();
            else
                startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", userName)
                        .putExtra("isComingCall", false));
        }
    }
    
    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }
    /**
     * 发送图片
     * 
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        String to = userName;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
      

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        conversation.addMessage(message);

        listview.setAdapter(adapter);
        adapter.refresh();
        listview.setSelection(listview.getCount() - 1);
        setResult(RESULT_OK);
        // more(more);
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
                if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                    loadmorePB.setVisibility(View.VISIBLE);
                    // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
                    List<EMMessage> messages;
                    try {
                        // 获取更多messges，调用此方法的时候从db获取的messages
                        // sdk会自动存入到此conversation中
                     
                            messages = conversation.loadMoreMsgFromDB(((EMMessage)adapter.getItem(0)).getMsgId(), Consts.PAGESIZE);
                  
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
                        listview.setSelection(messages.size() - 1);
                        if (messages.size() != Consts.PAGESIZE)
                            haveMoreData = false;
                    } else {
                        haveMoreData = false;
                    }
                    loadmorePB.setVisibility(View.GONE);
                    isloading = false;

                }
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
                if (!Utils.isExitsSdcard()) {
                  Utils.showToast("请插入SD卡");
                    return false;
                }
                arg0.setPressed(true);
                wakeLock.acquire();
                if (VoicePlayClickListener.isPlaying)
                    VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                recordContainer.setVisibility(View.VISIBLE);
                recordTip.setText(getString(R.string.move_up_to_cancel));
                recordTip.setBackgroundColor(Color.TRANSPARENT);
                voiceRecorder.startRecording(null, userName, getApplicationContext());
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
                arg0.setPressed(false);
                if(micAnim.isRunning()){  
                    micAnim.stop();  
                    }  
                recordContainer.setVisibility(View.INVISIBLE);
                if (wakeLock.isHeld())
                    wakeLock.release();
                if (arg1.getY() < 0) {
                    // discard the recorded audio.
                    voiceRecorder.discardRecording();

                } else {
                    // stop recording and send voice file
                    String st1 = getResources().getString(R.string.Recording_without_permission);
                    String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                    String st3 = getResources().getString(R.string.send_failure_please);
                        int length = voiceRecorder.stopRecoding();
                        if (length > 0) {
                            sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(userName),
                                    Integer.toString(length), false);
                        } else if (length == EMError.INVALID_FILE) {
                            Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                        }
                }
                return true;
            default:
                recordContainer.setVisibility(View.INVISIBLE);
                return false;
        }
        
    }
       
   }
   
   /**
    * 发送语音
    * 
    * @param filePath
    * @param fileName
    * @param length
    * @param isResend
    */
   private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
       if (!(new File(filePath).exists())) {
           return;
       }
       try {
           final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
           // 如果是群聊，设置chattype,默认是单聊
           message.setReceipt(userName);
           int len = Integer.parseInt(length);
           VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
           message.addBody(body);

           conversation.addMessage(message);
           adapter.refresh();
           listview.setSelection(listview.getCount() - 1);
           setResult(RESULT_OK);
           // send file
           // sendVoiceSub(filePath, fileName, message);
       } catch (Exception e) {
           e.printStackTrace();
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
