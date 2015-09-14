package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.Util;
import com.itboye.bluebao.util.UtilBitmap;
import com.umeng.analytics.MobclickAgent;
import com.umeng.scrshot.UMScrShotController.OnScreenshotListener;
import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 分享activity
 * 
 * @author Administrator
 */
public class ActiShare extends Activity implements OnClickListener {

	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	//final UMShakeService mShakeController = UMShakeServiceFactory.getShakeService("write.your.content");
	final UMShakeService mShakeController = UMShakeServiceFactory.getShakeService("com.umeng.share");
	
	private TextView tv_share_yes;
	private TextView tv_share_no;
	private TextView tv_dataMiles;
	private TextView tv_dataTime;
	private TextView tv_dataCals;
	private TextView tv_dataDays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_share);

		tv_share_yes = (TextView) findViewById(R.id.acti_share_tv_share_yes);
		tv_share_no = (TextView) findViewById(R.id.acti_share_tv_share_no);
		tv_dataMiles = (TextView) findViewById(R.id.acti_share_miles);
		tv_dataTime = (TextView) findViewById(R.id.acti_share_time);
		tv_dataCals = (TextView) findViewById(R.id.acti_share_cals);
		tv_dataDays = (TextView) findViewById(R.id.acti_share_days);

		// 赋值
		tv_dataMiles.setText(Util.miles);
		tv_dataTime.setText(Util.time);
		tv_dataCals.setText(Util.cals);
		tv_dataDays.setText(Util.continuous_day + "");

		tv_share_yes.setOnClickListener(this);
		tv_share_no.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);

		mController.getConfig().removePlatform(SHARE_MEDIA.QQ);
		mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
		// 1 微信 朋友圈
		String appId = "wx5df8e721b02d41d1";
		String appSecret = "2a559489a116a34453f8e1368db40d25";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ActiShare.this, appId, appSecret);
		wxHandler.addToSocialSDK();
		//wxHandler.showCompressToast(true);//9.9 added
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ActiShare.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		//wxCircleHandler.showCompressToast(true);//9.9 added

		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.acti_share_tv_share_yes:
			tv_share_yes.setVisibility(View.INVISIBLE);
			tv_share_no.setVisibility(View.INVISIBLE);
			mShakeController.takeScrShot(ActiShare.this, new UMAppAdapter(ActiShare.this), new OnScreenshotListener() {
				@Override
				public void onComplete(Bitmap bmp) {
					if (bmp != null) {// bmp就是屏幕截图
						Log.i("actishare", "bmp's size: " + bmp.getAllocationByteCount());
						if( bmp.getAllocationByteCount()>32768){//大于32k，压缩 9.9added
							bmp = UtilBitmap.createScaledBitmap(bmp, bmp.getWidth()/10, bmp.getHeight()/10);
							Log.i("actishare", "new bmp's size: " + bmp.getAllocationByteCount());
						}
					}
				}
			});

			mShakeController.openShare(ActiShare.this, false, new UMAppAdapter(ActiShare.this));
			
			

			tv_share_yes.setVisibility(View.VISIBLE);
			tv_share_no.setVisibility(View.VISIBLE);

			break;

		case R.id.acti_share_tv_share_no:
			mController.dismissShareBoard();
			ActiShare.this.finish();
			break;
		}
	}
}