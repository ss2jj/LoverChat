
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.view.PasteEditText;

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
        speak.setOnTouchListener(new SpeakListener());
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
}
