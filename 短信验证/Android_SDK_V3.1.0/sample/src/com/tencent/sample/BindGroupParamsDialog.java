package com.tencent.sample;

import com.tencent.open.GameAppOperation;
import com.tencent.sample.GetAskGiftParamsDialog.OnGetAskGiftParamsCompleteListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BindGroupParamsDialog extends Dialog implements
		android.view.View.OnClickListener {

    TextView tvGameUnionId;
    TextView tvGameZoneId;
    TextView tvGameSignature;
    TextView tvGameUnionName;

    private final OnGetParamsCompleteListener mListener;

    public interface OnGetParamsCompleteListener {
		public void onGetParamsComplete(Bundle params);
	}

    public BindGroupParamsDialog(Context context, OnGetParamsCompleteListener listener) {
        super(context, R.style.Dialog_Fullscreen);
        mListener = listener;
    }

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bind_group_params_dialog);
        tvGameUnionId = (TextView) findViewById(R.id.et_game_unionid);
        tvGameZoneId = (TextView) findViewById(R.id.et_game_zoneid);
        tvGameSignature = (TextView) findViewById(R.id.et_game_signature);
        tvGameUnionName = (TextView) findViewById(R.id.et_game_union_name);
        findViewById(R.id.bt_commit).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_commit:
			Bundle bundle = new Bundle();
                bundle.putString(GameAppOperation.GAME_UNION_ID, tvGameUnionId.getText() + "");
                bundle.putString(GameAppOperation.GAME_ZONE_ID, tvGameZoneId.getText() + "");
                bundle.putString(GameAppOperation.GAME_SIGNATURE, tvGameSignature.getText() + "");
                bundle.putString(GameAppOperation.GAME_UNION_NAME, tvGameUnionName.getText() + "");

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
