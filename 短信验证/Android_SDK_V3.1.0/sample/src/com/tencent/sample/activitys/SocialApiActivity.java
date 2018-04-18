package com.tencent.sample.activitys;

import java.io.FileNotFoundException;

import com.tencent.connect.common.Constants;
import com.tencent.open.SocialApi;
import com.tencent.open.SocialConstants;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.GetAskGiftParamsDialog;
import com.tencent.sample.GetGradeParamsDialog;
import com.tencent.sample.GetInviteParamsDialog;
import com.tencent.sample.GetLbsParamsDialog;
import com.tencent.sample.GetPkBragParamsDialog;
import com.tencent.sample.GetReactiveParamsDialog;
import com.tencent.sample.GetStoryParamsDialog;
import com.tencent.sample.GetVoiceParamsDialog;
import com.tencent.sample.R;
import com.tencent.sample.GetInviteParamsDialog.OnGetInviteParamsCompleteListener;
import com.tencent.sample.GetVoiceParamsDialog.OnGetVoiceParamsCompleteListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SocialApiActivity extends BaseActivity implements OnClickListener {
	// set to 1 for test params
	private int mNeedInputParams = 1;

	//private SocialApi mSocialApi = null;
    public static final int REQUEST_VOICE_PIC = 1002;
    private Tencent mTencent;
    private LongSparseArray<IUiListener> mLiteners;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBarTitle("社交API");
		setLeftButtonEnable();
		setContentView(R.layout.social_api_activity);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(this);
			}
		}
		mTencent = Tencent.createInstance(MainActivity.mAppid, this);
		mLiteners = new LongSparseArray<IUiListener>(10);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_story_btn:
			onClickSendStory();
			break;
		case R.id.invite_btn:
			onClickInvite();
			break;
		case R.id.ask_gift_btn:
            onClickAskGift();
            break;
        //-----------------------------------
        //下面的注释请勿删除，编译lite版的时候需要删除
        //[liteexludestartmeta]
		case R.id.pk_brag_btn:
			onClickPkBrag();
			break;
		case R.id.voice_btn:
			onClickVoice();
			break;
		case R.id.grade_btn:
			onClickAppGrade();
			break;
		case R.id.reward_btn:
		    onClickAppReward();
			break;
		case R.id.reactive_btn:
			onClickReactive();
			break;
		case R.id.search_nearby:
			onClickSearchNearby();
			break;
		case R.id.delete_location:
			onClickDeleteLocation();
			break;
		//[liteexludeendmeta]
		}

	}

	private void onClickSendStory() {
		if (mTencent.isReady()) {
		    if (mNeedInputParams == 1) {
                new GetStoryParamsDialog(this,
                        new GetStoryParamsDialog.OnGetStoryParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(Bundle params) {
                                //mSocialApi.story(MainActivity.this, params, new BaseUiListener());
                                mTencent.story(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();

                params.putString(SocialConstants.PARAM_TITLE,
                        "AndroidSdk_1_3:UiStory title");
                params.putString(SocialConstants.PARAM_COMMENT,
                        "AndroidSdk_1_3: UiStory comment");
                params.putString(SocialConstants.PARAM_IMAGE,
                        "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                params.putString(SocialConstants.PARAM_SUMMARY,
                        "AndroidSdk_1_3: UiStory summary");
                params.putString(
                        SocialConstants.PARAM_PLAY_URL,
                        "http://player.youku.com/player.php/Type/Folder/"
                                + "Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");
                params.putString(SocialConstants.PARAM_ACT, "进入应用");
                //mSocialApi.story(MainActivity.this, params, new BaseUiListener());
                mTencent.story(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
            }
		}
	}

	private void onClickInvite() {
		if (mTencent.isReady()) {
		    if (mNeedInputParams == 1) {
                new GetInviteParamsDialog(this, new OnGetInviteParamsCompleteListener() {

                    @Override
                    public void onGetParamsComplete(Bundle params) {
                        //mSocialApi.invite(MainActivity.this, params, new BaseUiListener());
                        mTencent.invite(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                        Log.d("SDKQQAgentPref", "GetInviteFriendSwitch_SDK:" + SystemClock.elapsedRealtime());
                    }
                }).show();
            } else {
                Bundle params = new Bundle();
                // TODO keywords.
                params.putString(SocialConstants.PARAM_APP_ICON,
                        "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                params.putString(SocialConstants.PARAM_APP_DESC ,
                        "AndroidSdk_2_0: voice description!");
                params.putString(SocialConstants.PARAM_ACT, "进入应用");
                
                //mSocialApi.voice(MainActivity.this, params, new BaseUiListener());
                mTencent.invite(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
            }
		}
	}

	private void onClickAskGift() {
		if (mTencent.isReady()) {
		    if (mNeedInputParams == 1) {
                new GetAskGiftParamsDialog(
                        this,
                        new GetAskGiftParamsDialog.OnGetAskGiftParamsCompleteListener() {
                            @Override
                            public void onGetParamsComplete(Bundle params) {
                                if ("request".equals(params.getString(SocialConstants.PARAM_TYPE))) {
                                    mTencent.ask(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                                } else {
                                    mTencent.gift(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                                }
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();
                params.putString(SocialConstants.PARAM_TITLE, "title字段测试");
                params.putString(SocialConstants.PARAM_SEND_MSG, "msg字段测试");
                params.putString(SocialConstants.PARAM_IMG_URL,"http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                mTencent.ask(this, params, new BaseUIListener(SocialApiActivity.this));
            }
		}
	}

	//-----------------------------------
    //下面的注释请勿删除，编译lite版的时候需要删除
    //[liteexludestart]
	private void onClickPkBrag() {
		if (mTencent.isReady()) {
		    if (mNeedInputParams == 1) {
                new GetPkBragParamsDialog(
                        this,
                        new GetPkBragParamsDialog.OnGetPkBragParamsCompleteListener() {

                            @Override
                            public void onGetParamsComplete(Bundle params) {
                                Log.d("SDKQQAgentPref", "GetPKFriendInfoSwitch_SDK:" + SystemClock.elapsedRealtime());
                                if ("pk".equals((String) params.get(SocialConstants.PARAM_TYPE))) {
                                    mTencent.challenge(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                                } else if ("brag".equals((String) params
                                        .get(SocialConstants.PARAM_TYPE))) {
                                    mTencent.brag(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                                }
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();
                params.putString(SocialConstants.PARAM_RECEIVER, "4BE29C556418DE9A35469164C7B60B50");
                params.putString(SocialConstants.PARAM_SEND_MSG, "向某某某发起挑战");
                params.putString(SocialConstants.PARAM_IMG_URL, "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                mTencent.challenge(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
            }
		}
	}
	
	private void onClickAppReward() {
	    if (mTencent.isReady()) {
	        mTencent.showTaskGuideWindow(SocialApiActivity.this, null, new BaseUIListener(SocialApiActivity.this));
        }
	}

    private Bundle voiceBundle = new Bundle();
    private void onClickVoice() {
        Log.i("sample", "onClickVoice");
        if (mTencent.isReady()) {
            if (mNeedInputParams == 1) {
                showVoiceDialog();
            } else {
                if(voiceBundle == null){
                    voiceBundle = new Bundle();
                    // TODO keywords.
                    voiceBundle.putString(SocialConstants.PARAM_APP_ICON,
                            "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
                    voiceBundle.putString(SocialConstants.PARAM_APP_DESC,
                            "AndroidSdk_1_3: invite description!");
                    voiceBundle.putString(SocialConstants.PARAM_ACT, "进入应用");
                }
                
                mTencent.voice(SocialApiActivity.this, voiceBundle, new BaseUIListener(SocialApiActivity.this));
            }
        }
    }
    
    private void showVoiceDialog(){
        new GetVoiceParamsDialog(this, new OnGetVoiceParamsCompleteListener() {

            @Override
            public void onGetParamsComplete(Bundle params) {
                Log.i("sample", "onClickVoice complete");
                
                mTencent.voice(SocialApiActivity.this, params, 
                		new BaseUIListener(SocialApiActivity.this));
            }
            
            public void selectPhoto(Bundle params) {
                Log.i("sample", "onClickVoice complete");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_VOICE_PIC);
            }
        }, voiceBundle).show();
    }

    
    private void doVoicePhotoSelected(Uri uri){
        if(uri != null){
            Bitmap bitmap = null;  
            try {  
                ContentResolver resolver = getContentResolver();
                bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));  
                //bitmap = resizeImage(originalBitmap, 200, 200);  
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {  
                e.printStackTrace();
            }
            
            if(bitmap != null){
                voiceBundle.putParcelable(SocialConstants.PARAM_IMG_DATA, bitmap);
                showVoiceDialog();
            }
        }
    }
    
    private void onClickAppGrade() {
        if (mTencent.isReady()) {
            if (mNeedInputParams == 1) {
                new GetGradeParamsDialog(this, new GetGradeParamsDialog.OnGetGradeParamsCompleteListener() {

                    @Override
                    public void onGetParamsComplete(Bundle params) {
                        mTencent.grade(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                    }
                }).show();
            } else {
                Bundle params = new Bundle();
                params.putString("comment", "亲，给个好评吧");
                mTencent.grade(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
            }
        }
    }
    
    private void onClickReactive() {
    	if (mTencent.isReady()) {
		    if (mNeedInputParams == 1) {
                new GetReactiveParamsDialog(
                        this,
                        new GetReactiveParamsDialog.OnGetReactiveParamsCompleteListener() {
                            @Override
                            public void onGetParamsComplete(Bundle params) {
                                mTencent.reactive(SocialApiActivity.this, params, new BaseUIListener(SocialApiActivity.this));
                            }
                        }).show();
            } else {
                Bundle params = new Bundle();
                params.putString(SocialConstants.PARAM_TITLE, "title字段测试");
                params.putString(SocialConstants.PARAM_SEND_MSG, "msg字段测试");
                params.putString(SocialConstants.PARAM_IMG_URL,"http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                params.putString(SocialConstants.PARAM_REC_IMG, "http://i.gtimg.cn/qzonestyle/act/qzone_app_img/app888_888_75.png");
                params.putString(SocialConstants.PARAM_REC_IMG_DESC, "发送者获取礼物描述");
                mTencent.reactive(this, params, new BaseUIListener(SocialApiActivity.this));
            }
		}
    }
    
    private void onClickSearchNearby() {
    	if (mTencent.isReady()) {
    		BaseUIListener l = new BaseUIListener(SocialApiActivity.this);
			mLiteners.append(l.hashCode(), l);
			mTencent.searchNearby(SocialApiActivity.this, null, l);
			Log.d("SDKQQAgentPref", "GetNearbySwitch:" + SystemClock.elapsedRealtime());
    	}
    }
    
    private void onClickDeleteLocation() {
    	if (mTencent.isReady()) {
    		BaseUIListener l = new BaseUIListener(SocialApiActivity.this);
			mLiteners.append(l.hashCode(), l);
    		mTencent.deleteLocation(SocialApiActivity.this, null, l);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        Log.v("sample","onActivityResult:" + requestCode + ", resultCode:"+resultCode);
        if (requestCode == Constants.REQUEST_SOCIAL_API) {
        	Tencent.onActivityResultData(requestCode,resultCode,data,new BaseUIListener(SocialApiActivity.this));
        }
        if (data != null) {
            if (requestCode == REQUEST_VOICE_PIC) {
                doVoicePhotoSelected(data.getData());
            } 
        }
    }
    //[liteexludeend]
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        for (int i = 0; i < mLiteners.size(); i++) {
        	BaseUIListener listener = (BaseUIListener) mLiteners.valueAt(i);
        	listener.cancel();
		}
    }
}
