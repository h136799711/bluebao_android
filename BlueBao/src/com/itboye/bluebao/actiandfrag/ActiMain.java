package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.itboye.bluebao.R;
import com.itboye.bluebao.breceiver.ReceiverTool;
import com.itboye.bluebao.util.DoubleClickExitHelper;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * activity main
 * @author Administrator
 */
public class ActiMain extends Activity implements OnClickListener {

	protected static final String TAG = "-----ActiMain";
	private DoubleClickExitHelper doubleClick = new DoubleClickExitHelper(ActiMain.this); 

	private DrawerLayout mDrawerLayout;
	// 用于对Fragment进行管理
	private FragmentManager fragmentManager;

	private FragTabHome fragHome; // 用于展示 首页 的Fragment
	private FragTabTarget fragTarget;
	private FragTabPcenter fragPcenter;
	private View layout_fragment_tab_home; // tab首页Layout
	private View layout_fragment_tab_target;
	private View layout_fragment_tab_pcenter;
	private View layout_fragment_tab_share;// 当按钮用 8.28
	private ImageView iv_actimain_tab_home_imageview; // tab首页上的图标
	private ImageView iv_actimain_tab_target_imageview;
	private ImageView iv_actimain_tab_pcenter_imageview;
	private TextView tv_actimain_tab_home_textview; // tab首页上的文字
	private TextView tv_actimain_tab_target_textview;
	private TextView tv_actimain_tab_pcenter_textview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_main);

		// 9.3 推送
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();
		// 获取设备的DeviceToken
		//String device_token = UmengRegistrar.getRegistrationId(ActiMain.this);

		fragmentManager = getFragmentManager();
		initDrawerLayoutView(); 
		initViews(); 
		setListenerForDrawerLayout(); 
		registerMyBCR();

		setTabSelection(0); 
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {  
	            return doubleClick.onKeyDown(keyCode, event);  
	        } 
		return super.onKeyDown(keyCode, event);
	}

	private void initDrawerLayoutView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
	}

	private void setListenerForDrawerLayout() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0); // actimai中的content部分
				View mMenu = drawerView; // menu 可以有左右两个，此app只设计了左侧
				float scale = 1 - slideOffset;
				float rightScale = 0.8f + scale * 0.2f;

				if (drawerView.getTag().equals("LEFT")) { // 依据Tag来识别是左还是右Drawer

					float leftScale = 1 - 0.3f * scale;

					ViewHelper.setScaleX(mMenu, leftScale);
					ViewHelper.setScaleY(mMenu, leftScale);
					ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
					ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));
					ViewHelper.setPivotX(mContent, 0);
					ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				}
			}

			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
			}
		});
	}

	private void initViews() {
		layout_fragment_tab_share = findViewById(R.id.actimain_tab_share_layout);

		layout_fragment_tab_home = findViewById(R.id.actimain_tab_home_layout);
		layout_fragment_tab_target = findViewById(R.id.actimain_tab_target_layout);
		layout_fragment_tab_pcenter = findViewById(R.id.actimain_tab_pcenter_layout);

		iv_actimain_tab_home_imageview = (ImageView) findViewById(R.id.actimain_tab_home_iv);
		iv_actimain_tab_target_imageview = (ImageView) findViewById(R.id.actimain_tab_target_iv);
		iv_actimain_tab_pcenter_imageview = (ImageView) findViewById(R.id.actimain_tab_pcenter_iv);

		tv_actimain_tab_home_textview = (TextView) findViewById(R.id.actimain_tab_home_tv);
		tv_actimain_tab_target_textview = (TextView) findViewById(R.id.actimain_tab_target_tv);
		tv_actimain_tab_pcenter_textview = (TextView) findViewById(R.id.actimain_tab_pcenter_tv);

		layout_fragment_tab_home.setOnClickListener(this);
		layout_fragment_tab_target.setOnClickListener(this);
		layout_fragment_tab_pcenter.setOnClickListener(this);
		layout_fragment_tab_share.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.actimain_tab_home_layout: 
			setTabSelection(0);
			break;
		case R.id.actimain_tab_target_layout: 
			setTabSelection(1);
			break;
		case R.id.actimain_tab_pcenter_layout: 
			setTabSelection(2);
			break;
		default:
			Intent intent = new Intent(ActiMain.this, ActiShare.class);
			startActivity(intent);
			break;
		}

	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	private void setTabSelection(int index) {

		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);

		switch (index) {

		case 0:
			// 当点击了首页tab时，改变控件的图片和文字颜色
			iv_actimain_tab_home_imageview.setImageResource(R.drawable.actimain_tab_home_selected);
			tv_actimain_tab_home_textview.setTextColor(getResources().getColor(R.color.colorActiMainTabWordAfter));
			//if (fragHome == null) {
				fragHome = new FragTabHome();
				transaction.add(R.id.acti_main_fl_content, fragHome);
			//} else {
			//	transaction.show(fragHome);
			//}
			break;

		case 1:
			iv_actimain_tab_target_imageview.setImageResource(R.drawable.actimain_tab_target_selected);
			tv_actimain_tab_target_textview.setTextColor(getResources().getColor(R.color.colorActiMainTabWordAfter));
			//if (fragTarget == null) {
				fragTarget = new FragTabTarget();
				transaction.add(R.id.acti_main_fl_content, fragTarget);
			//} else {
			//	transaction.show(fragTarget);
			//}
			break;

		case 2:
			iv_actimain_tab_pcenter_imageview.setImageResource(R.drawable.actimain_tab_pcenter_selected);
			tv_actimain_tab_pcenter_textview.setTextColor(getResources().getColor(R.color.colorActiMainTabWordAfter));
			//if (fragPcenter == null) {
				fragPcenter = new FragTabPcenter();
				transaction.add(R.id.acti_main_fl_content, fragPcenter);
			//} else {
			//	transaction.show(fragPcenter);
			//}
			break;

		default:
			break;
		}
		transaction.commit();
	}

	private void clearSelection() {
		iv_actimain_tab_home_imageview.setImageResource(R.drawable.actimain_tab_home_unselected);
		tv_actimain_tab_home_textview.setTextColor(Color.parseColor("#82858b"));
		iv_actimain_tab_target_imageview.setImageResource(R.drawable.actimain_tab_target_unselected);
		tv_actimain_tab_target_textview.setTextColor(Color.parseColor("#82858b"));
		iv_actimain_tab_pcenter_imageview.setImageResource(R.drawable.actimain_tab_pcenter_unselected);
		tv_actimain_tab_pcenter_textview.setTextColor(Color.parseColor("#82858b"));
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (fragHome != null) {
			transaction.remove(fragHome);
			//transaction.hide(fragHome);
		}
		if (fragTarget != null) {
			transaction.remove(fragTarget);
			//transaction.hide(fragTarget);
		}
		if (fragPcenter != null) {
			transaction.remove(fragPcenter);
			//transaction.hide(fragPcenter);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(myReceiver);// 注销广播接收器
		//mPushAgent.disable();//关闭推送9.3
		super.onDestroy();
	}

	private void registerMyBCR() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(ReceiverTool.OPEN_SLIDEMENU);
		iFilter.addAction(ReceiverTool.SHOW_MY_DETAIL);
		iFilter.addAction(ReceiverTool.SHOW_MY_AIMS);
		registerReceiver(myReceiver, iFilter);
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (ReceiverTool.OPEN_SLIDEMENU.equals(action)) {
				mDrawerLayout.openDrawer(Gravity.START);
			} else if (ReceiverTool.SHOW_MY_AIMS.equals(action)) {
				mDrawerLayout.closeDrawer(Gravity.START);
				setTabSelection(1);
			} else {
				Log.i(TAG, "没有接收到广播 ");
			}
		};
	};
}
