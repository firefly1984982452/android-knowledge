package com.tencent.sample.activitys;

import com.tencent.open.GameAppOperation;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SharePrizeActivity extends BaseActivity implements OnClickListener {
	
	private TextView title = null;
    private TextView imageUrl = null;
    private TextView summary = null;
    private TextView activityId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("有奖分享");
        setLeftButtonEnable();
		setContentView(R.layout.activity_share_prize);
		
		title = (TextView) findViewById(R.id.prize_title);
        imageUrl = (TextView) findViewById(R.id.prize_image_url);
        summary = (TextView) findViewById(R.id.prize_summary);
        activityId = (TextView) findViewById(R.id.prize_id_et);

        findViewById(R.id.shareqq_commit).setOnClickListener(this);
        findViewById(R.id.check_activity_commit).setOnClickListener(this);
        findViewById(R.id.exchange_prize_commit).setOnClickListener(this);
        checkTencentInstance();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.shareqq_commit:
                final Bundle params = new Bundle();
                params.putString(GameAppOperation.SHARE_PRIZE_TITLE, title.getText().toString());
                params.putString(GameAppOperation.SHARE_PRIZE_SUMMARY, summary.getText().toString());
                params.putString(GameAppOperation.SHARE_PRIZE_IMAGE_URL, imageUrl.getText().toString());
                params.putString(GameAppOperation.SHARE_PRIZE_ACTIVITY_ID, activityId.getText().toString());

                MainActivity.mTencent.sharePrizeToQQ(this, params, listener);
                break;
            case R.id.check_activity_commit:
                MainActivity.mTencent.checkActivityAvailable(this, activityId.getText().toString(), listener);
                break;
            case R.id.exchange_prize_commit:
                final Bundle bundle = new Bundle();
                bundle.putString(GameAppOperation.SHARE_PRIZE_ACTIVITY_ID, activityId.getText().toString());
                MainActivity.mTencent.queryUnexchangePrize(this, bundle, listener);
                break;
		default:
			break;
		}
		
	}
	
	private IUiListener listener = new IUiListener() {
		
		@Override
		public void onError(UiError e) {
            Util.toastMessage(SharePrizeActivity.this, "onError: " + e.errorMessage + "detail: " + e.errorDetail, "e");
		}
		
		@Override
		public void onComplete(Object response) {
			Util.toastMessage(SharePrizeActivity.this, "onComplete: " + response.toString());
		}
		
		@Override
		public void onCancel() {
			Util.toastMessage(SharePrizeActivity.this, "onCancel: ");
		}
	};

}
