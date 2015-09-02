package com.itboye.bluebao.actiandfrag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.CodeAndData;
import com.itboye.bluebao.bean.PInfo;
import com.itboye.bluebao.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 个人资料activity，头像的上传和个人信息的修改只能在这里
 * 
 * @author Administrator
 */
public class ActiPersonalInfo extends Activity {

	protected static final String TAG = "-----ActiPersonalInfo";
	private HttpUtils httpUtils = new HttpUtils();
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Gson gson = new Gson();
	LayoutInflater inflater;
	private View parentView;// for PopupWindow to show in it

	// private ImageButton ibtn_save;
	private int uid;
	private ProgressDialog pdialog;
	private PopupWindow popWindow = null;

	private static final int REQUEST_CODE_SAVE = 0;
	private static final int REQUEST_CODE_CROP = 1;

	private ImageView iv_userimg;// 用户头像
	private EditText et_usernickname;
	private EditText et_userSentence;
	private RadioGroup rg_usergender;
	private TextView tv_userAge;
	private TextView tv_userHeight;
	private TextView tv_userWeightNow;// 当前体重
	private TextView tv_userWeightTarget;// 目标体重
	private TextView tv_userBMI;
	private RadioButton gender_male;
	private RadioButton gender_female;

	private String userNickname;
	private String userSentence = "在此填写个性签名";// 个性签名
	private int gender;// 0 female 1 male 默认：男
	private int age;
	private int userHeight;
	private int userWeightNow;
	private int userWeightTarget;
	private String userBMI; // 体质指数（BMI）=体重（kg）÷身高^2（m）
							// EX：70kg÷（1.75×1.75）=22.86

	private Button btn_save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_personalinfo);
		inflater = LayoutInflater.from(ActiPersonalInfo.this);

		// ibtn_save = (ImageButton)
		// findViewById(R.id.acti_personalinfo_ibtn_save);// 便于avd中保存操作
		// ibtn_save.setOnClickListener(new MyOnClickListener());

		iv_userimg = (ImageView) findViewById(R.id.acti_personalinfo_iv_userimg);
		et_usernickname = (EditText) findViewById(R.id.acti_personalinfo_et_username);
		et_userSentence = (EditText) findViewById(R.id.acti_personalinfo_et_userSentence);
		tv_userBMI = (TextView) findViewById(R.id.acti_personalinfo_tv_userBMI);

		gender_male = (RadioButton) findViewById(R.id.acti_personalinfo_rbtn_userGender_male);
		gender_female = (RadioButton) findViewById(R.id.acti_personalinfo_rbtn_userGender_female);
		rg_usergender = (RadioGroup) findViewById(R.id.acti_personalinfo_rg_userGender);
		rg_usergender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.acti_personalinfo_rbtn_userGender_female)
					gender = 0;
				else
					gender = 1;
			}
		});

		tv_userAge = (TextView) findViewById(R.id.acti_personalinfo_tv_userAge);
		tv_userHeight = (TextView) findViewById(R.id.acti_personalinfo_tv_userHeight);
		tv_userWeightNow = (TextView) findViewById(R.id.acti_personalinfo_tv_userWeightNow);
		tv_userWeightTarget = (TextView) findViewById(R.id.acti_personalinfo_tv_userWeightTarget);
		btn_save = (Button) findViewById(R.id.acti_personalinfo_btn_save);

		iv_userimg.setOnClickListener(new MyOnClickListener());
		tv_userAge.setOnClickListener(new MyOnClickListener());
		tv_userHeight.setOnClickListener(new MyOnClickListener());
		tv_userWeightNow.setOnClickListener(new MyOnClickListener());
		tv_userWeightTarget.setOnClickListener(new MyOnClickListener());
		btn_save.setOnClickListener(new MyOnClickListener());

	}

	@Override
	protected void onResume() {
		// 如果SP中已经保存有数据，则显示保存的数据
		sp = this.getSharedPreferences(Util.SP_FN_PINFO, Context.MODE_PRIVATE);
		String strtemp = sp.getString(Util.SP_KEY_PINFO, "");
		if (!strtemp.isEmpty()) {
			PInfo pInfo = gson.fromJson(strtemp, PInfo.class);

			et_usernickname.setText(pInfo.getNickname());
			et_userSentence.setText(pInfo.getSignature());
			if (pInfo.getGender() == 1) {
				gender_male.setChecked(true);
			} else {
				gender_female.setChecked(true);
			}
			tv_userAge.setText(pInfo.getAge() + "岁");
			tv_userHeight.setText(pInfo.getHeight() + "cm");
			tv_userWeightNow.setText(pInfo.getWeight() + "kg");
			tv_userWeightTarget.setText(pInfo.getWeightTarget() + "kg");
			tv_userBMI.setText(pInfo.getBMI() + "");

			// 防止没有改动sp的值，提交之后，下次显示全变为0
			gender = pInfo.getGender();
			age = pInfo.getAge();
			userHeight = pInfo.getHeight();
			userWeightNow = pInfo.getWeight();
			userWeightTarget = pInfo.getWeightTarget();
			userBMI = pInfo.getBMI();

		}else{
			et_usernickname.setText("爱运动 享自由");
			et_userSentence.setText("个性签名");
			gender = 1;
			age = 21;
			userHeight = 165;
			userWeightNow = 65;
			userWeightTarget = 65;
			userBMI = 23.88+"";
		}

		// 如果用户修改过头像，则显示更改之后的头像
		sp = ActiPersonalInfo.this.getSharedPreferences(Util.SP_FN_USERIMG, Context.MODE_PRIVATE);
		String strUserImgName = sp.getString(Util.SP_KEY_USERIMG_USE, "");
		String strUserImgPath = sp.getString(Util.SP_KEY_USERIMG_PATH, "");
		String strUserImgUrl = strUserImgPath + "/" + strUserImgName;
		Log.i(TAG, "strUserImgPath is : " + strUserImgPath);
		Log.i(TAG, "strUserImgName is : " + strUserImgName);
		Log.i(TAG, "strUserImgUrl is : " + strUserImgUrl);

		// 若存在，判断其中是否有需要的这张图片
		if (strUserImgUrl.length() != 1) {
			Bitmap photoBitmap = BitmapFactory.decodeFile(strUserImgUrl);
			if (photoBitmap != null) {
				iv_userimg.setImageBitmap(photoBitmap);
			} else {
				iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
			}
		} else {
			iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
		}

		super.onResume();
	}

	// =========================================================
	private class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.acti_personalinfo_tv_userAge:// 点击显示身高的TextView

				LinearLayout dialog_userage = (LinearLayout) inflater.inflate(R.layout.layout_dialog_userage, null);
				NumberPicker picUserAge = (NumberPicker) dialog_userage.findViewById(R.id.dialog_userage);
				picUserAge.setMaxValue(100);
				picUserAge.setMinValue(10);
				picUserAge.setValue(21); // 默认显示数值
				picUserAge.setOnValueChangedListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						age = newVal;
					}
				});

				// AlertDialog
				AlertDialog.Builder builderAge = new AlertDialog.Builder(ActiPersonalInfo.this);
				builderAge.setTitle("年龄设置").setView(dialog_userage) // 自定义弹出框样式
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (age != 0) {
									tv_userAge.setText(age + "岁");
								} else {
									age = 21;
									tv_userAge.setText(21 + "岁");// default
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create().show(); // 创建并展示

				break;

			case R.id.acti_personalinfo_tv_userHeight:// 点击显示身高的TextView

				LinearLayout dialog_userheight = (LinearLayout) inflater.inflate(R.layout.layout_dialog_userheight, null);
				NumberPicker picUserHeight = (NumberPicker) dialog_userheight.findViewById(R.id.dialog_userheight);
				picUserHeight.setMaxValue(230);
				picUserHeight.setMinValue(130);
				picUserHeight.setValue(165); // 默认显示数值
				picUserHeight.setOnValueChangedListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						userHeight = newVal;
					}
				});

				// AlertDialog
				AlertDialog.Builder builderHeight = new AlertDialog.Builder(ActiPersonalInfo.this);
				builderHeight.setTitle("身高设置").setView(dialog_userheight) // 自定义弹出框样式
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (userHeight != 0) {
									tv_userHeight.setText(userHeight + "cm");
								} else {
									userHeight = 165;
									tv_userHeight.setText(165 + "cm");// default
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create().show(); // 创建并展示

				break;

			case R.id.acti_personalinfo_tv_userWeightNow:// 点击显示身高的TextView

				LinearLayout dialog_userweightnow = (LinearLayout) inflater.inflate(R.layout.layout_dialog_userweightnow, null);
				NumberPicker picUserWeightNow = (NumberPicker) dialog_userweightnow.findViewById(R.id.dialog_userweightnow);
				picUserWeightNow.setMaxValue(150);
				picUserWeightNow.setMinValue(40);
				picUserWeightNow.setValue(65); // 默认显示数值
				picUserWeightNow.setOnValueChangedListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						userWeightNow = newVal;
					}
				});

				// AlertDialog
				AlertDialog.Builder builderWeightNow = new AlertDialog.Builder(ActiPersonalInfo.this);
				builderWeightNow.setTitle("当前体重").setView(dialog_userweightnow) // 自定义弹出框样式
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (userWeightNow != 0) {
									tv_userWeightNow.setText(userWeightNow + "kg");
								} else {
									userWeightNow = 65;
									tv_userWeightNow.setText(65 + "kg");// default
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create().show(); // 创建并展示

				break;

			case R.id.acti_personalinfo_tv_userWeightTarget:// 点击显示身高的TextView

				LinearLayout dialog_userweighttarget = (LinearLayout) inflater.inflate(R.layout.layout_dialog_userweighttarget, null);
				NumberPicker picUserWeightTarget = (NumberPicker) dialog_userweighttarget.findViewById(R.id.dialog_userweighttarget);
				picUserWeightTarget.setMaxValue(150);
				picUserWeightTarget.setMinValue(40);
				picUserWeightTarget.setValue(65); // 默认显示数值
				picUserWeightTarget.setOnValueChangedListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						userWeightTarget = newVal;
					}
				});

				// AlertDialog
				AlertDialog.Builder builderWeightTarget = new AlertDialog.Builder(ActiPersonalInfo.this);
				builderWeightTarget.setTitle("目标体重").setView(dialog_userweighttarget) // 自定义弹出框样式
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (userWeightTarget != 0) {
									tv_userWeightTarget.setText(userWeightTarget + "kg");
								} else {
									userWeightTarget = 65;
									tv_userWeightTarget.setText(65 + "kg");// default
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create().show(); // 创建并展示

				break;

			// case R.id.acti_personalinfo_ibtn_save://
			// actionbar中的save和最下边的save执行相同的操作
			case R.id.acti_personalinfo_btn_save: // ==保存到服务器===

				// 0 更新BMI值
				Log.i(TAG, "userWeightNow is :" + userWeightNow + "     userWeightTarget is :" + userWeightTarget
						+ "     userAge is :" + age + "     userGender is :" + gender + "     userHeight is :" + userHeight
						+ "     userHeight is :" + userHeight + "    height ping fang chu yi 10000 is:" + userHeight * userHeight
						/ 10000.0);

				float flo = (float) (userWeightNow / (userHeight * userHeight / 10000.0)); // 默认值
				String userBMIVlaue = Util.df.format(flo);// 保留两位小数
				tv_userBMI.setText(userBMIVlaue+"");// 更新页面上BMI值

				// 1 取得数据
				// userName、userSentence、gender、age、userHeight、userWeightNow、userWeightTarget
				userNickname = et_usernickname.getText().toString().trim();
				userSentence = et_userSentence.getText().toString().trim();

				// 2 把数据保存到SP中
				PInfo pInfo = new PInfo();
				// pInfo.setUsername(Util.username);
				// pInfo.setPassword(Util.password);
				pInfo.setNickname(userNickname);
				pInfo.setSignature(userSentence);
				pInfo.setGender(gender);
				pInfo.setAge(age);
				pInfo.setHeight(userHeight);
				pInfo.setWeight(userWeightNow);
				pInfo.setWeightTarget(userWeightTarget);
				pInfo.setBMI(userBMIVlaue);

				String strtempp = gson.toJson(pInfo);

				sp = ActiPersonalInfo.this.getSharedPreferences(Util.SP_FN_PINFO, Context.MODE_PRIVATE);
				editor = sp.edit();
				editor.putString(Util.SP_KEY_PINFO, strtempp);
				editor.commit();

				// 3 获取token并保存到服务器
				String token = Util.getAccessToken(ActiPersonalInfo.this);
				if (token.isEmpty()) {
					Toast.makeText(ActiPersonalInfo.this, "更新失败(token is null)，请重新提交", Toast.LENGTH_SHORT).show();
				} else {

					RequestParams params = new RequestParams();
					params.addBodyParameter("nickname", userNickname);// username就是email
					params.addBodyParameter("signature", userSentence);
					params.addBodyParameter("sex", gender + "");
					params.addBodyParameter("height", userHeight + "");
					params.addBodyParameter("weight", userWeightNow + "");
					params.addBodyParameter("target_weight", userWeightTarget + "");
					params.addBodyParameter("birthday", age + "");

					String urlUpdatePI = Util.urlUpdatePI + token;
					Log.i(TAG, urlUpdatePI);

					httpUtils.send(HttpMethod.POST, urlUpdatePI, params, new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Log.i(TAG, "更新失败：" + arg0.getLocalizedMessage() + "  " + arg0.getMessage() + "  " + arg1);
							Toast.makeText(ActiPersonalInfo.this, "更新失败：" + arg1, Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Log.i(TAG, "更新成功：" + arg0.result);
							CodeAndData info = gson.fromJson(arg0.result, CodeAndData.class);
							if (info.getCode() == -1) {
								Toast.makeText(ActiPersonalInfo.this, "更新失败：" + info.getData(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(ActiPersonalInfo.this, "更新成功：" + info.getData(), Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(ActiPersonalInfo.this, ActiMainTest3.class);
								startActivity(intent);
							}
						}
					});

				}
				break;

			// ==头像图片处理==============================================================================================
			case R.id.acti_personalinfo_iv_userimg:// 选择图片或者拍照，剪裁之后显示，保存在本地，并上传

				Toast.makeText(ActiPersonalInfo.this, "用户图片被点击了", Toast.LENGTH_SHORT).show();

				popWindow = new PopupWindow(ActiPersonalInfo.this);// 类似AlertDialog
				parentView = getLayoutInflater().inflate(R.layout.layout_acti_personalinfo, null);
				View view = getLayoutInflater().inflate(R.layout.layout_dialog_uploadimg, null);
				popWindow.setWidth((int) getResources().getDimension(R.dimen.widthPopupWindow));
				popWindow.setHeight((int) getResources().getDimension(R.dimen.heightPopupWindow));
				popWindow.setOutsideTouchable(true);// 点击popWindow之外区域的时候，popWindow消失
				popWindow.setContentView(view);
				popWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);// 放置的位置

				TextView tv_choseLocalImg = (TextView) view.findViewById(R.id.dialog_uploadimg_tv_choselocalimg);
				TextView tv_takePhoto = (TextView) view.findViewById(R.id.dialog_uploadimg_tv_takeimg);

				tv_choseLocalImg.setOnClickListener(this);
				tv_takePhoto.setOnClickListener(this);

				break;

			case R.id.dialog_uploadimg_tv_choselocalimg:
				// Toast.makeText(ActiPersonalInfo.this, "选择本地图片",
				// Toast.LENGTH_SHORT).show();

				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(openAlbumIntent, REQUEST_CODE_CROP);
				popWindow.dismiss();

				break;

			case R.id.dialog_uploadimg_tv_takeimg:
				// Toast.makeText(ActiPersonalInfo.this, "拍照",
				// Toast.LENGTH_SHORT).show();

				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
				startActivityForResult(openCameraIntent, REQUEST_CODE_CROP);
				popWindow.dismiss();

				break;
			}
		}
	}

	/**
	 * onActivityResult
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQUEST_CODE_CROP: // 剪裁

				if (data != null) {// 本地图片
					// Log.i(TAG, "data is  not  null");
					Uri uri = data.getData();
					// Log.i(TAG, "uri is :" + uri);
					crop(uri, REQUEST_CODE_SAVE);
				} else {// 拍照
						// Log.i(TAG, "data is null");
						// Log.i(TAG, "uri is :" + Uri.fromFile(new
						// File(Environment.getExternalStorageDirectory(),"temp.jpg")));
					crop(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")), REQUEST_CODE_SAVE);
				}

				break;

			case REQUEST_CODE_SAVE:// 保存 本地and服务器
				// 通过data.getData()获取的uri为空

				Bitmap photo = null;// 处理过的图片

				Bundle extras = data.getExtras();
				if (extras != null) {// 本地图片
					photo = extras.getParcelable("data");
				}

				if (photo == null) { // 加载截图
					photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.jpg");
				}

				iv_userimg.setImageBitmap(photo);

				saveImgToLocal(photo);

				break;

			}
		}
	}

	/**
	 * 剪裁图片
	 * 
	 * @param uri
	 */
	private void crop(Uri uri, int requestCode) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 120);
		intent.putExtra("outputY", 120);
		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		// 开启一个带有返回值的Activity，请求码为requestCode
		startActivityForResult(intent, requestCode);
	}

	private void saveImgToLocal(Bitmap photo) {

		String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluebao";// 路径
		String imgName = System.currentTimeMillis() + ".jpg";// 文件名
		Log.i(TAG, "保存路径：" + imgPath);
		Log.i(TAG, "imgName is: " + imgName);

		sp = getApplicationContext().getSharedPreferences(Util.SP_FN_USERIMG, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(Util.SP_KEY_USERIMG_PATH, imgPath);
		editor.putString(Util.SP_KEY_USERIMG_USE, imgName);
		editor.commit();

		// 文件夹不存在则创建之
		File dir = new File(imgPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 删除之前文件夹下的所有文件，每次只有一张图片
		File[] files = new File(imgPath).listFiles();
		for (File filed : files) {
			Log.i(TAG, "要删除的文件 is: " + filed.getName());
			filed.delete();
		}

		// 解析出文件【bitmap--file】保存到imgPath下，文件名为imgName
		File photoFile = new File(imgPath, imgName);// 要保存的图片文件
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(photoFile);
			if (photo.compress(Bitmap.CompressFormat.JPEG, 80, fos)) {
				fos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 保存到服务器

	}

}