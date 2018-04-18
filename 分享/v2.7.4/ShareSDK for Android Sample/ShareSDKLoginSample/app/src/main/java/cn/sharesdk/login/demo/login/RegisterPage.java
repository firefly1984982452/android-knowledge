package cn.sharesdk.login.demo.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.login.demo.R;

/** 此页面时注册页面，需要的话可以不要这个改成您自己的注册页面 */
public class RegisterPage extends Activity implements OnClickListener, Callback{

	private static final int INTENT_ACTION_PICTURE = 0;
	private static final int INTENT_ACTION_CAREMA = 1;
	private static final int INTENT_ACTION_CROP = 2;
	private static final int LOAD_USER_ICON = 3;

	private static final String PICTURE_NAME = "UserIcon.jpg";

	private static OnLoginListener tmpRegisterListener;
	private static String tmpPlatform;

	private OnLoginListener registerListener;
	private Platform platform;
	private ImageView ivUserIcon, ivBoy, ivGril;
	private TextView tvUserNote;
	private EditText tvUserName;
	private String picturePath; //图片路径
	private UserInfo userInfo = new UserInfo();

	static final void setOnLoginListener(OnLoginListener login){
		 tmpRegisterListener = login;
	 }

	static final void setPlatform(String platName) {
		tmpPlatform = platName;
	}

	protected void onCreate(Bundle savedInstanceState) {
		registerListener = tmpRegisterListener;
		platform = ShareSDK.getPlatform(tmpPlatform);
		tmpRegisterListener = null;
		tmpPlatform = null;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		tvUserName = (EditText)findViewById(R.id.tv_user_name);
		tvUserNote = (TextView)findViewById(R.id.tv_user_note);
		ivUserIcon = (ImageView)findViewById(R.id.iv_user_icon);
		ivBoy = (ImageView)findViewById(R.id.image_boy);
		ivGril = (ImageView)findViewById(R.id.image_gril);

		ivUserIcon.setOnClickListener(this);
		findViewById(R.id.tv_ensure).setOnClickListener(this);
		tvUserNote.setOnClickListener(this);
		findViewById(R.id.layout_boy).setOnClickListener(this);
		findViewById(R.id.layout_gril).setOnClickListener(this);
		findViewById(R.id.ll_back).setOnClickListener(this);

		initData();
	}

	/*初始化用户数据*/
	private void initData(){
		String gender = "";
		if(platform != null){
			gender = platform.getDb().getUserGender();
			if("m".equals(gender)){
				userInfo.setUserGender(UserInfo.Gender.MALE);
				ivBoy.setVisibility(View.VISIBLE);
				ivGril.setVisibility(View.INVISIBLE);
			}else{
				userInfo.setUserGender(UserInfo.Gender.FEMALE);
				ivBoy.setVisibility(View.INVISIBLE);
				ivGril.setVisibility(View.VISIBLE);
			}
			userInfo.setUserIcon(platform.getDb().getUserIcon());
			userInfo.setUserName(platform.getDb().getUserName());
			userInfo.setUserNote(platform.getDb().getUserId());
		}
		tvUserName.setText(userInfo.getUserName());
		tvUserNote.setText(userInfo.getUserNote());
		// 加载头像
		if(!TextUtils.isEmpty(userInfo.getUserIcon())){
			loadIcon();
		}
		//先把getUserIcon获取到的图片保存在本地，再通过ImageView调用显示出来
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String thumPicture = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getPackageName()+"/download";
			File pictureParent =new File(thumPicture);
			File pictureFile =new File(pictureParent, PICTURE_NAME);

			if(!pictureParent.exists()){
				pictureParent.mkdirs();
			}
			try{
				if (!pictureFile.exists()) {
					pictureFile.createNewFile();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			//最终图片路径
			picturePath = pictureFile.getAbsolutePath();
			Log.d("picturePath ==>>", picturePath);
		}else{
			Log.d("change user icon ==>>", "there is not sdcard!");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.tv_ensure:
			// 执行注册
			if (registerListener.onRegister(userInfo)) {
				Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, R.string.register_failed, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.iv_user_icon:
			getPicture();
			break;
		case R.id.layout_boy:
			ivGril.setVisibility(View.INVISIBLE);
			ivBoy.setVisibility(View.VISIBLE);
			break;
		case R.id.layout_gril:
			ivGril.setVisibility(View.VISIBLE);
			ivBoy.setVisibility(View.INVISIBLE);
			break;
		}
	}

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case LOAD_USER_ICON:
			ivUserIcon.setImageURI(Uri.parse(picturePath));
			break;
		}
		return false;
	}

	/**加载用户登陆后返回的头像*/
	private void loadIcon() {
		final String imageUrl = platform.getDb().getUserIcon();
		new Thread(new Runnable() {
			public void run() {
				try {
					URL picUrl = new URL(imageUrl);
					Bitmap userIcon = BitmapFactory.decodeStream(picUrl.openStream());
			        FileOutputStream b = null;
			        try {
			        	b = new FileOutputStream(picturePath);
			        	userIcon.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			        } finally {
			        	try {
			        		b.flush();
			                b.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			        userInfo.setUserIcon(picturePath);
			        Message msg = new Message();
					msg.what = LOAD_USER_ICON;
					ivUserIcon.post(new Runnable() {
						public void run() {
							ivUserIcon.setImageURI(Uri.parse(picturePath));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**接受返回的图片源*/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == INTENT_ACTION_PICTURE && resultCode == RESULT_OK && null != data){
			Cursor c = getBaseContext().getContentResolver().query(data.getData(), null, null, null, null);
			c.moveToNext();
			String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
			c.close();
			if(new File(path).exists()){
				Log.d(getClass().getSimpleName(), "onActivityResult == " + path +" == exist");
				userInfo.setUserIcon(path);
				ivUserIcon.setImageBitmap(Tool.compressImageFromFile(path));
			}
		}else if(requestCode == INTENT_ACTION_CAREMA && resultCode == RESULT_OK){
			userInfo.setUserIcon(picturePath);
			ivUserIcon.setImageDrawable(Drawable.createFromPath(picturePath));
		}else if(requestCode == INTENT_ACTION_CROP && resultCode == RESULT_OK && null != data){
			ivUserIcon.setImageDrawable(Drawable.createFromPath(picturePath));
		}
	}

	/*从相册获取图片，把一张本地图片当做头像*/
	private void getPicture(){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, INTENT_ACTION_PICTURE);
	}

	/**打开相机照相，把照相后的图片当做头像*/
//	private void openCamera(){
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picturePath)));
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//		startActivityForResult(intent, INTENT_ACTION_CAREMA);
//	}
}
