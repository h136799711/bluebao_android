package com.itboye.bluebao.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;

public class ActiBleConnAndGetdata extends Activity {

	private final static String TAG = "-----ActiBleConnAndGetdata";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private Gson gson = new Gson();

	private TextView tv_deviceName;
	private TextView tv_deviceAddress;
	private TextView tv_deviceConnState;// 连接状态
	private TextView tv_heartRate;// 测试读取心率数据
	private String deviceName;
	private String deviceAddress;

	private ExpandableListView elv_gattServicesList; // 展示ble设备services的listview
	private BluetoothLeService mBluetoothLeService;
	// ArrayList<BluetoothGattCharacteristic>== Service是Characteristic的集合
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>就是一个ble设备能提供的所有Service的集合
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	// bindService的时候成功或不成功的处理函数 // Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Toast.makeText(ActiBleConnAndGetdata.this, "服务未成功初始化", Toast.LENGTH_SHORT).show();
				finish();
			}
			Toast.makeText(ActiBleConnAndGetdata.this, "服务已连接", Toast.LENGTH_SHORT).show();
			Toast.makeText(ActiBleConnAndGetdata.this, "开始连接设备，设备地址为： " + deviceAddress, Toast.LENGTH_SHORT).show();

			// Automatically connects to the device upon successful start-up initialization.
			mBluetoothLeService.connect(deviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			Toast.makeText(ActiBleConnAndGetdata.this, "服务未成功连接", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Conn and GD-----onCreate");
		setContentView(R.layout.layout_acti_ble_connandgetdate);

		tv_deviceName = (TextView) findViewById(R.id.acti_ble_connandgetdata_tv_devicename);
		tv_deviceAddress = (TextView) findViewById(R.id.acti_ble_connandgetdata_tv_deviceaddress);
		tv_deviceConnState = (TextView) findViewById(R.id.acti_ble_connandgetdata_tv_devicestate);
		tv_heartRate = (TextView) findViewById(R.id.acti_ble_connandgetdata_tv_heartrate);
		elv_gattServicesList = (ExpandableListView) findViewById(R.id.acti_ble_connandgetdata_exlv_services);
		elv_gattServicesList.setOnChildClickListener(servicesListClickListner);// ExpandableListView-setOnChildClickListener

		Intent intent = getIntent();
		deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		tv_deviceName.setText(deviceName);
		tv_deviceAddress.setText(deviceAddress);

		// 绑定服务。mServiceConnection处理连接成功与否。成功：直接根据deviceAddress连接；
		// 失败：mBluetoothLeService
		Intent gattServiceIntent = new Intent(ActiBleConnAndGetdata.this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Conn and GD-----onResume");

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(deviceAddress);
			Log.d(TAG, "Connect device result is : " + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "Conn and GD-----onPause");
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Conn and GD-----onDestory");
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	/*
	 * ==点击一个characteristic时，显示其的Descriptor===
	 *  If a given GATT characteristic is selected, check for  supported features. This sample demonstrates 'Read' and 'Notify' features. 
	 *  See http:d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete list of supported characteristic features.
	 */
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			Log.i(TAG, "Conn and GD-----onChildClick");
			
			if (mGattCharacteristics != null) {
				final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
				Log.i("点击了 选项", groupPosition + "---" + childPosition);
				final int charaProp = characteristic.getProperties();
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
					// If there is an active notification on a characteristic,
					// clear it first so it doesn't update the data field on the user interface.
					if (mNotifyCharacteristic != null) {
						mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
						mNotifyCharacteristic = null;
					}
					mBluetoothLeService.readCharacteristic(characteristic);
				}
				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					mNotifyCharacteristic = characteristic;
					mBluetoothLeService.setCharacteristicNotification(characteristic, true);
				}
				return true;
			}
			return false;
		}
	};

	// ==处理BluetoothLeService发来的广播==========================================================
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/*
	 * Handles various events fired by the Service. ACTION_GATT_CONNECTED:
	 * connected to a GATT server. ACTION_GATT_DISCONNECTED: disconnected from a
	 * GATT server. ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	 * ACTION_DATA_AVAILABLE: received data from the device. This can be a
	 * result of read or notification operations.
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Conn and GD-----onReceiveBroadcast");

			final String action = intent.getAction();

			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(mConnected);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(mConnected);
				Toast.makeText(ActiBleConnAndGetdata.this, "设备已断开", Toast.LENGTH_SHORT).show();
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
			}else if ( BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action) ) {
				String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				displayData(data);
			}else{
				Toast.makeText(ActiBleConnAndGetdata.this, "获取到不明广播" , Toast.LENGTH_SHORT).show();
			}
		}
	};

	// ==以下是一些处理函数================================================
	private void updateConnectionState(final boolean state) {
		Log.i(TAG, "Conn and GD-----updateConnectionState");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "updateConnectionState : " + state);
				if (state ) {
					tv_deviceConnState.setText("已连接");
				}else {
					tv_deviceConnState.setText("未连接");
				}
			}
		});
	}

	private void clearUI() {
		Log.i(TAG, "Conn and GD-----clearUI");
		elv_gattServicesList.setAdapter((SimpleExpandableListAdapter) null);
	}

	/*
	 * Demonstrates how to iterate through the supported GATT
	 * Services/Characteristics. In this sample, we populate the data structure
	 * that is bound to the ExpandableListView on the UI.
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		Log.i(TAG, "Conn and GD-----displayGattServices");
		if (gattServices == null)
			return;

		String uuid = null;
		String unknownServiceString = getResources().getString(R.string.unknown_service);
		String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}

		// gattServiceAdapter中用到两个数据：gattServiceData和gattCharacteristicData
		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData,
				android.R.layout.simple_expandable_list_item_2, new String[] { LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
						android.R.id.text2 }, gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[] {
						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1, android.R.id.text2 });
		
		elv_gattServicesList.setAdapter(gattServiceAdapter);
	}

	private void displayData(String data) {
		Log.i(TAG, "Conn and GD-----displayData");
		if (data != null) {
			Log.i(TAG, "读取到的 DATA 是：" + data.toString());
			
			DataToShowBean dataToShowBean = gson.fromJson(data, DataToShowBean.class);
			if (  dataToShowBean != null ) {
			}
			
			
			Toast.makeText(ActiBleConnAndGetdata.this, "读取到的数据是：" + data, Toast.LENGTH_SHORT).show();
			tv_heartRate.setText(data);
		}
	}
}
