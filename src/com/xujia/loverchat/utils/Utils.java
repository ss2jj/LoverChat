package com.xujia.loverchat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.xujia.loverchat.R;
import com.xujia.loverchat.control.BaseApplication;

public class Utils {

    
    /*
     * 显示toast
     */
    public static void showToast(String message)    {
        Toast.makeText(BaseApplication.getGlobalContext(), message, 1000).show();
    }
    
    //判断当前网络是否有可用
    public static boolean isNetworkActive() {
        boolean isWork = false;
        ConnectivityManager manager = (ConnectivityManager) BaseApplication.getGlobalContext().getSystemService(BaseApplication.getGlobalContext().CONNECTIVITY_SERVICE);
        NetworkInfo networks  = manager.getActiveNetworkInfo();
         if(networks != null)   {
             isWork = networks.isAvailable();
         }
        return isWork;
    }

    //根据消息内容和消息类型获取消息内容提示
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct == EMMessage.Direct.RECEIVE) {
                //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                digest = getString(context, R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                digest = getString(context, R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息
            digest = getString(context, R.string.picture);
            break;
        case VOICE:// 语音消息
            digest = getString(context, R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = getString(context, R.string.video);
            break;
        case TXT: // 文本消息
            if(!message.getBooleanAttribute(Consts.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            }else{
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = getString(context, R.string.voice_call) + txtBody.getMessage();
            }
            break;
        case FILE: //普通文件消息
            digest = getString(context, R.string.file);
            break;
        default:
            System.err.println("error, unknow type");
            return "";
        }

        return digest;
    }
    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }
}
