package com.tencent.sample.activitys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.dataprovider.CallbackManager;
import com.tencent.sample.R;

public class DataProviderActivity extends Activity implements View.OnClickListener {
	private CallbackManager mCalledManager;
	private final String IMAGE_TYPE = "image/*";
	private final String VIDEO_TYPE = "video/*";
	private final int SELECT_IMAGE_RESULT_CODE = 0;   //
	private final int SELECT_VIDEO_RESULT_CODE = 1;   //
	private ImageView mImageView;
	private EditText mPicPathEditText;
	private EditText mVideoPathEditText;
	private EditText mSendContentEditText;
	private TextView mErrorCodeTextView;
	private ViewGroup mImageViewGroup;
	private ViewGroup mVideoViewGroup;
	private ViewGroup mTextViewGroup;
	private RadioGroup mRadioGroup;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
			    try {
                        mImageView.setImageBitmap((Bitmap) msg.obj);
			    } catch (Exception e) {
			    }
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calledactivity);
		mCalledManager = new CallbackManager(this);
		mImageViewGroup = (ViewGroup) findViewById(R.id.imagelayout);
		mVideoViewGroup = (ViewGroup) findViewById(R.id.videolayout);
		mVideoViewGroup.setVisibility(View.GONE);
		mTextViewGroup = (ViewGroup) findViewById(R.id.textlayout);
		mTextViewGroup.setVisibility(View.GONE);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.imageradiobtn:
					mImageViewGroup.setVisibility(View.VISIBLE);
					mVideoViewGroup.setVisibility(View.GONE);
					mTextViewGroup.setVisibility(View.GONE);
					break;
				case R.id.videoradiobtn:
					mVideoViewGroup.setVisibility(View.VISIBLE);
					mImageViewGroup.setVisibility(View.GONE);
					mTextViewGroup.setVisibility(View.GONE);
					break;
				case R.id.textradiobtn:
					mVideoViewGroup.setVisibility(View.GONE);
					mImageViewGroup.setVisibility(View.GONE);
					mTextViewGroup.setVisibility(View.VISIBLE);
					break;
				}
			}
		});
		
		mImageView = (ImageView) findViewById(R.id.pickedimage);
		mSendContentEditText = (EditText) findViewById(R.id.sendcontent);
		mPicPathEditText = (EditText) findViewById(R.id.picpath);
		mVideoPathEditText = (EditText) findViewById(R.id.videopath);
		mPicPathEditText.setText("http://img1.gtimg.com/news/pics/hv1/135/132/1342/87297345.jpg");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap bm = returnBitMap("http://img1.gtimg.com/news/pics/hv1/135/132/1342/87297345.jpg");
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.obj = bm;
				mHandler.sendMessage(msg);
			}
		}).start();
		
		Button sendpathbtn = (Button) findViewById(R.id.sendimagepathbth);
		sendpathbtn.setOnClickListener(this);
		Button sendvideobtn = (Button) findViewById(R.id.sendvideopathbtn);
		sendvideobtn.setOnClickListener(this);
		Button sendTextButton = (Button) findViewById(R.id.sendtextbtn);
		sendTextButton.setOnClickListener(this);
		Button pickpicbtn = (Button)findViewById(R.id.pickpicbtn);
		pickpicbtn.setOnClickListener(this);
		Button pickVideoButton = (Button) findViewById(R.id.pickvideobtn);
		pickVideoButton.setOnClickListener(this);
		
		mErrorCodeTextView = (TextView) findViewById(R.id.errorcode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
	        return;
	    }
	    
	    Bitmap bm = null;
	    //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
	    ContentResolver resolver = getContentResolver();
	    //此处的用于判断接收的Activity是不是你想要的那个
	    if (requestCode == SELECT_IMAGE_RESULT_CODE) {
	        try {
	            Uri originalUri = data.getData();        //获得图片的uri 
//	            mImageView.setDrawingCacheEnabled(true);
//	            Bitmap delBm = mImageView.getDrawingCache();
//	            mImageView.setDrawingCacheEnabled(false);
//	            if (delBm != null) {
//	            	delBm.recycle();
//	            }
	            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
	            
	            mImageView.setImageBitmap(bm);
	            //显得到bitmap图片	 
	            //这里开始的第二部分，获取图片的路径：
	            String[] proj = {MediaStore.Images.Media.DATA};
	            //好像是android多媒体数据库的封装接口，具体的看Android文档
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
	 
	            if (cursor != null) {
	            	try {
			            //按我个人理解 这个是获得用户选择的图片的索引值
			            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			            //将光标移至开头 ，这个很重要，不小心很容易引起越界
			            cursor.moveToFirst();
			            //最后根据索引值获取图片路径
			            String path = cursor.getString(column_index);
			            mPicPathEditText.setText(path);
	            	} finally {
	            		cursor.close();
	            		cursor = null;
	            	}
	            }
			} catch (IOException e) {
	        }
	    } else if (requestCode == SELECT_VIDEO_RESULT_CODE) {
            Uri originalUri = data.getData();        //获得图片的uri 
            try {
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            mImageView.setImageBitmap(bm);
            //显得到bitmap图片	 
            //这里开始的第二部分，获取图片的路径：
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
            if (cursor != null) {
            	try {
		            //按我个人理解 这个是获得用户选择的图片的索引值
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            //将光标移至开头 ，这个很重要，不小心很容易引起越界
		            cursor.moveToFirst();
		            //最后根据索引值获取图片路径	
		            String path = cursor.getString(column_index);
			    	mVideoPathEditText.setText(path);
            	} finally {
            		cursor.close();
            		cursor = null;
            	}
            }
	    }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendimagepathbth:
			mErrorCodeTextView.setText("");
			if (mPicPathEditText.length() > 0) {
				int errorCode = mCalledManager.sendTextAndImagePath(mSendContentEditText.getText().toString(), 
						mPicPathEditText.getText().toString());
				handleError(errorCode);
			} else {
				Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.sendvideopathbtn:
			mErrorCodeTextView.setText("");
			if (mVideoPathEditText.length() > 0) {
				int errorCode = mCalledManager.sendTextAndVideoPath(mSendContentEditText.getText().toString(), 
						mVideoPathEditText.getText().toString());
				handleError(errorCode);
			} else {
				Toast.makeText(getApplicationContext(), "请先选择视频", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.pickpicbtn:
			//使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片
			Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
			try {
			    getAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
			    startActivityForResult(getAlbum, SELECT_IMAGE_RESULT_CODE);
			} catch (Exception e){
			}
			break;
		case R.id.pickvideobtn:
			Intent getVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
			try {
			    getVideoIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, VIDEO_TYPE);
			    startActivityForResult(getVideoIntent, SELECT_VIDEO_RESULT_CODE);
			} catch (Exception e) {
			}
			break;
		case R.id.sendtextbtn:
			mErrorCodeTextView.setText("");
			if (mSendContentEditText.length() > 0) {
				int errorCode = mCalledManager.sendTextOnly(mSendContentEditText.getText().toString());
				handleError(errorCode);
			} else {
				Toast.makeText(getApplicationContext(), "请填上要发送的文本内容", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
	
	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (myFileUrl == null) return null;
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
            bitmap = null;
		}
		return bitmap;
	}
	
	private void handleError(int error) {
		switch (error) {
		case 0:
			finish();
			break;
		default:
			mErrorCodeTextView.setText(Integer.toString(error));
			break;
		}
	}
}
