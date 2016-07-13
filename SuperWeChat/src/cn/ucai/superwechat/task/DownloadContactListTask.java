package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

public class DownloadContactListTask {
    private static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String username;

    public DownloadContactListTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
    }

    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        try {
                            Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                            Log.e(TAG,"result="+result);
                            if(result!=null && result.isRetMsg()){
                                List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                                if(list!=null) {
                                    Log.e(TAG, "DownloadContactList,contacts size=" + list.size());
                                    ArrayList<UserAvatar> contactList =
                                            SuperWeChatApplication.getInstance().getContactList();
                                    contactList.clear();
                                    contactList.addAll(list);
                                    HashMap<String, UserAvatar> userList =
                                            SuperWeChatApplication.getInstance().getUserList();
                                    userList.clear();
                                    for (UserAvatar c : list) {
                                        userList.put(c.getMUserName(), c);
                                    }
                                    mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"onError,error="+error);
                    }
                });
    }
}
