
package com.xujia.loverchat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.xujia.loverchat.R;
import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.PreferenceUtils;
import com.xujia.loverchat.utils.Utils;

public class RegisterActivity extends Activity {
private  EditText userNameEdit;
private EditText passWordEdit;
private EditText confirmPassWordEdit;
private boolean showProgress =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        userNameEdit = (EditText) findViewById(R.id.userNameEdit1);
        passWordEdit = (EditText) findViewById(R.id.passwdEdit1);
        confirmPassWordEdit = (EditText) findViewById(R.id.passwdEdit2);
        
    }

    public void back(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    public void register(View view) {
        if(!Utils.isNetworkActive())  {
            Utils.showToast(getString(R.string.network_unavaible));
            return;
        }
        final String userName = userNameEdit.getText().toString().trim();
        final String passWord = passWordEdit.getText().toString().trim();
        final String confirmPassWord = confirmPassWordEdit.getText().toString().trim();
      if(TextUtils.isEmpty(userName))   {
          Utils.showToast(getString(R.string.username_unavaible));
          return;
      }
      if(TextUtils.isEmpty(passWord))    {
          Utils.showToast(getString(R.string.password_unavaible));
          return;
      }
      if(TextUtils.isEmpty(confirmPassWord))    {
          Utils.showToast(getString(R.string.confirm_password_unavaible));
          return;
      }
      if(!passWord.equals(confirmPassWord)) {
          Utils.showToast(getString(R.string.unsame_password));
          return;
      }
      
     final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
      pd.setCanceledOnTouchOutside(false);
      pd.setMessage(getString(R.string.is_registig));
      pd.show();
      new Thread(new Runnable() {
          public void run() {
            try {
               // 调用sdk注册方法
               EMChatManager.getInstance().createAccountOnServer(userName, passWord);
               runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if(!RegisterActivity.this.isFinishing())    {
                        pd.dismiss();
                        //登录成功 保存用户名 密码
                        PreferenceUtils.saveValue(Consts.userName, userName, Consts.userPre);
                        PreferenceUtils.saveValue(Consts.password, passWord, Consts.userPre);
                        Utils.showToast(getString(R.string.register_success));
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();
                    }
                    
                }
            });
            } catch (final EaseMobException e) {
            //注册失败
              //String errorMsg = e.getMessage();
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if(!RegisterActivity.this.isFinishing())    {
                            pd.dismiss();
                         }
                    }
                });
              int errorCode=e.getErrorCode();
              if(errorCode==EMError.NONETWORK_ERROR){
                  Utils.showToast(getString(R.string.network_unavaible));
              }else if(errorCode==EMError.USER_ALREADY_EXISTS){
                  Utils.showToast(getString(R.string.user_conflict));
              }else if(errorCode==EMError.UNAUTHORIZED){
                  Utils.showToast(getString(R.string.register_fail));
              }else{
                  Utils.showToast(getString(R.string.register_fail)+e.getMessage());
            }
         }
          } }).start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

}
