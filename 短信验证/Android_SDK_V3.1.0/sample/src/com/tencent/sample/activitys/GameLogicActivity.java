package com.tencent.sample.activitys;

import com.tencent.sample.AddFriendParamsDialog;
import com.tencent.sample.R;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GameLogicActivity extends Activity implements OnClickListener {

	private Tencent mTencent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_logic);
		findViewById(R.id.add_friend_btn).setOnClickListener(this);

		mTencent = Tencent.createInstance(MainActivity.getAppid(), this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.add_friend_btn:
                onClickAddFriend();
                break;
            default:
                break;
		}
	}

	private void onClickAddFriend() {
		new AddFriendParamsDialog(
				this,
				new AddFriendParamsDialog.OnGetAddFriendParamsCompleteListener() {

					@Override
					public void onGetParamsComplete(Bundle params) {
				        mTencent.makeFriend(GameLogicActivity.this, params);
					}
				}).show();
	}

}
