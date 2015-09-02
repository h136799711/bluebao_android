/*package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itboye.bluebao.R;
import com.nineoldandroids.view.ViewHelper;

*//**
 * activity main
 * 
 * @author Administrator
 *
 *//*
public class ActiMainTest extends Activity implements OnClickListener {
	
	

	protected static final String TAG = "-----ActiMain";

	*//**
	 * actimain布局文件中最外边的Layout--DrawerLayout 
	 * 8.2 drawerlayout测试已成功
	 *//*
	private DrawerLayout mDrawerLayout;
	

	*//**
	 * 用于对Fragment进行管理
	 *//*
	private FragmentManager fragmentManager;

	// =================================================

	*//**
	 * Fragments：4个Fragment类
	 *//*
	private FragTabHomeTest fragHome; // 用于展示 首页 的Fragment
	private FragTabTarget fragTarget;
	private FragTabPcenter fragPcenter;
	private FragTabShare fragShare;

	*//**
	 * Layouts：4个tab的Layout
	 *//*
	private View layout_fragment_tab_home; // tab首页Layout
	private View layout_fragment_tab_target;
	private View layout_fragment_tab_pcenter;
	private View layout_fragment_tab_share;

	*//**
	 * ImageViews：4个tab上的ImageView
	 *//*
	private ImageView iv_actimain_tab_home_imageview; // tab首页上的图标
	private ImageView iv_actimain_tab_target_imageview;
	private ImageView iv_actimain_tab_pcenter_imageview;
	private ImageView iv_actimain_tab_share_imageview;

	*//**
	 * TextViews：4个tab上的TextView
	 *//*
	private TextView tv_actimain_tab_home_textview; // tab首页上的文字
	private TextView tv_actimain_tab_target_textview;
	private TextView tv_actimain_tab_pcenter_textview;
	private TextView tv_actimain_tab_share_textview;

	// =================================================

	*//**
	 * onCreate
	 *//*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_main);

		// =====8.2 drawerlayout测试 已成功====================
		initDrawerLayoutView(); // 初始化DrawerLayout
		setListenerForDrawerLayout(); // 给DrawerLayout添加监听事件
		// =====8.2 drawerlayout测试 已成功====================
		

		initViews(); // 初始化布局元素
		fragmentManager = getFragmentManager();
		setTabSelection(0); // 第一次启动时选中第0个tab

	}

	// =====8.2 drawerlayout测试 已成功====================
	*//**
	 * 初始化DrawerLayout
	 *//*
	private void initDrawerLayoutView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
	}

	*//**
	 * 给DrawerLayout添加监听事件
	 *//*
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

	// =====8.2 drawerlayout测试 已成功====================

	*//**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 *//*
	private void initViews() {

		layout_fragment_tab_home = findViewById(R.id.actimain_tab_home_layout); // 4个tab的Layout
		layout_fragment_tab_target = findViewById(R.id.actimain_tab_target_layout);
		layout_fragment_tab_pcenter = findViewById(R.id.actimain_tab_pcenter_layout);
		layout_fragment_tab_share = findViewById(R.id.actimain_tab_share_layout);

		iv_actimain_tab_home_imageview = (ImageView) findViewById(R.id.actimain_tab_home_iv); // 4个tab上的ImageView
		iv_actimain_tab_target_imageview = (ImageView) findViewById(R.id.actimain_tab_target_iv);
		iv_actimain_tab_pcenter_imageview = (ImageView) findViewById(R.id.actimain_tab_pcenter_iv);
		iv_actimain_tab_share_imageview = (ImageView) findViewById(R.id.actimain_tab_share_iv);

		tv_actimain_tab_home_textview = (TextView) findViewById(R.id.actimain_tab_home_tv); // 4个tab上的TextView
		tv_actimain_tab_target_textview = (TextView) findViewById(R.id.actimain_tab_target_tv);
		tv_actimain_tab_pcenter_textview = (TextView) findViewById(R.id.actimain_tab_pcenter_tv);
		tv_actimain_tab_share_textview = (TextView) findViewById(R.id.actimain_tab_share_tv);

		layout_fragment_tab_home.setOnClickListener(this); // 为4个tab添加OnClickListener
		layout_fragment_tab_target.setOnClickListener(this);
		layout_fragment_tab_pcenter.setOnClickListener(this);
		layout_fragment_tab_share.setOnClickListener(this);

	}

	*//**
	 * 实现OnClickListener要实现的方法
	 *//*
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
		case R.id.actimain_tab_share_layout: // 当点击了分享tab时，选中第4个tab
			setTabSelection(3);
			break;
		default:
			break;
		}

	}

	*//**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示首页，1表示目标，2表示个人中心，3表示分享。
	 *//*
	private void setTabSelection(int index) {

		// 每次选中之前先清楚掉上次的选中状态
		// TODO 此处最好是和上次的index比较一下，则不再重新显示
		clearSelection();

		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);

		switch (index) {

		case 0:
			// 当点击了首页tab时，改变控件的图片和文字颜色
			iv_actimain_tab_home_imageview.setImageResource(R.drawable.actimain_tab_home_selected);
			tv_actimain_tab_home_textview.setTextColor(Color.BLACK);
			if (fragHome == null) {
				// 如果fragHome为空，则创建一个并添加到界面上
				fragHome = new FragTabHomeTest();
				transaction.add(R.id.acti_main_fl_content, fragHome);
			} else {
				// 如果fragHome不为空，则直接将它显示出来
				transaction.show(fragHome);
			}
			break;

		case 1:
			iv_actimain_tab_target_imageview.setImageResource(R.drawable.actimain_tab_target_selected);
			tv_actimain_tab_target_textview.setTextColor(Color.BLACK);
			if (fragTarget == null) {
				fragTarget = new FragTabTarget();
				transaction.add(R.id.acti_main_fl_content, fragTarget);
			} else {
				transaction.show(fragTarget);
			}
			break;

		case 2:
			iv_actimain_tab_pcenter_imageview.setImageResource(R.drawable.actimain_tab_pcenter_selected);
			tv_actimain_tab_pcenter_textview.setTextColor(Color.BLACK);
			if (fragPcenter == null) {
				fragPcenter = new FragTabPcenter();
				transaction.add(R.id.acti_main_fl_content, fragPcenter);
			} else {
				transaction.show(fragPcenter);
			}
			break;

		case 3:
		default:
			iv_actimain_tab_share_imageview.setImageResource(R.drawable.actimain_tab_share_selected);
			tv_actimain_tab_share_textview.setTextColor(Color.BLACK);
			if (fragShare == null) {
				fragShare = new FragTabShare();
				transaction.add(R.id.acti_main_fl_content, fragShare);
			} else {
				transaction.show(fragShare);
			}
			break;

		}
		transaction.commit();
	}

	*//**
	 * 清除掉所有的选中状态。
	 *//*
	private void clearSelection() {

		iv_actimain_tab_home_imageview.setImageResource(R.drawable.actimain_tab_home_unselected);
		tv_actimain_tab_home_textview.setTextColor(Color.parseColor("#82858b"));

		iv_actimain_tab_target_imageview.setImageResource(R.drawable.actimain_tab_target_unselected);
		tv_actimain_tab_target_textview.setTextColor(Color.parseColor("#82858b"));

		iv_actimain_tab_pcenter_imageview.setImageResource(R.drawable.actimain_tab_pcenter_unselected);
		tv_actimain_tab_pcenter_textview.setTextColor(Color.parseColor("#82858b"));

		iv_actimain_tab_share_imageview.setImageResource(R.drawable.actimain_tab_share_unselected);
		tv_actimain_tab_share_textview.setTextColor(Color.parseColor("#82858b"));

	}

	*//**
	 * 将所有的Fragment都置为隐藏状态。 主要是把之前显示的Fragment隐藏起来
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 *//*
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
		if (fragShare != null) {
			transaction.hide(fragShare);
		}

	}

	// =================================================

	*//**
	 * 当Activity首次启动时，
	 * 系统会调用onCreateOptionsMenu()方法给Activity组装Action bar和悬浮菜单
	 *//*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.acti_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//======================================================
	

}
*/