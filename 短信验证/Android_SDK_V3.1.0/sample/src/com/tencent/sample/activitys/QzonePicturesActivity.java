
package com.tencent.sample.activitys;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.open.qzone.Albums;
import com.tencent.open.qzone.Albums.AlbumSecurity;
import com.tencent.sample.BaseApiListener;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.PhotoListDialog;
import com.tencent.sample.R;
import com.tencent.sample.Util;
import com.tencent.tauth.Tencent;

public class QzonePicturesActivity extends BaseActivity {
    private JSONObject mPicsJson = null;
    private GridView mGridView = null;
    private BaseAdapter mAdapter = null;
	private static final int REQUEST_UPLOAD_PIC = 100;
	Albums mAlbums = null;
	QzonePics mPics ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("空间相册");
        setLeftButtonEnable();
        setContentView(R.layout.pic_list_layout);

        // 检查是否登录帐号
        if (MainActivity.ready(this)) {
            mAlbums = new Albums(this, MainActivity.mTencent.getQQToken());
        } else {
            Util.toastMessage(this, "需要登录才能查看相册信息!!!");
            finish();
            return;
        }
		initViews(); 
        // 获取相册信息
       mAlbums.listAlbum(new AlbumListListener("list_album", false, this));
       setClickListener();
    }
    
    private static final int SET_IMAGE = 0;
    private static final int SET_ADAPTER = 1;
    private static final int SHOW_PICTURE = 2;
    private static final int UPDATE_ALBUM = 3;
    private static final int UPLOAD_PIC = 4;
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_IMAGE:
				int position = msg.arg1;
				Bitmap bitmap = (Bitmap) msg.obj;
				if (mGridView.getChildAt(position) != null) {
					ImageView image = (ImageView) mGridView.getChildAt(
							position).findViewById(R.id.album_image);
					image.setImageBitmap(bitmap);
				}
				break;
			case SET_ADAPTER:
				 Util.showResultDialog(QzonePicturesActivity.this, mPicsJson.toString(), "response");
                 mAdapter = new PicGridAdapter();
                 mGridView.setAdapter(mAdapter);
                 break;
			case SHOW_PICTURE:
				new PhotoListDialog(QzonePicturesActivity.this, (JSONObject) msg.obj)
				.show();
				break;
			case UPDATE_ALBUM:
				String response = (String)msg.obj;
				Util.showResultDialog(QzonePicturesActivity.this, response, "response");
				mAlbums.listAlbum(new AlbumListListener("list_album", false,QzonePicturesActivity.this));
				break;
			case UPLOAD_PIC:
				Util.showResultDialog(QzonePicturesActivity.this, (String)msg.obj, "response");
				break;
			default:
				break;
			}
		}
    	
    };
//    
    private void initViews(){
    	mGridView = (GridView)findViewById(R.id.picGridView);
    	Util.showProgressDialog(this, null, null);
    }
//    
    private void setClickListener(){
    	mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int arg2,
					long arg3) {
				QzonePics.AlbumInfo info = mPics.albumInfos[arg2];
				mAlbums.listPhotos(info.albumid, 
						new PhotoListListener("list_photo",false,QzonePicturesActivity.this));
				Util.showProgressDialog(QzonePicturesActivity.this, null, null);
			}
		});
    	this.findViewById(R.id.add_album_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickAddAlbum();
			}
		});
    	this.findViewById(R.id.upload_pic_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickUploadPic();
			}
		});
    }
    
	private void onClickAddAlbum() {
		if (MainActivity.ready(QzonePicturesActivity.this)) {
			String albumname =
					"QQ登陆SDK：Add_Album测试" + System.currentTimeMillis();// 必须。相册名，不能超过30个字符。
			String albumdesc = "QQ登陆SDK：Add_Album测试" + new Date();// 相册描述，不能超过200个字符。
			String question = "question";// 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：
			String answer = "answer";// 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：

			mAlbums.addAlbum(albumname, albumdesc, AlbumSecurity.publicToAll, question, answer, 
					new AlbumListListener("add_album", true,this));
			Util.showProgressDialog(this, null, null);
		}
	}
	private void onClickUploadPic() {
		if (MainActivity.ready(QzonePicturesActivity.this)) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_UPLOAD_PIC);
		}
	}

	private void doUploadPic(Uri uri) {

		String photodesc = "QQ登陆SDK：UploadPic测试" + new Date();// 照片描述，注意照片描述不能超过200个字符。
		String title = "QQ登陆SDK：UploadPic测试" + System.currentTimeMillis()
				+ ".png";// 照片的命名，必须以.jpg,
							// .gif,
							// .png,
							// .jpeg,
							// .bmp此类后缀结尾。

		String x = "0-360";// 照片拍摄时的地理位置的经度。请使用原始数据（纯经纬度，0-360）。
		String y = "0-360";// 照片拍摄时的地理位置的纬度。请使用原始数据（纯经纬度，0-360）。

		mAlbums.uploadPicture(getAbsoluteImagePath(uri), photodesc, null, x, y,
				new AlbumListListener("upload_pic", true, this));// 相册id，不填则传到默认相册
		Util.showProgressDialog(this, null, null);
	}
	
	private String getAbsoluteImagePath(Uri uri) {
		// can post image
		ContentResolver cr = this.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, null, null, null, null);
            if (null == cursor) {
                return "";
            }
			cursor.moveToFirst();
			return cursor.getString(1);
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_UPLOAD_PIC && resultCode == Activity.RESULT_OK){
			doUploadPic(data.getData());
		}
	}

	private class PicGridAdapter extends BaseAdapter {
    	private LayoutInflater mInflater;
    	private List<Bitmap> mBitmapList = new ArrayList<Bitmap>();
        public PicGridAdapter() {
			super();
			try {
				mPics = QzonePicsFactory.parseJson(mPicsJson);
			} catch (JSONException e) {
				mPics = null;
				e.printStackTrace();
			}
			mInflater = LayoutInflater.from(QzonePicturesActivity.this);
			if(mPics != null) {
				new Thread(){

					@Override
					public void run() {
						for(int i = 0 ;i<mPics.albumnum;i++){
							Bitmap bitmap = Util.getbitmap(mPics.albumInfos[i].coverurl);
							mBitmapList.add(bitmap);
							if(bitmap != null){
								Message msg = new Message();
								msg.what = 0;
								msg.arg1 = i;
								msg.obj = bitmap;
								mHandler.sendMessage(msg);
							}
						}
					}
					
				}.start();
			}
		}

		@Override
        public int getCount() {
			if (mPics == null) {
				return 0;
			}else {
				return mPics.albumnum;
			}
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	Holder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.pic_album_list_item, null);
				holder = new Holder();
				holder.albumImage = (ImageView) convertView
						.findViewById(R.id.album_image);
				holder.albumName = (TextView) convertView
						.findViewById(R.id.album_name);
				holder.albumInfo = mPics.albumInfos[position];
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.albumName.setText(mPics.albumInfos[position].name);
			if (position < mBitmapList.size()) {
				holder.albumImage.setImageBitmap(mBitmapList.get(position));
			}
			return convertView;
        }

        
    }
    static class Holder {
    	ImageView albumImage;
    	TextView albumName;
    	QzonePics.AlbumInfo albumInfo;
    }
    /**
     * 
     * @author zivonchen
     *
     */
    private class AlbumListListener extends BaseUIListener {
		private String mScope = "all";
        private Boolean mNeedReAuth = false;
        private Activity mActivity;
    	public AlbumListListener(String scope, boolean needReAuth,
				Activity activity) {
			super(activity);
			this.mScope = scope;
			this.mNeedReAuth = needReAuth;
			this.mActivity = activity;
		}

        @Override
        public void onComplete(final Object response) {
        	Util.dismissDialog();
            try {
                final Activity activity = QzonePicturesActivity.this;
                JSONObject json = (JSONObject)response;
                int ret = json.getInt("ret");
                if (ret == 0 && mScope.equals("list_album")) {
                    mPicsJson = json;
                    // 初始化UI
                   Message msg = new Message();
                   msg.what = 1;
                   mHandler.sendMessage(msg);
                } else if(mScope.equals("add_album")){
                	Message msg = new Message();
                	msg.what = 3;
                	msg.obj = response.toString();
                	mHandler.sendMessage(msg);
                } else if(mScope.equals("upload_pic")){
                	Message msg = new Message();
                	msg.what = 4;
                	msg.obj = response.toString();
                	mHandler.sendMessage(msg);
                }else if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                MainActivity.mTencent.reAuth(activity, mScope,
                                        new BaseUIListener(QzonePicturesActivity.this));
                            }
                        };
                        QzonePicturesActivity.this.runOnUiThread(r);
                    }
                } else if (ret == 100031){
                	Util.toastMessage(QzonePicturesActivity.this, "第三方应用没有对该api操作的权限,请发送邮件进行OpenAPI权限申请");
                }
            } catch (JSONException e) {
                Util.toastMessage(QzonePicturesActivity.this, "onComplete() JSONException: " + response.toString());
            }
            Util.dismissDialog();
        }
    }
    private class PhotoListListener extends BaseUIListener {

		

		public PhotoListListener(String scope, boolean needReAuth,
				Activity activity) {
			super(activity);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onComplete(Object response) {
			Util.dismissDialog();
			Message msg = new Message();
			msg.what = 2;
			msg.obj = response;
			mHandler.sendMessage(msg);
		}
    	
    }
    
    /**
     * QZone相册信息
     * @author zivonchen
     *
     */
    static class QzonePics {
        int ret;
        String msg;
        int albumnum;

        AlbumInfo[] albumInfos;
        
        /**
         * 单个相册信息
         * @author zivonchen
         *
         */
        static class AlbumInfo {
            long createtime;
            String desc;
            int picnum;
            String name;
            String coverurl;
            int priv;
            String albumid;
            int classid;
        }
    }
    
    private final static class QzonePicsFactory {
        public static final QzonePics parseJson(JSONObject jsonObject) throws JSONException {
            QzonePics QzonePics = new QzonePics();
            QzonePics.ret = jsonObject.getInt("ret");
            QzonePics.msg = jsonObject.getString("msg");
            QzonePics.albumnum = jsonObject.getInt("albumnum");
            JSONArray albumArray = jsonObject.getJSONArray("album");
            if(QzonePics.albumnum != 0){
            	QzonePics.albumInfos = new QzonePics.AlbumInfo[QzonePics.albumnum];
            }
			if (QzonePics.albumInfos != null) {
				for (int i = 0; i < albumArray.length(); i++) {
					JSONObject albumInfo = albumArray.getJSONObject(i);
					QzonePics.AlbumInfo info = new QzonePics.AlbumInfo();
					info.createtime = albumInfo.getLong("createtime");
					info.desc = albumInfo.getString("desc");
					info.picnum = albumInfo.getInt("picnum");
					info.name = albumInfo.getString("name");
					info.coverurl = albumInfo.getString("coverurl");
					info.priv = albumInfo.getInt("priv");
					info.albumid = albumInfo.getString("albumid");
					info.classid = albumInfo.getInt("classid");
					QzonePics.albumInfos[i] = info;
				}
			}
            return QzonePics;
        }
    }
}
