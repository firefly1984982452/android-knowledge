package com.tencent.sample.activitys;

import com.tencent.sample.BindGroupParamsDialog;
import com.tencent.sample.R;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class QQGroupActivity extends Activity implements OnClickListener {
    private AlertDialog mQQGroupDialog;
    private EditText mKeyEdit;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqgroup);
        findViewById(R.id.qq_group_btn).setOnClickListener(this);
        findViewById(R.id.bind_group_btn).setOnClickListener(this);

        mTencent = Tencent.createInstance(MainActivity.mAppid, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq_group_btn:
                showQQGroupDialog();
                break;
            case R.id.bind_group_btn:
                onClickBindGameGroup();
                break;
            default:
                break;
        }
    }

    private void onClickBindGameGroup() {
        new BindGroupParamsDialog(this, new BindGroupParamsDialog.OnGetParamsCompleteListener() {
            @Override
            public void onGetParamsComplete(Bundle params) {
                mTencent.bindQQGroup(QQGroupActivity.this, params);
            }
        }).show();
    }

    private void showQQGroupDialog() {
        if (mQQGroupDialog == null) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.dialog_qqgroup, null);
            mKeyEdit = (EditText) textEntryView.findViewById(R.id.key_edit);
            mQQGroupDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.qq_group_dialog_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = mKeyEdit.getText().toString();
                        if (TextUtils.isEmpty(key)) {
                            Toast.makeText(QQGroupActivity.this, "key不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            mTencent.joinQQGroup(QQGroupActivity.this, key);
                        }
                    }
                })
                .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
        }
        //mKeyEdit.setText("tDxW_JtRXq84ls9Kfo523A9ri18Xb1Vu");
        mQQGroupDialog.show();
    }

}
