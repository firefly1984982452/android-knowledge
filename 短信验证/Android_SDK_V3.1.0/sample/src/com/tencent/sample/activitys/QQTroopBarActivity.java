package com.tencent.sample.activitys;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.open.GameAppOperation;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQTroopBarActivity extends BaseActivity implements OnClickListener {
    private String TAG = "QQTroopbarActivity";

    private TextView title = null;
    private TextView troopbarId = null;
    private TextView summary = null;
    private TextView appName = null;
    private TextView imageUrl = null;
    private RadioButton mRadioBtn_localImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("发送至群部落");
        setLeftButtonEnable();
        setContentView(R.layout.qq_troopbar_activity);

        title = (TextView) findViewById(R.id.shareqq_title);
        troopbarId = (TextView) findViewById(R.id.ev_troopid);
        summary = (TextView) findViewById(R.id.shareqq_summary);
        appName = (TextView) findViewById(R.id.shareqq_app_name);
        imageUrl = (TextView) findViewById(R.id.shareqq_image_url);


        findViewById(R.id.shareqq_commit).setOnClickListener(this);
        mRadioBtn_localImage = (RadioButton) findViewById(R.id.radioBtn_local_image);
        mRadioBtn_localImage.setOnClickListener(this);

        checkTencentInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareqq_commit: // 提交
                final Bundle params = new Bundle();
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, appName.getText().toString());
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, title.getText().toString());
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, summary.getText().toString());
                params.putString(GameAppOperation.TROOPBAR_ID, troopbarId.getText().toString());
                String srFileData = imageUrl.getText().toString();
                if (!TextUtils.isEmpty(srFileData)) {
                    ArrayList<String> fileDataList = new ArrayList<String>();
                    srFileData.replace(" ", "");
                    String[] filePaths = srFileData.split(";");
                    int size = filePaths.length;
                    for (int i = 0; i < size; i++) {
                        String path = filePaths[i].trim();
                        if (!TextUtils.isEmpty(path)) {
                            fileDataList.add(path);
                        }
                    }
                    params.putStringArrayList(GameAppOperation.QQFAV_DATALINE_FILEDATA, fileDataList);
                }
                doShareToTroopBar(params);
                return;
            case R.id.radioBtn_local_image: // 本地图片
                startPickLocaleImage(this);
                return;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == Constants.REQUEST_SHARE_TO_TROOP_BAR) {
    		Tencent.onActivityResultData(requestCode, resultCode, data, qqTroopBarListener);
    	} else if (requestCode == 10000) {
			String path = null;
			if (resultCode == Activity.RESULT_OK) {
				if (data != null && data.getData() != null) {
					// 根据返回的URI获取对应的SQLite信息
					Uri uri = data.getData();
					path = Util.getPath(this, uri);
				}
			}
			if (path != null) {
				imageUrl.setText(path);
			} else {
				showToast("请重新选择图片");
			}
		}      
    }

    private static final void startPickLocaleImage(Activity activity) {
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        
        if (android.os.Build.VERSION.SDK_INT >= Util.Build_VERSION_KITKAT) {
            intent.setAction(Util.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.str_image_local)),
                10000);
    }
    
    IUiListener qqTroopBarListener = new IUiListener() {
        @Override
        public void onCancel() {
            Util.toastMessage(QQTroopBarActivity.this, "onCancel called");
        }
        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Util.toastMessage(QQTroopBarActivity.this, "onComplete: " + response.toString());
        }
        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            Util.toastMessage(QQTroopBarActivity.this, "onError: " + e.errorMessage, "e");
        }
    };
    
    private void doShareToTroopBar(final Bundle params) {
    	MainActivity.mTencent.shareToTroopBar(QQTroopBarActivity.this, params, qqTroopBarListener);
    }

    Toast mToast = null;
    private void showToast(String text) {
        if (mToast != null && !super.isFinishing()) {
            mToast.setText(text);
            mToast.show();
            return;
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MainActivity.mTencent != null) {
            MainActivity.mTencent.releaseResource();
        }
    }
}
