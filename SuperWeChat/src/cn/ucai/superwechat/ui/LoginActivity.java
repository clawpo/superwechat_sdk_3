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
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.ucai.superwechat.DemoHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.db.DemoDBManager;
import cn.ucai.superwechat.db.EMUserDao;
import cn.ucai.superwechat.task.DownloadAllGroupTask;
import cn.ucai.superwechat.task.DownloadContactListTask;
import cn.ucai.superwechat.task.DownloadPublicGroupTask;
import cn.ucai.superwechat.utils.UserUtils;
import cn.ucai.superwechat.utils.Utils;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private boolean autoLogin = false;

	private String currentUsername;
	private String currentPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 如果登录成功过，直接进入主页面
		if (DemoHelper.getInstance().isLoggedIn()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}
		setContentView(R.layout.em_activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);

		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		if (DemoHelper.getInstance().getCurrentUsernName() != null) {
			usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
		}
	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		// After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
		// close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);
        
		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		Log.d(TAG, "EMClient.getInstance().login");
		EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "login: onSuccess");

				if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}

				// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
				// ** manually load all local groups and
			    EMClient.getInstance().groupManager().loadAllGroups();
			    EMClient.getInstance().chatManager().loadAllConversations();

				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
						SuperWeChatApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				//异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
				DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                loginAppServer();

				// 进入主页面
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);

				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
				Log.d(TAG, "login: onProgress");
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d(TAG, "login: onError: " + code);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

    private void loginAppServer() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,currentUsername)
                .addParam(I.User.PASSWORD,currentPassword)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"result="+s);
                        if(s!=null && !s.isEmpty()){
                            Result result = Utils.getResultFromJson(s,UserAvatar.class);
                            Log.e(TAG,"useravatar="+result);
                            if(result.isRetMsg()) {
                                UserAvatar user = (UserAvatar) result.getRetData();
                                SuperWeChatApplication.getInstance().setUser(user);
                                Log.e(TAG, "user=" + SuperWeChatApplication.getInstance().getUser());
                                EMUserDao dao = new EMUserDao(LoginActivity.this);
                                dao.saveUser(user);

//                                DemoHelper.getInstance().getUserProfileManager().updataCurrentUserAvatar(UserUtils.getUserAvatarPath(user));
                                boolean updatenick = DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(user.getMUserNick());
                                Log.e(TAG,"update nick="+user.getMUserNick()+",result="+updatenick);
                                downloadUserAvatarFromAppServer(user);
//                                Glide.with(getApplicationContext()).load(UserUtils.getUserAvatarPath(user))
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.easeui.R.drawable.ease_default_avatar);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG,"start download contact,group,public group");
                                        //下载联系人集合
                                        new DownloadContactListTask(LoginActivity.this,currentUsername).execute();
                                        //下载群组集合
                                        new DownloadAllGroupTask(LoginActivity.this,currentUsername).execute();
                                        //下载公开群组集合
                                        new DownloadPublicGroupTask(LoginActivity.this,currentUsername,
                                                I.PAGE_ID_DEFAULT,I.PAGE_SIZE_DEFAULT).execute();
                                    }
                                });
                            }else{
                                Log.e(TAG, "error=" + result.getRetCode());
                                Toast.makeText(getApplicationContext(), Utils.getResourceString(LoginActivity.this,result.getRetCode()), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.Login_failed),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }

    private void downloadUserAvatarFromAppServer(UserAvatar user) {
        Log.e(TAG,"start download avatar....");
        final OkHttpUtils2<Message> utils = new OkHttpUtils2<>();
        utils.url(UserUtils.getUserAvatarPath(user))
            .targetClass(Message.class)
            .doInBackground(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG,"download avatar fail:"+e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    byte[] data = response.body().bytes();
                    Log.e(TAG,"data="+data);
                    String avatar = DemoHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                    Log.e(TAG,"download avatar,"+avatar);
                }
            })
        .execute(new OkHttpUtils2.OnCompleteListener<Message>() {
            @Override
            public void onSuccess(Message result) {
                Log.e(TAG,"resule="+result);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"download avatar error:"+error);
            }
        });
    }


    /**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}
}
