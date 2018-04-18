package com.tencent.sample.activitys;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.sample.BaseUIListener;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.open.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.tencent.open.utils.SystemUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class TQQInfoActivity extends BaseActivity implements OnClickListener {
	private String mLastAddTweetId = null;
	private Weibo mWeibo = null;
    private static final int REQUEST_ADD_PIC_T = 1001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setBarTitle("腾讯微博");
		setLeftButtonEnable();
		setContentView(R.layout.tqq_info_activity);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(this);
			}
		}
		mWeibo = new Weibo(this, MainActivity.mTencent.getQQToken());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_tqq_info_btn:
			onClickGetInfo();
			break;
		case R.id.add_t_btn:
			onClickAddTweet();
			break;
		case R.id.del_t_btn:
			onClickDeleteTweet();
			break;
		case R.id.add_pic_t_btn:
			onClickAddPicUrlTweet();
			break;
		}

	}

//	/**
//	 * 发送带图微博
//	 */
//	private void onClickAddPicUrlTweet() {
//		if (MainActivity.ready(TQQInfoActivity.this)) {
//			Bundle bundle = new Bundle();
//			bundle.putString("format", "json");
//			bundle.putString("content", "test add pic with url");
//			// params.putString("clientip", "127.0.0.1");
//
//			// 把 bitmap 转换为 byteArray , 用于发送请求
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//					R.drawable.ic_launcher);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
//			byte[] buff = baos.toByteArray();
//			// Log.v(TAG, "length: " + buff.length);
//			bundle.putByteArray("pic", buff);
//			mTencent.requestAsync(Constants.GRAPH_ADD_PIC_T, bundle,
//					Constants.HTTP_POST,
//					new TQQApiListener("add_pic_t", false,TQQInfoActivity.this), null);
//			mWeibo.sendPicText(content, picPath, new TQQ);
//			bitmap.recycle();
//			Util.showProgressDialog(TQQInfoActivity.this, null, null);
//		}
//	}

	private void onClickAddPicUrlTweet() {
		if (MainActivity.ready(TQQInfoActivity.this)) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_ADD_PIC_T);
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (data != null) {
    		if (requestCode == REQUEST_ADD_PIC_T) {
            	doSendPicWeibo(data.getData());
            }
    	}
    }
    private void doSendPicWeibo(Uri uri) {
    	String filePath = Util.getPath(this, uri);
    	mWeibo.sendPicText("互联sdk测试发送微博", filePath,
    			new TQQApiListener("add_t", false,TQQInfoActivity.this));
    	Util.showProgressDialog(TQQInfoActivity.this, null, null);
    }
	/**
	 * 发送纯文本微博
	 */
	private void onClickAddTweet() {
		if (MainActivity.ready(TQQInfoActivity.this)) {
			String content = "test add tweet";
			mWeibo.sendText(content, new TQQApiListener("add_t", false,TQQInfoActivity.this));
			Util.showProgressDialog(TQQInfoActivity.this, null, null);
		}
	}

	/**
	 * 取得微博用户个人信息
	 */
	private void onClickGetInfo() {
		if (MainActivity.ready(TQQInfoActivity.this)) {
			mWeibo.getWeiboInfo(new TQQApiListener("get_info", false,TQQInfoActivity.this));
			Util.showProgressDialog(TQQInfoActivity.this, null, null);
		}
	}

	/**
	 * 删除用本应用发表的最后一篇微博
	 */
	private void onClickDeleteTweet() {
		if (mLastAddTweetId == null) {
			Util.toastMessage(TQQInfoActivity.this, "请先用本应用发表一篇微博");
			return;
		}
		if (MainActivity.ready(TQQInfoActivity.this)) {
			mWeibo.deleteText(mLastAddTweetId, new TQQApiListener("del_t", false,TQQInfoActivity.this));
			Util.showProgressDialog(TQQInfoActivity.this, null, null);
		}
		mLastAddTweetId = null;
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			Util.toastMessage(TQQInfoActivity.this,
					"BaseUiListener ononComplete: " + response.toString());
		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(TQQInfoActivity.this, "BaseUiListener onError: "
					+ e.errorDetail);
		}

		@Override
		public void onCancel() {
			Util.toastMessage(TQQInfoActivity.this, "BaseUiListener onCancel.");
		}
	}


	private class TQQApiListener extends BaseUIListener {
		private String mScope = "all";
        private Boolean mNeedReAuth = false;
        private Activity mActivity;
    	public TQQApiListener(String scope, boolean needReAuth,
				Activity activity) {
			super(activity);
			this.mScope = scope;
			this.mNeedReAuth = needReAuth;
			this.mActivity = activity;
		}

		@Override
		public void onComplete(Object response) {
			final Activity activity = TQQInfoActivity.this;
			try {
				JSONObject json =(JSONObject)response;
				int ret = json.getInt("ret");
				if (json.has("data")) {
					JSONObject data = json.getJSONObject("data");
					if (data.has("id")) {
						mLastAddTweetId = data.getString("id");
					}
				}
				if (ret == 0) {
					Message msg = mHandler.obtainMessage(0, mScope);
					Bundle data = new Bundle();
					data.putString("response", response.toString());
					msg.setData(data);
					mHandler.sendMessage(msg);
				} else if (ret == 100030) {
					if (mNeedReAuth) {
						Runnable r = new Runnable() {
							public void run() {
								MainActivity.mTencent.reAuth(activity,
										mScope, new BaseUIListener(TQQInfoActivity.this));
							}
						};
						TQQInfoActivity.this.runOnUiThread(r);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Util.toastMessage(TQQInfoActivity.this,
						"onComplete() JSONException: " + response.toString());
			}
			Util.dismissDialog();
		}
	}

	/**
	 * 异步显示结果
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Context context = TQQInfoActivity.this;
			String scope = msg.obj.toString();
			String response = msg.getData().getString("response");
			Log.d("TQQInfoActivity", "resposne = " + response + " scope = "
					+ scope);
			if (response != null) {
				// 换行显示
				response = response.replace(",", "\r\n");
			}
			AlertDialog dialog = new AlertDialog.Builder(context)
					.setMessage(response).setNegativeButton("知道啦", null)
					.create();
			if (scope.equals("get_info")) {
				dialog.setTitle("用户资料");
			} else if (scope.equals("add_t")) {
				dialog.setTitle("发送微博");
			} else if (scope.equals("add_pic_t")) {
				dialog.setTitle("发送带图微博");
			} else if (scope.equals("del_t")) {
				dialog.setTitle("删除微博");
			}
			dialog.show();
		};
	};
}
