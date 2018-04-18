package com.tencent.sample.activitys;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.connect.common.Constants;
import com.tencent.open.yyb.AppbarAgent;
import com.tencent.sample.R;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SocialAppbarActivity extends BaseActivity implements
		View.OnClickListener {
	private Tencent mTencent;
	private AlertDialog mLabelDialog;
	private AlertDialog mThreadDialog;
	private EditText mKeyEdit;
	private EditText mThreadIdEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("应用吧");
		setLeftButtonEnable();

		setContentView(R.layout.social_appbar_layout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(this);
			}
		}
		mTencent = Tencent.createInstance(MainActivity.mAppid, SocialAppbarActivity.this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.appbarh5:
//			mTencent.startAppbarForceH5(this);
//			break;
		case R.id.appbar_detail:
			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_DETAIL);
			break;
		case R.id.appbar_my_message:
			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_NEWS);
			break;
		case R.id.appbar_send_blog:
			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_SEND_BLOG);
			break;
		case R.id.appbar_label:
		    showLabelDialog();
		    break;
//		case R.id.appbar_detail_h5:
//			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_DETAIL, true);
//			break;
//		case R.id.appbar_my_message_h5:
//			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_NEWS, true);
//			break;
//		case R.id.appbar_send_blog_h5:
//			mTencent.startAppbar(this, AppbarAgent.TO_APPBAR_SEND_BLOG, true);
//			break;
		case R.id.appbar_thread:
			showThreadDialog();
			break;
		default:
			break;
		}
	}
	
	private void showLabelDialog() {
        if (mLabelDialog == null) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.dialog_qqgroup, null);
            TextView textView = (TextView) textEntryView.findViewById(R.id.key_description);
            textView.setText(R.string.label_description);
            mKeyEdit = (EditText) textEntryView.findViewById(R.id.key_edit);
            mLabelDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.qq_group_dialog_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = mKeyEdit.getText().toString();
                        if (TextUtils.isEmpty(key)) {
                            Toast.makeText(SocialAppbarActivity.this, "标签不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            mTencent.startAppbarLabel(SocialAppbarActivity.this, key);
                        }
                    }
                })
                .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
        }
        mKeyEdit.setText("每日互动");
        mLabelDialog.show();
    }
	
	private void showThreadDialog() {
        if (mThreadDialog == null) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.dialog_qqgroup, null);
            TextView textView = (TextView) textEntryView.findViewById(R.id.key_description);
            textView.setText(R.string.threadid_description);
            mThreadIdEdit = (EditText) textEntryView.findViewById(R.id.key_edit);
            mThreadDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.qq_group_dialog_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String threadId = mThreadIdEdit.getText().toString();
                        if (TextUtils.isEmpty(threadId)) {
                            Toast.makeText(SocialAppbarActivity.this, "主题ID不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            mTencent.startAppbarThread(SocialAppbarActivity.this, threadId);
                        }
                    }
                })
                .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
        }
        mThreadIdEdit.setHint("主题ID必须为正整数");
        mThreadDialog.show();
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		/*
		 * 接收app内应用吧登录返回的登录信息
		 */
		if (requestCode == Constants.REQUEST_APPBAR) {
			if (resultCode == Constants.ACTIVITY_OK) {				
				try {
					MainActivity.initOpenidAndToken(new JSONObject(data.getStringExtra(Constants.LOGIN_INFO)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setResult(Constants.ACTIVITY_OK, data); //返回MainActivity界面时刷新UI
			}
		}
	}
}
