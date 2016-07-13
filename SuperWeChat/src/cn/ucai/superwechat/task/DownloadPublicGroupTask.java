package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Pager;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;


public class DownloadPublicGroupTask {
    private static final String TAG = DownloadPublicGroupTask.class.getName();
    Context mContext;
    String username;
    int pageId;
    int pageSize;

    public DownloadPublicGroupTask(Context mContext, String username, int pageId, int pageSize) {
        this.mContext = mContext;
        this.username = username;
        this.pageId = pageId;
        this.pageSize = pageSize;
    }

    public void execute(){
        OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,pageSize+"")
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            Result result = Utils.getPageResultFromJson(s, GroupAvatar.class);
                            Log.e(TAG,"result="+result);
                            if(result!=null && result.isRetMsg()){
                                Pager pager = (Pager) result.getRetData();
                                if(pager!=null){
                                    Log.e(TAG,"pager="+pager);

                                    ArrayList<GroupAvatar> list = (ArrayList<GroupAvatar>) pager.getPageData();
                                    Log.e(TAG,"public group list="+list.size());
                                    ArrayList<GroupAvatar> publicGroupList =
                                            SuperWeChatApplication.getInstance().getPublicGroup();
                                    for (GroupAvatar g : list) {
                                        if (!publicGroupList.contains(g)) {
                                            publicGroupList.add(g);
                                        }
                                    }
                                    mContext.sendStickyBroadcast(new Intent("update_public_group"));
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
