
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;

public class ConversationListActivity extends Activity {
private TextView userNameText;
private String userName;
private InputMethodManager manager;
private LinearLayout speak,more,face,container;
private RelativeLayout edit;
private Button speakButton,editButton;
private ImageView normalEmotion,checkEmotion;
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
        if(more.isShown())  {
            more.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            more.setVisibility(View.VISIBLE);
            face.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
          
        }
    }
    public void onClick(View view)  {
        if(view.getId() == R.id.iv_emoticons_normal)    {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_list, menu);
        return true;
    }

}
