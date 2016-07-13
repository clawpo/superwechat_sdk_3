package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

public class DownloadAllGroupTask {
    private static final String TAG = DownloadAllGroupTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadAllGroupTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
    }

    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            Result result = (Result) Utils.getListResultFromJson(s, GroupAvatar.class);
                            Log.e(TAG,"result="+result);
                            if(result!=null && result.isRetMsg()){
                                List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                                Log.e(TAG, "DownloadAllGroup,groups size=" + list.size());
                                ArrayList<GroupAvatar> groupList =
                                        SuperWeChatApplication.getInstance().getGroupList();
                                groupList.clear();
                                groupList.addAll(list);
                                mContext.sendStickyBroadcast(new Intent("update_group_list"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };


                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"onError,error="+error);
                    }
                });
    }
}
