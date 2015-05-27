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

/**
 * UI Demo HX Model implementation
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xujia.loverchat.utils.Consts;
import com.xujia.loverchat.utils.PreferenceUtils;

/**
 * HuanXin default SDK Model implementation
 * @author easemob
 *
 */
public class DefaultHXSDKModel extends HXSDKModel{
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PWD = "pwd";
    protected Context context = null;
    
    public DefaultHXSDKModel(Context ctx){
        context = ctx;
    }
    
    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {
        // TODO Auto-generated method stub
       // HXPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
        PreferenceUtils.saveValue(Consts.msgNotification, paramBoolean?"true":"false", Consts.userPre);
    }

    @Override
    public boolean getSettingMsgNotification() {
        // TODO Auto-generated method stub
        //return HXPreferenceUtils.getInstance().getSettingMsgNotification();
        return PreferenceUtils.getValue(Consts.msgNotification, "unknow", Consts.userPre).equals("true")?true:false;
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {
        // TODO Auto-generated method stub
        PreferenceUtils.saveValue(Consts.msgSound, paramBoolean?"true":"false", Consts.userPre);
    }

    @Override
    public boolean getSettingMsgSound() {
        // TODO Auto-generated method stub
        return PreferenceUtils.getValue(Consts.msgSound, "unknow", Consts.userPre).equals("true")?true:false;
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {
        // TODO Auto-generated method stub
        PreferenceUtils.saveValue(Consts.msgVibrate, paramBoolean?"true":"false", Consts.userPre);
    }

    @Override
    public boolean getSettingMsgVibrate() {
        // TODO Auto-generated method stub
        return PreferenceUtils.getValue(Consts.msgVibrate, "unknow", Consts.userPre).equals("true")?true:false;
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        // TODO Auto-generated method stub
        PreferenceUtils.saveValue(Consts.msgSpeaker, paramBoolean?"true":"false", Consts.userPre);
    }

    @Override
    public boolean getSettingMsgSpeaker() {
        // TODO Auto-generated method stub
        return PreferenceUtils.getValue(Consts.msgSpeaker, "unknow", Consts.userPre).equals("true")?true:false;
    }

    @Override
    public boolean getUseHXRoster() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean saveHXId(String hxId) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, hxId).commit();
    }

    @Override
    public String getHXId() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }

    @Override
    public boolean savePassword(String pwd) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_PWD, pwd).commit();    
    }

    @Override
    public String getPwd() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_PWD, null);
    }

    @Override
    public String getAppProcessName() {
        // TODO Auto-generated method stub
        return context.getPackageName();
    }
}
