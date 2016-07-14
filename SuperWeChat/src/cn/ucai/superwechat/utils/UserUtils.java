package cn.ucai.superwechat.utils;

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
        String path = null;
        UserAvatar user = getUserInfo(username);
        if(user!=null){
            StringBuffer sb = new StringBuffer();
            sb.append(I.SERVER_ROOT)
                    .append(I.REQUEST_DOWNLOAD_AVATAR)
                    .append(I.QUESTION)
                    .append(I.NAME_OR_HXID)
                    .append(I.EQUAL)
                    .append(username)
                    .append(I.AMPERSAND)
                    .append(I.AVATAR_TYPE)
                    .append(I.EQUAL)
                    .append(I.AVATAR_TYPE_USER_PATH)
                    .append(I.AMPERSAND)
                    .append(I.Avatar.AVATAR_SUFFIX)
                    .append(I.EQUAL)
                    .append(user.getMAvatarSuffix())
                    .append(I.AMPERSAND)
                    .append(I.WIDTH)
                    .append(I.EQUAL)
                    .append(I.WIDTH_DEFAULT)
                    .append(I.AMPERSAND)
                    .append(I.HEIGHT)
                    .append(I.EQUAL)
                    .append(I.HEIGHT_DEFAULT);
            path = sb.toString();
        }
        return path;
    }
}
