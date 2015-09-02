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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.breceiver.ReceiverTool;
import com.nineoldandroids.view.ViewHelper;

/**
 * activity main
 * 
 * @author Administrator
 */
public class ActiMainTest3 extends Activity implements OnClickListener {

	protected static final String TAG = "-----ActiMain";
	private long clickTime = 0; // 记录第一次点击返回键的时间

	private DrawerLayout mDrawerLayout;
	// 用于对Fragment进行管理
	private FragmentManager fragmentManager;

	private FragTabHomeTest2 fragHome; // 用于展示 首页 的Fragment
	private FragTabTarget fragTarget;
	private FragTabPcenter fragPcenter;
	private View layout_fragment_tab_home; // tab首页Layout
	private View layout_fragment_tab_target;
	private View layout_fragment_tab_pcenter;
	private View layout_fragment_tab_share;//当按钮用 8.28
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

		fragmentManager = getFragmentManager();
		initDrawerLayoutView(); // 初始化DrawerLayout
		initViews(); // 初始化布局元素
		setListenerForDrawerLayout(); // 给DrawerLayout添加监听事件
		registerMyBCR();
		
		setTabSelection(0); // 第一次启动时选中第0个tab

	}

	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
		clickTime = System.currentTimeMillis();
		if ((System.currentTimeMillis() - clickTime) > 2000) {
			android.os.Process.killProcess(android.os.Process.myPid());// 这里目前是退出功能
		}
		super.onBackPressed();
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

		case R.id.actimain_tab_home_layout: // 当点击了首页tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.actimain_tab_target_layout: // 当点击了目标tab时，选中第2个tab
			setTabSelection(1);
			break;
		case R.id.actimain_tab_pcenter_layout: // 当点击了个人中心tab时，选中第3个tab
			setTabSelection(2);
			break;
		default:
			Intent intent = new Intent(ActiMainTest3.this, ActiShare.class);
			startActivity(intent);
			break;
		}

	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示首页，1表示目标，2表示个人中心，3表示分享。
	 */
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
			if (fragHome == null) {
				fragHome = new FragTabHomeTest2();
				transaction.add(R.id.acti_main_fl_content, fragHome);
			} else {
				transaction.show(fragHome);
			}
			break;

		case 1:
			iv_actimain_tab_target_imageview.setImageResource(R.drawable.actimain_tab_target_selected);
			tv_actimain_tab_target_textview.setTextColor(getResources().getColor(R.color.colorActiMainTabWordAfter));
			if (fragTarget == null) {
				fragTarget = new FragTabTarget();
				transaction.add(R.id.acti_main_fl_content, fragTarget);
			} else {
				transaction.show(fragTarget);
			}
			break;

		case 2:
			iv_actimain_tab_pcenter_imageview.setImageResource(R.drawable.actimain_tab_pcenter_selected);
			tv_actimain_tab_pcenter_textview.setTextColor(getResources().getColor(R.color.colorActiMainTabWordAfter));
			if (fragPcenter == null) {
				fragPcenter = new FragTabPcenter();
				transaction.add(R.id.acti_main_fl_content, fragPcenter);
			} else {
				transaction.show(fragPcenter);
			}
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
			transaction.hide(fragHome);
		}
		if (fragTarget != null) {
			transaction.hide(fragTarget);
		}
		if (fragPcenter != null) {
			transaction.hide(fragPcenter);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(myReceiver);// 注销广播接收器
		//Log.i(TAG, "  注销   广播接收器成功");
		super.onDestroy();
	}

	private void registerMyBCR() {
		IntentFilter iFilter = new IntentFilter();

		iFilter.addAction(ReceiverTool.OPEN_SLIDEMENU);
		iFilter.addAction(ReceiverTool.SHOW_MY_DETAIL);
		iFilter.addAction(ReceiverTool.SHOW_MY_DATA);
		iFilter.addAction(ReceiverTool.SHOW_MY_DATA2);

		registerReceiver(myReceiver, iFilter);// 注册
		//Log.i(TAG, "  注册    广播接收器成功");
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.i(TAG, "接收到的action is：" + action);

			if (ReceiverTool.OPEN_SLIDEMENU.equals(action)) {// 打开侧滑菜单
				mDrawerLayout.openDrawer(Gravity.START);
			} // else if (ReceiverTool.SHOW_MY_DETAIL.equals(action) ) {//显示
				// tab_pcenter
				// mDrawerLayout.closeDrawer(Gravity.START);
				// setTabSelection(2);
			// }
			else if (ReceiverTool.SHOW_MY_DATA.equals(action)) {// 显示 tab_target
				mDrawerLayout.closeDrawer(Gravity.START);
				setTabSelection(0);// 8.28 先转到tab_home
			} else if (ReceiverTool.SHOW_MY_DATA2.equals(action)) {
				mDrawerLayout.closeDrawer(Gravity.START);
				setTabSelection(1);
			} else {
				Log.i(TAG, "没有接收到广播 ");
			}
		};
	};
}
