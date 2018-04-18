package com.tencent.sample.activitys;

import com.tencent.sample.BaseUIListener;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.open.wpa.WPA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WPAActivity extends BaseActivity implements OnClickListener {
    EditText uinText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("临时会话");
		setLeftButtonEnable();
		setContentView(R.layout.wpa_activity);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(this);
			}
		}
        uinText = new EditText(this);
        checkTencentInstance();
	}

	/**
	 * 异步显示结果
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Context context = WPAActivity.this;
				String response = msg.getData().getString("response");
				if (response != null) {
					// 换行显示
					response = response.replace(",", "\r\n");
					AlertDialog dialog = new AlertDialog.Builder(context)
							.setMessage(response)
							.setNegativeButton("知道啦", null).create();
					dialog.show();
				}
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wpa_state_btn:
			onClickGetWPAOnline();
			break;
		case R.id.start_wpa_btn:
			onClickStartWPA();
			break;
		case R.id.start_qq_group_btn:
		    onClickStartQQGroupWPA();
             break;
		}
	}

	/**
	 * 发起wpa会话
	 */
	private void onClickStartWPA() {
        buildDialog("请输入对方QQ号", 
                new EditText(this),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uin = uinText.getText().toString().trim();
                        if (!"".equals(uin)) {
                    int ret = MainActivity.mTencent.startWPAConversation(WPAActivity.this, uin, "");
                            if (ret != 0) {
                                Toast.makeText(getApplicationContext(),
                                        "start WPA conversation failed. error:" + ret,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
	}

    /**
     * 发起QQ群会话
     */
    private void onClickStartQQGroupWPA() {
        buildDialog("请输入群号 \n1. 手Q已登陆 2. 群号为登陆QQ的群号码",
                new EditText(this),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uin = uinText.getText().toString().trim();
                if (!"".equals(uin)) {
                    int ret = MainActivity.mTencent.startWPAConversation(WPAActivity.this, WPA.CHAT_TYPE_GROUP, uin, "");
                    if (ret != 0) {
                        Toast.makeText(getApplicationContext(), "start WPA conversation failed. error:" + ret,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

	/**
	 * 获取wpa用户在线状态，0表示离线，1表示在线
	 */
	private void onClickGetWPAOnline() {
        buildDialog("请输入对方QQ号",
                new EditText(this),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uin = uinText.getText().toString();
                        MainActivity.mTencent.getWPAUserOnlineState(uin, new WPAApiListener("get_uin_state", false, WPAActivity.this));
                        Util.showProgressDialog(WPAActivity.this, null, null);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
	}

    /**
     * 创建Dialog会话
     */
    private void buildDialog(String title, EditText inputText, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        uinText = inputText;
        new AlertDialog.Builder(this).setTitle(title).setView(inputText)
                .setPositiveButton(R.string.app_ok, positiveListener)
                .setNegativeButton(R.string.app_cancel, negativeListener).show();
    }

	private class WPAApiListener extends BaseUIListener {
		private String mScope = "all";
        private Boolean mNeedReAuth = false;
        private Activity mActivity;
    	public WPAApiListener(String scope, boolean needReAuth,
				Activity activity) {
			super(activity);
			this.mScope = scope;
			this.mNeedReAuth = needReAuth;
			this.mActivity = activity;
		}

		@Override
		public void onComplete(Object response) {
			Message msg = mHandler.obtainMessage(0);
			Bundle data = new Bundle();
			data.putString("response", response.toString());
			msg.setData(data);
			mHandler.sendMessage(msg);
			Util.dismissDialog();
		}
	}
}
