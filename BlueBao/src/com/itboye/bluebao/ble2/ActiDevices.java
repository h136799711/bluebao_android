package com.itboye.bluebao.ble2;

import java.util.ArrayList;

import com.itboye.bluebao.R;
import com.itboye.bluebao.actiandfrag.ActiMain;
import com.itboye.bluebao.ble.BluetoothLeService;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActiDevices extends Activity {

	private static final String TAG = "----ActiDevices-NEW";
	private ListView lv_bledevices;
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private String deviceAddress; // 要连接的ble的mac addresss
	private BluetoothLeService mBluetoothLeService;
	private ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_new_scandevices_found);
		registerMyBCR();

		lv_bledevices = (ListView) findViewById(R.id.acti_new_scandevices_succeed_lv);
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		lv_bledevices.setAdapter(mLeDeviceListAdapter);
		lv_bledevices.setOnItemClickListener(new MyOnItemClickListener());

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		if (pdialog != null) {
			pdialog.cancel();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myrec);
		//mBluetoothLeService = null;
	}

	private void registerMyBCR() {
		Log.i(TAG, "registerMyBCR");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("FOUND_A_DEVICE");
		iFilter.addAction("CANCELPD_AND_TOHOME");
		// iFilter.addAction("ACTION_BOND_STATE_CHANGED");//设备绑定状态改变
		registerReceiver(myrec, iFilter);
	}

	// 接收 发现的设备
	private BroadcastReceiver myrec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if ("FOUND_A_DEVICE".equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra("device");
				// Log.i(TAG, "device address is :" + device.getAddress());
				if (device != null) {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			} else if ("CANCELPD_AND_TOHOME".equals(action)) {
				// 1 改变状态
				// Log.i(TAG, "模拟状态改变");
				// 2 停止dialog
				pdialog.cancel();

				// 3 转到首页
				//Intent toHomeIntent = new Intent(ActiDevices.this, ActiMain.class);
				//startActivity(toHomeIntent);

				ActiDevices.this.finish();

			}

		};
	};

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = ActiDevices.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.new_listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0) {

				if (deviceName.equals("UART-BLE")) {
					viewHolder.deviceName.setText("蓝堡动感单车");
				} else {
					viewHolder.deviceName.setText(deviceName);
				}

			} else {
				viewHolder.deviceName.setText(R.string.unknown_device);
			}
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
	}

	// 监听lv_bledevices的item点击事件
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BluetoothDevice ble = mLeDeviceListAdapter.getDevice(position);
			if (ble == null) {
				Toast.makeText(ActiDevices.this, "设备已经关闭", Toast.LENGTH_SHORT).show();
				return;
			}
			deviceAddress = ble.getAddress();// 赋给常量deviceAddress
			Log.i(TAG, "MyOnItemClickListener-deviceAddress is:" + deviceAddress);

			// 9.11
			// 绑定服务。mServiceConnection处理连接成功与否。成功：直接根据deviceAddress连接；
			// 失败：mBluetoothLeService
			Intent gattServiceIntent = new Intent(ActiDevices.this, BluetoothLeService.class);
			//gattServiceIntent.putExtra("deviceAddress", deviceAddress);//9.12和武健商量之后添加的代码，同时在BluetoothLeService中调用onStartCommand方法
			//ActiDevices.this.startService(gattServiceIntent);//9.12和武健商量之后添加的代码，同时在BluetoothLeService中调用onStartCommand方法
			//9.12小结：本以为是ActiDevices finish的时候service也被destory了，和武健商量之后添加startService，即和bindService混用的方式来使得当ActiDevices finish时，service不被destory掉，
			//但是结果是在回到ActiMain之后仍然获取不到数据。  之后我在service的disconnect close方法中添加了Log.i，根据打印出来的log判断是mBluetoothGatt.close()，所以注释了
			//disconnect 和 close 中的相关代码，同时不再和startService混用，结果仍然可以获取到数据。所以目前采用这种方法：在原来的基础上注释掉mBluetoothGatt.close()。
			
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

			// 开启一个processDialog直到获取到需要的服务才关闭它
			pdialog = new ProgressDialog(ActiDevices.this);
			pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
			pdialog.setCancelable(true);// 设置是否可以通过点击Back键取消
			pdialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
			pdialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			pdialog.setMessage("连接中......");
			pdialog.show();

		}
	}

	// bindService的时候成功或不成功的处理函数 // Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {

			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Toast.makeText(ActiDevices.this, "服务未成功初始化", Toast.LENGTH_SHORT).show();
				finish();
			}
			Log.i(TAG, "服务已连接");
			Log.i(TAG, "开始连接设备，设备地址为： " + deviceAddress);
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(deviceAddress);//9.12和武健商量之后去掉的代码
			//mBluetoothLeService.connect(deviceAddress,0,5);//9.11 add

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			pdialog.cancel();
			Log.i(TAG, "服务未连接");
			Toast.makeText(ActiDevices.this, "服务未成功连接", Toast.LENGTH_SHORT).show();
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
}
