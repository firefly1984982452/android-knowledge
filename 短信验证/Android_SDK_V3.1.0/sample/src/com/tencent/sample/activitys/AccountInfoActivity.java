
package com.tencent.sample.activitys;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tencent.connect.UserInfo;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.R;
import com.tencent.sample.Util;

public class AccountInfoActivity extends BaseActivity implements OnClickListener {
	private UserInfo mInfo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_activity);
        setBarTitle("获取用户资料");
        setLeftButtonEnable();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof Button) {
                view.setOnClickListener(this);
            }
        }
        mInfo = new UserInfo(this, MainActivity.mTencent.getQQToken());
    }

    @Override
    public void onClick(View v) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (v.getId()) {
            case R.id.user_info_btn:
                onClickUserInfo();
                v.startAnimation(shake);
                break;

            case R.id.vip_info_btn:
                onClickVipInfo();
                v.startAnimation(shake);
                break;

            case R.id.vip_rich_info_btn:
                onClickVipRichInfo();
                v.startAnimation(shake);
                break;
            case R.id.tenpay_info_btn:
            	onClickTenpayInfo();
            	v.startAnimation(shake);
                break;
        }
    }

    /**
     * 
     */
    private void onClickUserInfo() {
        if (MainActivity.ready(this)) {
        	mInfo.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            Util.showProgressDialog(this, null, null);
        }
    }

    /**
     * 
     */
    private void onClickVipInfo() {
        if (MainActivity.ready(this)) {
        	mInfo.getVipUserInfo(new BaseUIListener(this, "get_vip_info"));
            Util.showProgressDialog(this, null, null);
        }
    }

    /**
     * 
     */
    private void onClickVipRichInfo() {
        if (MainActivity.ready(this)) {
            mInfo.getVipUserRichInfo(new BaseUIListener(this, "get_vip_rich_info"));
            Util.showProgressDialog(this, null, null);
        }
    }
    
    /**
     * 
     */
    private void onClickTenpayInfo() {
    	if (MainActivity.ready(this)) {
    		mInfo.getTenPayAddr(new BaseUIListener(this, "get_vip_rich_info"));
    		Util.showProgressDialog(this, null, null);
    	}
    }

    /**
     * 异步显示结果
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Context context = AccountInfoActivity.this;
            String scope = msg.obj.toString();
            String response = msg.getData().getString("response");
            if (response != null) {
                // 换行显示
                response = response.replace(",", "\r\n");
            }
            AlertDialog dialog = new AlertDialog.Builder(context).setMessage(response)
                    .setNegativeButton("知道啦", null).create();
            if (scope.equals("get_simple_userinfo")) {
                dialog.setTitle("用户基本资料");
            } else if (scope.equals("get_vip_info")) {
                dialog.setTitle("用户会员基本资料");
            } else if (scope.equals("get_vip_rich_info")) {
                dialog.setTitle("用户会员高级资料");
            } else if (scope.equals("get_vip_rich_info")) {
                // TODO
                // 这里准备写财付通的接口

            }
            dialog.show();
        };
    };
}
