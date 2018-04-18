package com.tencent.sample;

import com.tencent.open.GameAppOperation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddFriendParamsDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	TextView fopenid;
    TextView label;
    TextView message;
    
    private OnGetAddFriendParamsCompleteListener mListener;

	public interface OnGetAddFriendParamsCompleteListener {
		public void onGetParamsComplete(Bundle params);
	}
	
	public AddFriendParamsDialog(Context context, OnGetAddFriendParamsCompleteListener listener) {
        super(context, R.style.Dialog_Fullscreen);
        mListener = listener;
    }
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_friend_params_dialog);
        
        fopenid = (TextView) findViewById(R.id.et_friend_openid);
        label = (TextView) findViewById(R.id.et_friend_label);
        message = (TextView) findViewById(R.id.et_friend_msg);
        
        fopenid.setText("969AFF8199B3B0F0A074FC892158209A");
        
        findViewById(R.id.bt_commit).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_commit:
			Bundle bundle = new Bundle();
            bundle.putString(GameAppOperation.GAME_FRIEND_OPENID, fopenid.getText() + "");
            bundle.putString(GameAppOperation.GAME_FRIEND_LABEL, label.getText() + "");
            bundle.putString(GameAppOperation.GAME_FRIEND_ADD_MESSAGE, message.getText() + "");
            
            if(null != mListener){
            	mListener.onGetParamsComplete(bundle);
            }
            this.dismiss();
			break;

		default:
			break;
		}
	}

}
