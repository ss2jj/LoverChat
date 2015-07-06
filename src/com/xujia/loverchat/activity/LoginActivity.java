
package com.xujia.loverchat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xujia.loverchat.R;
import com.xujia.loverchat.R.layout;
import com.xujia.loverchat.R.menu;
import com.xujia.loverchat.control.HXSDKHelper;
import com.xujia.loverchat.model.UserDao;
import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.PreferenceUtils;
import com.xujia.loverchat.utils.Utils;

import java.util.List;

public class LoginActivity extends Activity {
    private EditText userNameEdit;
    private EditText passWorfEdit;
    private boolean autologin = false;
    private boolean showProgress =false;
    private String userName;
    private String passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       
        //已经登录则跳过此界面不显示 直接进入主界面
        if(HXSDKHelper.getInstance().isLogined())   {
            autologin = true;
           startActivity(new Intent(this,ChatActivity.class));
            return ;
        }
        setContentView(R.layout.activity_login);
        userNameEdit = (EditText)findViewById(R.id.userNameEdit);
        passWorfEdit = (EditText)findViewById(R.id.passwdEdit);
        userNameEdit.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                passWorfEdit.setText(null);
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
        if(PreferenceUtils.getValue(Consts.userName, null, Consts.userPre)!=null)
        userNameEdit.setText(PreferenceUtils.getValue(Consts.userName, null, Consts.userPre));
    }

    //点击登陆按钮
    public void login(View  view) {
      if(!Utils.isNetworkActive())  {
          Utils.showToast(getString(R.string.network_unavaible));
          return;
      }
     userName = userNameEdit.getText().toString().trim();
     passWord = passWorfEdit.getText().toString().trim();
     if(TextUtils.isEmpty(userName))    {
         Utils.showToast(getString(R.string.username_unavaible));
         return;
     }
     if(TextUtils.isEmpty(passWord))    {
         Utils.showToast(getString(R.string.password_unavaible));
         return;
     }
    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
    showProgress = true;
    pd.setCanceledOnTouchOutside(false);
    pd.setOnCancelListener(new OnCancelListener() {
        
        @Override
        public void onCancel(DialogInterface arg0) {
            // TODO Auto-generated method stub
            showProgress = false;
        }
    });
    pd.setMessage(getString(R.string.is_loging));
    pd.show();
    EMChatManager.getInstance().login(userName, passWord, new EMCallBack() {
        
        @Override
        public void onSuccess() {
            // TODO Auto-generated method stub
            if(!showProgress)   {
                return;
            }
            //登录成功 保存用户名 密码
            PreferenceUtils.saveValue(Consts.userName, userName, Consts.userPre);
            PreferenceUtils.saveValue(Consts.password, passWord, Consts.userPre);
            runOnUiThread(new Runnable() {
                public void run() {
                    pd.setMessage(getString(R.string.is_geting_info));
                
                }
            });
            try {
                //登陆时获取好友关系并保存到本地数据库
                List<String> usernames =  EMContactManager.getInstance().getContactUserNames();
                if(usernames != null && usernames.size()>0 ) {
                    UserDao.getInstance().saveUser(usernames.get(0), "yes");
                }
            } catch (EaseMobException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            EMChatManager.getInstance().getAllConversations();
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().updateCurrentUserNick(userName);
            if(!LoginActivity.this.isFinishing())   {
                pd.dismiss();
                //跳转
               startActivity(new Intent(LoginActivity.this,ChatActivity.class));
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Utils.showToast("登录成功");
                    }
                });
               
                finish();
            }
        }
        
        @Override
        public void onProgress(int arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onError(int arg0, String arg1) {
            // TODO Auto-generated method stub
            if (!showProgress) {
                return;
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    pd.dismiss();
                   Utils.showToast(getString(R.string.login_fail));
                }
            });
        }
    });
    }
    //跳转到注册界面
    public void register(View  view)  {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(autologin)   {
            return;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

}
