package cn.ucai.superwechat.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.UserAvatar;

public class UserUtils {

    public static UserAvatar getUserInfo(String username){
        UserAvatar user = null;
        HashMap<String, UserAvatar> userList = SuperWeChatApplication.getInstance().getUserList();
        if(userList==null)return null;
        user = userList.get(username);
        return user;
    }

    public static String getUserAvatarPath(String username){
        if(username==null || username.isEmpty())return null;
        UserAvatar user = getUserInfo(username);
        return getUserAvatarPath(user);
    }

    public static String getUserAvatarPath(UserAvatar user){
        if(user==null)return null;
        return getAvatarPath(user.getMUserName(),user.getMAvatarPath(),user.getMAvatarSuffix());
    }

    public static String getUserAvatarPathByUserName(String username){
        if(username==null || username.isEmpty())return null;
        return getAvatarPath(username,I.AVATAR_TYPE_USER_PATH,I.AVATAR_SUFFIX_JPG);
    }

    public static String getAvatarPath(String avatarName,String avatarType,String suffix){
        StringBuffer sb = new StringBuffer();
        sb.append(I.SERVER_ROOT)
                .append(I.REQUEST_DOWNLOAD_AVATAR)
                .append(I.QUESTION)
                .append(I.NAME_OR_HXID)
                .append(I.EQUAL)
                .append(avatarName)
                .append(I.AMPERSAND)
                .append(I.AVATAR_TYPE)
                .append(I.EQUAL)
                .append(avatarType)
                .append(I.AMPERSAND)
                .append(I.Avatar.AVATAR_SUFFIX)
                .append(I.EQUAL)
                .append(suffix)
                .append(I.AMPERSAND)
                .append(I.WIDTH)
                .append(I.EQUAL)
                .append(I.WIDTH_DEFAULT)
                .append(I.AMPERSAND)
                .append(I.HEIGHT)
                .append(I.EQUAL)
                .append(I.HEIGHT_DEFAULT);
        return sb.toString();
    }

    /**
     * 设置用户头像
     * @param context
     * @param path
     * @param imageView
     */
    public static void setUserAvatar(Context context, String path, ImageView imageView){
        if(path != null){
            try {
                int avatarResId = Integer.parseInt(path);
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //正常的string路径
                Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.easeui.R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(com.hyphenate.easeui.R.drawable.ease_default_avatar).into(imageView);
        }
    }
}
