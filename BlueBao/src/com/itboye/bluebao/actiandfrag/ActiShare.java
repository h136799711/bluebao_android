package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itboye.bluebao.R;
import com.umeng.scrshot.UMScrShotController.OnScreenshotListener;
import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
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

		tv_share_yes.setOnClickListener(this);
		tv_share_no.setOnClickListener(this);

		/*
		 * //设置website的方式如下 mController.setAppWebSite(SHARE_MEDIA.WEIXIN_CIRCLE,
		 * "http://www.baidu.com");
		 */

		/*
		 * //集成微信的代码 String appID = "wx967daebe835fbeac"; String appSecret =
		 * "5fa9e68ca3970e87a1f83e563c8dcbce"; // 微信图文分享必须设置一个Url //String
		 * contentUrl = "http://t.cn/zTXUNMu"; String contentUrl = ""; //
		 * 添加微信平台，参数1为当前Activity， 参数2为用户申请AppID,参数3为点击分享内容跳转到的目标url // 添加微信平台
		 * UMWXHandler wxHandler = new
		 * UMWXHandler(ActiShare.this,appID,contentUrl);
		 * wxHandler.addToSocialSDK(); // 添加微信朋友圈 UMWXHandler wxCircleHandler =
		 * new UMWXHandler(ActiShare.this,appID,appSecret);
		 * wxCircleHandler.setToCircle(true); wxCircleHandler.addToSocialSDK();
		 */

	}

	@Override
	protected void onResume() {
		// 各平台SSO配置
		mController.getConfig().removePlatform(SHARE_MEDIA.QQ);
		mController.getConfig().removePlatform(SHARE_MEDIA.QZONE);
		// 1 微信 朋友圈
		
		String appId = "wx5df8e721b02d41d1";
		String appSecret = "2a559489a116a34453f8e1368db40d25"; 
		// 添加微信平台
	    UMWXHandler	 wxHandler = new UMWXHandler(ActiShare.this, appId, appSecret);
		wxHandler.addToSocialSDK(); 
		// 添加微信朋友圈 
		UMWXHandler wxCircleHandler =new UMWXHandler(ActiShare.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		 

		// 2 新浪微博
		//mController.getConfig().setSsoHandler(new SinaSsoHandler());// 设置新浪SSO
																	// handler

		// 3 腾讯微博
		//mController.getConfig().setSsoHandler(new TencentWBSsoHandler());// 设置腾讯微博SSO
																			// handler
		/*
		 * 1.手机中必须安装微博客户端V3.8.1及以上的版本才支持SSO功能.
		 * 2.腾讯微博的SSO没有回调。由于腾讯微博SSO没有提供回调，因此腾讯微博SSO不会在onActivityResult方法内被调用
		 * （腾讯微博授权流程不经过onActivityResult方法).
		 */

		// 4 QQ空间
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		//QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ActiShare.this, "1104734137", "x4F6P6MXAkkVR4PJ");
		//qZoneSsoHandler.addToSocialSDK();

		super.onResume();
	}

	// 配置sso授权回调
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

			// 设置分享内容
			mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能");
			// 设置分享图片, 参数2为图片的url地址
   			//mController.setShareMedia(new UMImage(ActiShare.this, R.drawable.ic_launcher));

			// UMAppAdapter为无SurfaceView的应应截图适配器,其他类型的截屏适配器实现请参考2.4章节
			mShakeController.takeScrShot(ActiShare.this, new UMAppAdapter(ActiShare.this), new OnScreenshotListener() {

				@Override
				public void onComplete(Bitmap bmp) {
					if (bmp != null) {
						// bmp就是屏幕截图
					}
				}
			});

			// UMAppAdapter为无SurfaceView的应应截图适配器,其他类型的截屏适配器实现请参考2.4章节
			mShakeController.openShare(ActiShare.this, false, new UMAppAdapter(ActiShare.this));
			// mController.getConfig().removePlatform( SHARE_MEDIA.RENREN,
			// SHARE_MEDIA.DOUBAN);

			// 是否只有已登录用户才能打开分享选择页
			// mController.openShare(ActiShare.this, false);
			// mController.postShare(ActiShare.this, null, null);
			break;

		case R.id.acti_share_tv_share_no:
			mController.dismissShareBoard();
			break;
		}
	}

	/*
	 * @Override //监听点击了后退键 public void onBackPressed() { Intent intent = new
	 * Intent(ActiShare.this,ActiMainTest3.class); startActivity(intent);
	 * super.onBackPressed(); }
	 */
}