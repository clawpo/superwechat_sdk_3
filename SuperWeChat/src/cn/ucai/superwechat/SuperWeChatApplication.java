/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
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
package cn.ucai.superwechat;

import android.app.Application;
import android.content.Context;

import com.easemob.redpacketsdk.RedPacket;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.UserAvatar;

public class SuperWeChatApplication extends Application {

	public static Context applicationContext;
	private static SuperWeChatApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";

	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
        instance = this;
        
        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
		RedPacket.getInstance().initContext(applicationContext);
	}

	public static SuperWeChatApplication getInstance() {
		return instance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

    /**全局的当前登录用户对象*/
	private UserAvatar user;
    /**全局的当前登录用户的好友列表*/
    private ArrayList<UserAvatar> contactList = new ArrayList<>();
    /**全局的当前登录用户的好友集合*/
    private HashMap<String,UserAvatar> userList = new HashMap<>();
    /**全局的群组集合*/
    private ArrayList<GroupAvatar> groupList = new ArrayList<>();
    /**全局的当前公共群列表*/
    private ArrayList<GroupAvatar> publicGroup = new ArrayList<>();
    /**全局的群组成员列表*/
    private HashMap<String,ArrayList<MemberUserAvatar>> groupMembers = new HashMap<>();

    public UserAvatar getUser() {
        return user;
    }

    public void setUser(UserAvatar user) {
        this.user = user;
    }

    public ArrayList<UserAvatar> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<UserAvatar> contactList) {
        this.contactList = contactList;
    }

    public HashMap<String, UserAvatar> getUserList() {
        return userList;
    }

    public void setUserList(HashMap<String, UserAvatar> userList) {
        this.userList = userList;
    }

    public ArrayList<GroupAvatar> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<GroupAvatar> groupList) {
        this.groupList = groupList;
    }

    public ArrayList<GroupAvatar> getPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(ArrayList<GroupAvatar> publicGroup) {
        this.publicGroup = publicGroup;
    }

    public HashMap<String, ArrayList<MemberUserAvatar>> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(HashMap<String, ArrayList<MemberUserAvatar>> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
