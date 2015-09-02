package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.Util;
import com.umeng.scrshot.UMScrShotController.OnScreenshotListener;
import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMShareBoardListener;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 分享activity 点击ActiMainTest2中的分享tab时，弹出FragTabShare
 * fragment，点击其中的分享按钮弹出此activity
 * 
 * @author Administrator
 *
 */
public class ActiShare extends Activity implements OnClickListener {

	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	// 声明mShakeController, 参数1为sdk 控制器描述符
	UMShakeService mShakeController = UMShakeServiceFactory.getShakeService("write.your.content");

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

		mController.getConfig().removePlatform(SHARE_MEDIA.QQ);
		mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
		// 1 微信 朋友圈
		String appId = "wx5df8e721b02d41d1";
		String appSecret = "2a559489a116a34453f8e1368db40d25";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ActiShare.this, appId, appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ActiShare.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.acti_share_tv_share_yes:
			// 把确定和取消隐藏掉
			// tv_share_yes.setVisibility(View.INVISIBLE);
			// tv_share_no.setVisibility(View.INVISIBLE);

			// 设置分享内容
			// mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能");
			// 设置分享图片, 参数2为图片的url地址
			// mController.setShareMedia(new UMImage(ActiShare.this,
			// R.drawable.ic_launcher));

			// UMAppAdapter为无SurfaceView的应应截图适配器,其他类型的截屏适配器实现请参考2.4章节
			mShakeController.takeScrShot(ActiShare.this, new UMAppAdapter(ActiShare.this), new OnScreenshotListener() {

				@Override
				public void onComplete(Bitmap bmp) {
					if (bmp != null) {
						// bmp就是屏幕截图
					}
				}
			});
			
		/*	mController.setShareBoardListener(new UMShareBoardListener() {
				@Override
				public void onShow() {
					tv_share_yes.setVisibility(View.VISIBLE);
					tv_share_no.setVisibility(View.VISIBLE);
				}

				@Override
				public void onDismiss() {
					tv_share_yes.setVisibility(View.INVISIBLE);
					tv_share_no.setVisibility(View.INVISIBLE);
				}
			});*/

			// UMAppAdapter为无SurfaceView的应应截图适配器,其他类型的截屏适配器实现请参考2.4章节
			mShakeController.openShare(ActiShare.this, false, new UMAppAdapter(ActiShare.this));


			
			break;

		case R.id.acti_share_tv_share_no:
			mController.dismissShareBoard();
			break;
		}
	}

}