/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xujia.loverchat.control;

import java.util.Iterator;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.analytics.EMMessageCollector;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


public  class HXSDKHelper {
    private static final String TAG = "HXSDKHelper";

    protected Context appContext = null;
    private boolean sdkInited = false;
    private EMConnectionListener connectionListener;
    private String hxId = null;
    private String password = null;
    private HXSDKModel hxModel;
    private static HXSDKHelper me = null;
    
    private HXSDKHelper(){
      //  me = this;
    }
    
    //初始化环信环境
    public synchronized boolean onInit(Context context){
        if(sdkInited){
            return true;
        }
        appContext = context;
        hxModel = new DefaultHXSDKModel(appContext);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.d(TAG, "process app name : " + processAppName);
        if (processAppName == null || !processAppName.equalsIgnoreCase(hxModel.getAppProcessName()))   {
            Log.e(TAG, "enter the service process!");
            return false;
        }
      
        // 初始化环信SDK,要先调用init()
        EMChat.getInstance().init(context);
        
        // 设置sandbox测试环境
        if(hxModel.isSandboxMode()){
         EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
        }
         // set debug mode in development process
        if(hxModel.isDebugMode())   {
        EMChat.getInstance().setDebugMode(true);    
        }
        Log.d(TAG, "initialize EMChat SDK");
        initHXOptions();
        initListener();
        sdkInited = true;
        return true;
    }
    private String getAppProcessName()  {
        String processName = "";
        processName =  appContext.getPackageName();
        return processName;
    }
    /**
     * get global instance
     * @return
     */
    public static HXSDKHelper getInstance(){
                if(me == null)
                   me = new HXSDKHelper();
                return me;
    }

    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    protected void initHXOptions(){
        Log.d(TAG, "init HuanXin Options");
        
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(hxModel.getAcceptInvitationAlways());
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(hxModel.getUseHXRoster());
        // 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
        options.setNotifyBySoundAndVibrate(hxModel.getSettingMsgNotification());
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(hxModel.getSettingMsgSound());
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(hxModel.getSettingMsgVibrate());
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(hxModel.getSettingMsgSpeaker());
        // 设置是否需要已读回执
        options.setRequireAck(hxModel.getRequireReadAck());
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(hxModel.getRequireDeliveryAck());
        // 设置notification消息点击时，跳转的intent为自定义的intent
        options.setOnNotificationClickListener(getNotificationClickListener());
        options.setNotifyText(getMessageNotifyListener());
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(1);
    }
    
    /**
     * logout HuanXin SDK
     */
    public void logout(final EMCallBack callback){
              EMChatManager.getInstance().logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
    
    /**
     * �?��是否已经登录�?     * @return
     */
    public boolean isLogined(){
       return EMChat.getInstance().isLoggedIn();
    }
    
    /**
     * get the message notify listener
     * @return
     */
    protected OnMessageNotifyListener getMessageNotifyListener(){
        return null;
    }
    
    /**
     *get notification click listener
     */
    protected OnNotificationClickListener getNotificationClickListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    protected void initListener(){
   
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }else{
                    onConnectionDisconnected(error);
                }
            }

            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }
    
    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}

    
    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}
    
    
    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}
    
    /**
     * handle the connection disconnect
     * @param error see {@link EMError}
     */
    protected void onConnectionDisconnected(int error){}

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
