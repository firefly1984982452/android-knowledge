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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.open.GameAppOperation;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQFavoritesActivity extends BaseActivity implements OnClickListener {
    private String TAG = "QQFavoritesActivity";

    private View mContainer_title = null;
    private View mContainer_summary = null;
    private View mContainer_audioUrl = null;
    private View mContainer_targetUrl = null;
    private View mContainer_imgUrl = null;
    private View mContainer_appName = null;

    private TextView title = null;
    private TextView imageUrl = null;
    private TextView targetUrl = null;
    private EditText audioUrl = null;
    private TextView summary = null;
    private TextView appName = null;

    private TextView imageUrlLabel = null;
    private TextView targetUrlLabel = null;
    private TextView audioUrlLabel = null;

    private RadioButton mRadioBtn_localImage = null;
    private RadioButton mRadioBtn_netImage = null;

    private RadioButton mRadioBtnShareTypeImgText;
    private RadioButton mRadioBtnShareTypeAudio;
    private RadioButton mRadioBtnShareTypeInfo;
    private RadioButton mRadioBtnShareTypeText;

    private int shareType = GameAppOperation.QQFAV_DATALINE_TYPE_TEXT;

    private ImageView    mAddImageBtn = null;
    private LinearLayout mImageListLayout = null;
	
    static int num = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("QQ收藏");
        setLeftButtonEnable();
        setContentView(R.layout.qq_favorites_dataline_activity);

        title = (TextView) findViewById(R.id.shareqq_title);
        imageUrl = (TextView) findViewById(R.id.shareqq_image_url);
        imageUrlLabel = (TextView) findViewById(R.id.tv_shareqq_image_url);
        targetUrl = (TextView) findViewById(R.id.shareqq_target_url);
        targetUrlLabel = (TextView) findViewById(R.id.tv_shareqq_target_url);
        audioUrl = (EditText) findViewById(R.id.et_shareqq_audioUrl);
        audioUrlLabel = (TextView) findViewById(R.id.tv_shareqq_audioUrl);
        summary = (TextView) findViewById(R.id.shareqq_summary);
        appName = (TextView) findViewById(R.id.shareqq_app_name);

        findViewById(R.id.shareqq_commit).setOnClickListener(this);

        mContainer_title = findViewById(R.id.qqshare_title_container);
        mContainer_summary = findViewById(R.id.qqshare_summary_container);
        mContainer_audioUrl = findViewById(R.id.qqshare_audioUrl_container);
        mContainer_targetUrl = findViewById(R.id.qqshare_targetUrl_container);
        mContainer_imgUrl = findViewById(R.id.qqshare_imageUrl_container);
        mContainer_appName = findViewById(R.id.qqshare_appName_container);

        mRadioBtn_netImage = (RadioButton) findViewById(R.id.radioBtn_net_image);
        mRadioBtn_netImage.setOnClickListener(this);
        mRadioBtn_localImage = (RadioButton) findViewById(R.id.radioBtn_local_image);
        mRadioBtn_localImage.setOnClickListener(this);
        mRadioBtnShareTypeImgText = (RadioButton) findViewById(R.id.radioBtn_share_type_img_text);
        mRadioBtnShareTypeImgText.setOnClickListener(this);
        mRadioBtnShareTypeAudio = (RadioButton) findViewById(R.id.radioBtn_share_type_audio);
        mRadioBtnShareTypeAudio.setOnClickListener(this);
        mRadioBtnShareTypeInfo = (RadioButton) findViewById(R.id.radioBtn_share_type_information);
        mRadioBtnShareTypeInfo.setOnClickListener(this);
        mRadioBtnShareTypeText = (RadioButton) findViewById(R.id.radioBtn_share_type_text);
        mRadioBtnShareTypeText.setOnClickListener(this);
        
        mAddImageBtn = (ImageView)findViewById(R.id.btn_addImage);
        mAddImageBtn.setVisibility(View.VISIBLE);
        mAddImageBtn.setOnClickListener(this);
        mImageListLayout = (LinearLayout)findViewById(R.id.images_picker_layout);
        mImageListLayout.setVisibility(View.VISIBLE);
        initShareUI(shareType);

        checkTencentInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareqq_commit: // 提交
                final Bundle params = new Bundle();
                params.putString(GameAppOperation.QQFAV_DATALINE_APPNAME, appName.getText().toString());
                params.putString(GameAppOperation.QQFAV_DATALINE_TITLE, title.getText().toString());
                params.putInt(GameAppOperation.QQFAV_DATALINE_REQTYPE, shareType);
                params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, summary.getText().toString());

                switch (shareType) {
                    case GameAppOperation.QQFAV_DATALINE_TYPE_IMAGE_TEXT:
                        if (!TextUtils.isEmpty(summary.getText().toString())) {
                            params.putString(GameAppOperation.QQFAV_DATALINE_DESCRIPTION, summary.getText().toString()
                                    + GameAppOperation.PIC_SYMBOLE);
                        }
                        
                        String srFileData = imageUrl.getText().toString();
                        ArrayList<String> fileDataList = new ArrayList<String>();
                        if (!TextUtils.isEmpty(srFileData)) {
                            srFileData.replace(" ", "");
                            String[] filePaths = srFileData.split(";");
                            int size = filePaths.length;
                            for (int i = 0; i < size; i++) {
                                String path = filePaths[i].trim();
                                if (!TextUtils.isEmpty(path)) {
                                    fileDataList.add(path);
                                }
                            }
                        }
                        if (mImageListLayout != null && mImageListLayout.isShown()) {
							for (int i = 0; i < mImageListLayout.getChildCount(); i++) {
								LinearLayout addItem = (LinearLayout) mImageListLayout.getChildAt(i);
								EditText editText = (EditText) addItem.getChildAt(1);
								fileDataList.add(editText.getText().toString());
							}
                        }
						if (fileDataList.size() > 0 && fileDataList.size() <= 5) {
							params.putStringArrayList(GameAppOperation.QQFAV_DATALINE_FILEDATA, fileDataList);
						} else if (fileDataList.size() > 5){
							Toast.makeText(QQFavoritesActivity.this, "图片数量超过5张", 1000).show();
						}
                        break;
                    case GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT:
                        params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL, imageUrl.getText().toString());
                        params.putString(GameAppOperation.QQFAV_DATALINE_URL, targetUrl.getText().toString());
                        break;
                    case GameAppOperation.QQFAV_DATALINE_TYPE_AUDIO:
                        params.putString(GameAppOperation.QQFAV_DATALINE_IMAGEURL, imageUrl.getText().toString());
                        params.putString(GameAppOperation.QQFAV_DATALINE_URL, targetUrl.getText().toString());
                        params.putString(GameAppOperation.QQFAV_DATALINE_AUDIOURL, audioUrl.getText()
                                .toString());
                        break;
                }
                doAddToQQFavorites(params);
                return;
            case R.id.radioBtn_local_image: // 本地图片
                startPickLocaleImage(this);
                return;
            case R.id.radioBtn_net_image:
                return;
            case R.id.radioBtn_share_type_audio:
                shareType = GameAppOperation.QQFAV_DATALINE_TYPE_AUDIO;
                break;
            case R.id.radioBtn_share_type_information:
                shareType = GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT;
                break;
            case R.id.radioBtn_share_type_img_text:
                shareType = GameAppOperation.QQFAV_DATALINE_TYPE_IMAGE_TEXT;
                break;
            case R.id.radioBtn_share_type_text:
                shareType = GameAppOperation.QQFAV_DATALINE_TYPE_TEXT;
                break;
            case R.id.btn_addImage:
               
				// if (num < MAX_IMAGE) {
				LinearLayout addItem = (LinearLayout) LayoutInflater.from(this)
						.inflate(R.layout.image_picker_layout, null);
				mImageListLayout.addView(addItem);
				TextView textView0 = (TextView) addItem.getChildAt(0); // index
				View view1 = addItem.getChildAt(1); // editText url
				View view2 = addItem.getChildAt(2); // picker按钮
				View view3 = addItem.getChildAt(3); // 删除按钮
				textView0.setText(String.valueOf(num+1));
				view1.setId(1000 + num); // url EditText
				view2.setId(2000 + num); // picker
				view3.setId(3000 + num); // 删除
				addItem.setId(num);
				view1.requestFocus();
				view2.setOnClickListener(this);
				view3.setOnClickListener(this);
				num += 1;
				// } else {
				// showToast("不能添加更多的图片!!!");
				// }
                break;
        }
        int id = v.getId();
        if (id >= 2000 && id < 3000) {
            // 点的是选择图片
            startPickLocaleImage(this, id - 2000);
        } else if (id >= 3000 && id < 4000) {
            // 点的是删除图片
            if (mImageListLayout.getChildCount() > 0) {
                View view = mImageListLayout.findViewById(id - 3000);
                mImageListLayout.removeView(view);
            }
        }
        
        initShareUI(shareType);
    }

    /**
     * 初始化UI
     * @param shareType
     */
    private void initShareUI(int shareType) {
        switch (shareType) {
            case GameAppOperation.QQFAV_DATALINE_TYPE_TEXT:
                mContainer_audioUrl.setVisibility(View.GONE);
                mContainer_targetUrl.setVisibility(View.GONE);
                mContainer_imgUrl.setVisibility(View.GONE);
                title.setText(R.string.qqshare_title_content);
                summary.setText(R.string.qqshare_summary_content);
                break;

            case GameAppOperation.QQFAV_DATALINE_TYPE_IMAGE_TEXT:
                mContainer_audioUrl.setVisibility(View.GONE);
                mContainer_targetUrl.setVisibility(View.GONE);
                mContainer_imgUrl.setVisibility(View.VISIBLE);
                mRadioBtn_localImage.setVisibility(View.VISIBLE);
                mRadioBtn_netImage.setVisibility(View.VISIBLE);
                imageUrl.setText(R.string.qqshare_imageUrl_content);
                title.setText(R.string.qqshare_title_content);
                summary.setText(R.string.qqshare_summary_content);
                imageUrlLabel.setText("图片路径:");
                mRadioBtn_netImage.setChecked(true);
                break;

            case GameAppOperation.QQFAV_DATALINE_TYPE_AUDIO:
                mContainer_audioUrl.setVisibility(View.VISIBLE);
                mContainer_targetUrl.setVisibility(View.VISIBLE);
                mContainer_imgUrl.setVisibility(View.VISIBLE);
                mRadioBtn_localImage.setVisibility(View.GONE);
                mRadioBtn_netImage.setVisibility(View.GONE);

                audioUrlLabel.setText("音乐播放地址:");
                targetUrlLabel.setText("详情页地址:");
                imageUrlLabel.setText("预览图地址:");

                title.setText("不要说话");
                audioUrl.setText("http://open.music.qq.com/fcgi-bin/fcg_music_get_playurl.fcg?redirect=0&song_id=7219451&filetype=mp3"
                        + "&qqmusic_fromtag=50&app_id=100497308&app_key=8498609f25f65295491a1d866e4f0258&device_id=ffffffff81e161b63d6ab6f6334b8cc1");
                imageUrl.setText("http://imgcache.qq.com/music/photo/album/24/150_albumpic_655724_0.jpg");
                targetUrl.setText("http://data.music.qq.com/playsong.html?hostuin="
                        + "&songid=7219451&appshare=android_qq#p=(2rpl)&source=qq");

                summary.setText("专辑名：不想放手歌手名：陈奕迅");
                break;
            case GameAppOperation.QQFAV_DATALINE_TYPE_DEFAULT:
                mContainer_audioUrl.setVisibility(View.GONE);
                mContainer_targetUrl.setVisibility(View.VISIBLE);
                mContainer_imgUrl.setVisibility(View.VISIBLE);
                mRadioBtn_localImage.setVisibility(View.GONE);
                mRadioBtn_netImage.setVisibility(View.GONE);

                targetUrlLabel.setText("详情页地址:");
                imageUrlLabel.setText("预览图地址:");

                title.setText("不要说话");
                imageUrl.setText(R.string.qqshare_imageUrl_content);
                targetUrl.setText("http://v.yinyuetai.com/video/2116526");
                summary.setText("专辑名：不想放手歌手名：陈奕迅");
                break;
        }
        mContainer_title.setVisibility(View.VISIBLE);
        mContainer_summary.setVisibility(View.VISIBLE);
        mContainer_appName.setVisibility(View.VISIBLE);
        appName.setText(R.string.qqshare_appName_content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == Constants.REQUEST_QQ_FAVORITES) {
    		Tencent.onActivityResultData(requestCode, resultCode, data, addToQQFavoritesListener);
    	}
    	if (requestCode == 10000) {
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
            	if(shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE){
            		showToast("请重新选择图片");
            	}
            }
    	} else{
        	String path = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    // 根据返回的URI获取对应的SQLite信息
                    Uri uri = data.getData();
                    path = Util.getPath(this, uri);
                }
            }
            if (path != null) {
            	EditText editText = (EditText)mImageListLayout.findViewById(requestCode + 1000);
            	editText.setText(path);
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
    
    private static final void startPickLocaleImage(Activity activity, int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (android.os.Build.VERSION.SDK_INT >= Util.Build_VERSION_KITKAT) {
            intent.setAction(Util.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, activity.getString(R.string.str_image_local)), requestId);
    }
    
    IUiListener addToQQFavoritesListener = new IUiListener() {
        @Override
        public void onCancel() {
            Util.toastMessage(QQFavoritesActivity.this, "onCancel called");
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Util.toastMessage(QQFavoritesActivity.this, "onComplete: " + response.toString());
        }
        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            Util.toastMessage(QQFavoritesActivity.this, "onError: " + e.errorMessage, "e");
        }
    };
    
    private void doAddToQQFavorites(final Bundle params) {
        MainActivity.mTencent.addToQQFavorites(QQFavoritesActivity.this, params, addToQQFavoritesListener);
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
