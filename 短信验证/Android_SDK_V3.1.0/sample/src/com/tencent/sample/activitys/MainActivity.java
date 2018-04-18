package com.tencent.sample.activitys;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.sample.AppConstants;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.tencent.tauth.Tencent;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
    public static String mAppid;
	private Button mNewLoginButton;
    private Button mServerSideLoginBtn;
	private TextView mUserInfo;
	private ImageView mUserLogo;
    private UserInfo mInfo;
	private EditText mEtAppid = null;
	public static Tencent mTencent;
    private static Intent mPrizeIntent = null;
    private static boolean isServerSideLogin = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "-->onCreate");
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//
		// 固定竖屏
		setContentView(R.layout.activity_main_new);
		initViews();
		//setBarTitle("demo菜单");
		if (TextUtils.isEmpty(mAppid)) {
			mAppid = "222222";
    		mEtAppid = new EditText(this);
    		mEtAppid.setText(mAppid);
			try {
				new AlertDialog.Builder(this).setTitle("请输入APP_ID")
						.setCancelable(false)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(mEtAppid)
						.setPositiveButton("Commit", mAppidCommitListener)
						.setNegativeButton("Use Default", mAppidCommitListener)
						.show();
			} catch (Exception e) {
			}
		} else {
		    if (mTencent == null) {
		        mTencent = Tencent.createInstance(mAppid, this);
		    }
		}
		
        // 获取有奖分享的intent信息
        if (null != getIntent()) {
            mPrizeIntent = getIntent();
        }
    }

    /**
     * 有奖分享处理，未接入有奖分享可以不考虑
     */
    private void handlePrizeShare() {
        // -----------------------------------
        // 下面的注释请勿删除，编译lite版的时候需要删除, 注意//[不要有空格。
        //[liteexludestartmeta]
        if (null == mPrizeIntent || null == mTencent) {
            return;
        }
        // 有奖分享处理
        boolean hasPrize = mTencent.checkPrizeByIntent(this, mPrizeIntent);
        if (hasPrize) {
            Util.showConfirmCancelDialog(this, "有奖品领取", "请使用QQ登录后，领取奖品！", prizeShareConfirmListener);
        }
        //[liteexludeendmeta]
    }

    // -----------------------------------
    // 下面的注释请勿删除，编译lite版的时候需要删除, 注意//[不要有空格。
    //[liteexludestart_flag_one]
    private DialogInterface.OnClickListener prizeShareConfirmListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    boolean isLogin = mTencent.isSessionValid();
                    if (isLogin) {
                        // 本地已经有保存openid和accesstoken的情况下，先调用mTencent.setAccesstoken().
                        // 也可在奖品列表页，主动调用此接口获取未领取的奖品
                        if (null != mPrizeIntent) {
                            mTencent.queryUnexchangePrize(MainActivity.this, mPrizeIntent.getExtras(),
                                    prizeQueryUnexchangeListener);
                        }
                    } else {
                        // 未登陆提示用户使用QQ号登陆
                        onClickLogin();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private IUiListener prizeQueryUnexchangeListener = new IUiListener() {

        @Override
        public void onError(UiError e) {
            Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(MainActivity.this, "onCancel: ");
            Util.dismissDialog();
        }

        @Override
        public void onComplete(Object response) {
            Util.showConfirmCancelDialog(MainActivity.this, "兑换奖品", response.toString(),
                    new PrizeClickExchangeListener(response.toString()));
            // 兑换奖品后，mPrizeIntent 置为空
            mPrizeIntent = null;
        }
    };

    private IUiListener prizeExchangeListener = new IUiListener() {

        @Override
        public void onError(UiError e) {
            Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(MainActivity.this, "onCancel: ");
            Util.dismissDialog();
        }

        @Override
        public void onComplete(Object response) {
            Util.showResultDialog(MainActivity.this, response.toString(), "兑换信息");
        }
    };

    private class PrizeClickExchangeListener implements DialogInterface.OnClickListener {
        String response = "";

        PrizeClickExchangeListener(String strResponse) {
            response = strResponse;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (null != mTencent) {
                        Bundle params = new Bundle();
                        ArrayList<String> shareIdList = handlePrizeResponse(response);
                        if (null != shareIdList) {
                            ArrayList<String> list = new ArrayList<String>();
                            // 后台测试环境目前只支持一个shareid的兑换，正式环境会支持多个shareid兑换。
                            list.add(shareIdList.get(0));
                            params.putStringArrayList("shareid_list", list);
                            mTencent.exchangePrize(MainActivity.this, params, prizeExchangeListener);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private ArrayList<String> handlePrizeResponse(String response) {
        ArrayList<String> shareIdList = new ArrayList<String>();
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        try {
            JSONObject obj = new JSONObject(response);
            int code = obj.getInt("ret");
            int subCode = obj.getInt("subCode");
            if (code == 0 && subCode == 0) {
                JSONObject data = obj.getJSONObject("data");
                JSONArray prizeList = data.getJSONArray("prizeList");
                int size = prizeList.length();
                JSONObject prize = null;
                for (int i = 0; i < size; i++) {
                    prize = prizeList.getJSONObject(i);
                    if (null != prize) {
                        shareIdList.add(prize.getString("shareId"));
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return shareIdList;
    }
    //[liteexludeend_flag_one]

	@Override
	protected void onStart() {
		Log.d(TAG, "-->onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "-->onResume");
        // 有奖分享处理
        handlePrizeShare();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "-->onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "-->onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "-->onDestroy");
		super.onDestroy();

	}

	private void initViews() {
		mNewLoginButton = (Button) findViewById(R.id.new_login_btn);
        mServerSideLoginBtn = (Button) findViewById(R.id.server_side_login_btn);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		OnClickListener listener = new NewClickListener();
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(listener);
			}
		}
		mUserInfo = (TextView) findViewById(R.id.user_nickname);
		mUserLogo = (ImageView) findViewById(R.id.user_logo);
		updateLoginButton();
	}

	private void updateLoginButton() {
		if (mTencent != null && mTencent.isSessionValid()) {
            if (isServerSideLogin) {
                mNewLoginButton.setTextColor(Color.BLUE);
                mNewLoginButton.setText("登录");
                mServerSideLoginBtn.setTextColor(Color.RED);
                mServerSideLoginBtn.setText("退出Server-Side账号");
            } else {
                mNewLoginButton.setTextColor(Color.RED);
                mNewLoginButton.setText("退出帐号");
                mServerSideLoginBtn.setTextColor(Color.BLUE);
                mServerSideLoginBtn.setText("Server-Side登陆");
            }
		} else {
			mNewLoginButton.setTextColor(Color.BLUE);
			mNewLoginButton.setText("登录");
            mServerSideLoginBtn.setTextColor(Color.BLUE);
            mServerSideLoginBtn.setText("Server-Side登陆");
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread(){

						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
			mUserInfo.setText("");
			mUserInfo.setVisibility(android.view.View.GONE);
			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mUserInfo.setVisibility(android.view.View.VISIBLE);
						mUserInfo.setText(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else if(msg.what == 1){
				Bitmap bitmap = (Bitmap)msg.obj;
				mUserLogo.setImageBitmap(bitmap);
				mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};

	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
            isServerSideLogin = false;
			Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
		} else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(this);
                mTencent.login(this, "all", loginListener);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
		    mTencent.logout(this);
			updateUserInfo();
			updateLoginButton();
		}
	}

    private void onClickServerSideLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.loginServerSide(this, "all", loginListener);
            isServerSideLogin = true;
            Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            if (!isServerSideLogin) { // SSO模式的登陆，先退出，再进行Server-Side模式登陆
                mTencent.logout(this);
                mTencent.loginServerSide(this, "all", loginListener);
                isServerSideLogin = true;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(this);
            isServerSideLogin = false;
            updateUserInfo();
            updateLoginButton();
        }
    }

	public static String getAppid() {
		if (TextUtils.isEmpty(mAppid)) {
			mAppid = "222222";
		}

		return mAppid;
	}

	public static boolean ready(Context context) {
		if (mTencent == null) {
			return false;
		}
		boolean ready = mTencent.isSessionValid()
				&& mTencent.getQQToken().getOpenId() != null;
		if (!ready) {
            Toast.makeText(context, "login and get openId first, please!",
					Toast.LENGTH_SHORT).show();
        }
		return ready;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.d(TAG, "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
	    if (requestCode == Constants.REQUEST_LOGIN ||
	    	requestCode == Constants.REQUEST_APPBAR) {
	    	Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

	IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            updateUserInfo();
            updateLoginButton();
        }
    };

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(MainActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(MainActivity.this, "返回为空", "登录失败");
                return;
            }
			Util.showResultDialog(MainActivity.this, response.toString(), "登录成功");
            // 有奖分享处理
            handlePrizeShare();
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			Util.toastMessage(MainActivity.this, "onCancel: ");
			Util.dismissDialog();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
		}
	}

	private DialogInterface.OnClickListener mAppidCommitListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mAppid = AppConstants.APP_ID;
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// 用输入的appid
				String editTextContent = mEtAppid.getText().toString().trim();
				if (!TextUtils.isEmpty(editTextContent)) {
				    mAppid = editTextContent;
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				// 默认appid
				break;
			}
			mTencent = Tencent.createInstance(mAppid, MainActivity.this);
            // 有奖分享处理
            handlePrizeShare();
		}
	};

    private void onClickIsSupportSSOLogin() {
        if (mTencent.isSupportSSOLogin(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "支持SSO登陆", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "不支持SSO登陆", Toast.LENGTH_SHORT).show();
        }
    }

	class NewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Context context = v.getContext();
			Animation shake = AnimationUtils.loadAnimation(context,
					R.anim.shake);
			Class<?> cls = null;
			boolean isAppbar = false;
			switch (v.getId()) {
			case R.id.new_login_btn:
				onClickLogin();
				v.startAnimation(shake);
				return;
			case R.id.server_side_login_btn:
			    onClickServerSideLogin();
			    v.startAnimation(shake);
			    return;
			case R.id.main_sso_btn:
                    onClickIsSupportSSOLogin();
			    return;
			case R.id.main_getInfo_btn:
				cls = AccountInfoActivity.class;
				break;
			case R.id.main_qqShare_btn:
				cls = QQShareActivity.class;
				break;
			case R.id.main_qzoneShare_btn:
				cls = QZoneShareActivity.class;
				break;
			case R.id.main_social_api_btn:
                cls = SocialApiActivity.class;
                break;
            //-----------------------------------
            // 下面的注释请勿删除，编译lite版的时候需要删除, 注意//[不要有空格。
            //[liteexludestart]
			case R.id.game_add_friend:
                cls = GameLogicActivity.class;
                break;
			case R.id.main_qzonePic_btn:
				cls = QzonePicturesActivity.class;
				break;
			case R.id.main_tqqInfo_btn:
				cls = TQQInfoActivity.class;
				break;
			case R.id.main_wap_btn:
				cls = WPAActivity.class;
				break;
			case R.id.main_others_btn:
				cls = OtherApiActivity.class;
				break;
			case R.id.main_avatar_btn:
				cls = AvatarActivity.class;
				break;
			case R.id.main_appbar_btn:
				cls = SocialAppbarActivity.class;
				isAppbar = true;
				break;
			case R.id.main_qqgroup_btn:
                cls = QQGroupActivity.class;
                break;
            //[liteexludeend]
			}
			v.startAnimation(shake);
			if (cls != null) {
				Intent intent = new Intent(context, cls);
				if (isAppbar) { //APP内应用吧登录需接收登录结果
					startActivityForResult(intent, Constants.REQUEST_APPBAR);
				} else {
					context.startActivity(intent);
				}
			}
		}
	}
}
