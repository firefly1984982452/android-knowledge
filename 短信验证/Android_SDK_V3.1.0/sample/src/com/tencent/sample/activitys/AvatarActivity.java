package com.tencent.sample.activitys;

import com.tencent.connect.avatar.QQAvatar;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AvatarActivity extends BaseActivity implements OnClickListener {

	private static final int REQUEST_SET_AVATAR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("用户头像");
		setLeftButtonEnable();
		setContentView(R.layout.avatar_activity);
		findViewById(R.id.set_avatar_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_avatar_btn:
			onClickSetAvatar();
			break;

		default:
			break;
		}

	}

    private void onClickSetAvatar() {
        if (MainActivity.ready(AvatarActivity.this)) {
            Intent intent = new Intent();
            // 开启Pictures画面Type设定为image
            intent.setType("image/*");
            // 使用Intent.ACTION_GET_CONTENT这个Action
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // 取得相片后返回本画面
            startActivityForResult(intent, REQUEST_SET_AVATAR);
            // 在 onActivityResult 中调用 doSetAvatar
        }
    }

    private void doSetAvatar(Uri uri) {
        QQAvatar qqAvatar = new QQAvatar(MainActivity.mTencent.getQQToken());
        qqAvatar.setAvatar(this, uri, new BaseUIListener(this), R.anim.zoomout);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SET_AVATAR
				&& resultCode == Activity.RESULT_OK)
			doSetAvatar(data.getData());
	}
	
}
