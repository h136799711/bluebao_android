package com.itboye.bluebao.ble;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.actiandfrag.ActiMainTest3;

public class ActiBleScan extends Activity implements OnClickListener {

	private final static String TAG = "-----ActiBleNew";
	private final Context CONTEXT = ActiBleScan.this;

	// start 8.26 added
	private BluetoothLeService mBluetoothLeService;
	private String deviceAddress; // 要连接的ble的mac address
	// end 8.26 added

	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothManager bluetoothManager;
	/** 搜索BLE终端 */
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;
	private static final int REQUEST_ENABLE_BT = 1;

	private Button btn_scan;
	private ListView lv_bledevices;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_ble_scan);
		mHandler = new Handler();

		/*
		 * if (!getPackageManager().hasSystemFeature(PackageManager.
		 * FEATURE_BLUETOOTH_LE)) { Toast.makeText(this, "您的手机不支持BLE",
		 * Toast.LENGTH_SHORT).show(); finish(); }
		 */

		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableBtIntent);
		}

		btn_scan = (Button) findViewById(R.id.acti_ble_scan_btn_scan);
		lv_bledevices = (ListView) findViewById(R.id.acti_ble_scan_lv_foundeddevices);
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		lv_bledevices.setAdapter(mLeDeviceListAdapter);
		lv_bledevices.setOnItemClickListener(new MyOnItemClickListener());
		btn_scan.setOnClickListener(this);

	}

	// 监听lv_bledevices的item点击事件
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BluetoothDevice ble = mLeDeviceListAdapter.getDevice(position);
			if (ble == null) {
				Toast.makeText(CONTEXT, "设备已经关闭", Toast.LENGTH_SHORT).show();
				return;
			}
			deviceAddress = ble.getAddress();// 赋给常量deviceAddress

			// start 8.26 added

			// 绑定服务。mServiceConnection处理连接成功与否。成功：直接根据deviceAddress连接；
			// 失败：mBluetoothLeService
			Intent gattServiceIntent = new Intent(ActiBleScan.this, BluetoothLeService.class);
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

			Intent intent = new Intent(CONTEXT, ActiMainTest3.class);
			// intent.putExtra(ActiBleConnAndGetdata.EXTRAS_DEVICE_NAME,
			// ble.getName());
			// intent.putExtra(ActiBleConnAndGetdata.EXTRAS_DEVICE_ADDRESS,
			// ble.getAddress());
			if (mScanning) {// 若正在搜索则停止搜索
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
			startActivity(intent);

			/*
			 * Intent intent = new Intent(CONTEXT, ActiBleConnAndGetdata.class);
			 * intent.putExtra(ActiBleConnAndGetdata.EXTRAS_DEVICE_NAME,
			 * ble.getName());
			 * intent.putExtra(ActiBleConnAndGetdata.EXTRAS_DEVICE_ADDRESS,
			 * ble.getAddress()); if ( mScanning ){//若正在搜索则停止搜索
			 * mBluetoothAdapter.stopLeScan(mLeScanCallback); }
			 * startActivity(intent);
			 */
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled, fire an intent to display a dialog asking the user
		// to grant permission to enable it.
		/*
		 * if (!mBluetoothAdapter.isEnabled()) { if
		 * (!mBluetoothAdapter.isEnabled()) { Intent enableBtIntent = new
		 * Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 * startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); } }
		 */
		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.enable()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(enableBtIntent);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		lv_bledevices.setAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);

		// start 8.26 added
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(deviceAddress);
			Log.d(TAG, "Connect device result is : " + result);
		}
		// end 8.26 added
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onDestroy() {
		mBluetoothLeService = null;

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.acti_ble_scan_btn_scan: // 搜索ble设备
			if (mScanning == true) {
				Toast.makeText(CONTEXT, "scanning......", Toast.LENGTH_SHORT).show();
			} else {
				scanLeDevice(true);
			}
			break;
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = ActiBleScan.this.getLayoutInflater();
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
				viewHolder.deviceName.setText(deviceName);
			} else {
				viewHolder.deviceName.setText(R.string.unknown_device);
			}
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	// 8.26 added below
	// bindService的时候成功或不成功的处理函数 // Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {

			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Toast.makeText(ActiBleScan.this, "服务未成功初始化", Toast.LENGTH_SHORT).show();
				finish();
			}
			Toast.makeText(ActiBleScan.this, "服务已连接", Toast.LENGTH_SHORT).show();
			Toast.makeText(ActiBleScan.this, "开始连接设备，设备地址为： " + deviceAddress, Toast.LENGTH_SHORT).show();

			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(deviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			Toast.makeText(ActiBleScan.this, "服务未成功连接", Toast.LENGTH_SHORT).show();
		}
	};

}
