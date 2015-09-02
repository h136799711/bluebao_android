package com.itboye.bluebao.actiandfrag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.UtilBluetooth;

/**
 * 搜索蓝牙设备activity
 * 
 * @author Administrator
 *
 */
public class ActiFindBTDevice extends Activity implements OnClickListener {
	
	private static final String TAG = "-----ActiFindBTDevice";
	
	private BluetoothAdapter adapter;
	private List<BluetoothDevice> btList = new ArrayList<BluetoothDevice>();//存放搜索到的bt设备
	private LinearLayout ll_forShowDiscoveredBtDevices; // linearlayout 展示搜索到的蓝牙设备，动态在其中添加TextView

	private EditText et_locaobtinfo;
	private EditText et_alreadyconnectedbtinfo;
	private EditText et_remotebtinfo;
	
	private Button btn_getlocalbtinfo;
	private Button btn_getalreadyconnetctedbtinfo;
	private Button btn_getremotebinfo;

	//====================
	
	@Override
	protected void onStart() {
		
		btList.clear(); // 清空列表

		// 设置receiveResultsReceiver的Filter并注册
		IntentFilter receiveResultsFilter = new IntentFilter();
		receiveResultsFilter.addAction(UtilBluetooth.ACTION_FOUND_DEVICE);
		receiveResultsFilter.addAction(UtilBluetooth.ACTION_NOT_FOUND_DEVICE);
		registerReceiver(receiveResultsReceiver, receiveResultsFilter);

		startService(new Intent("com.jys.bluetoothproj.service.BtService")); // 开启服务

		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_findbtdevice);
		
		adapter = BluetoothAdapter.getDefaultAdapter(); // 获得本地蓝牙适配器的唯一的方式:getDefaultAdapter()

		ll_forShowDiscoveredBtDevices = (LinearLayout) findViewById(R.id.actifindbtdevice_ll_forshowdiscoveredBtDevices);

		btn_getlocalbtinfo = (Button) findViewById(R.id.actifindbtdevice_btn_getlocalbtinfo);
		btn_getalreadyconnetctedbtinfo = (Button) findViewById(R.id.actifindbtdevice_btn_getalreadyconnetctedbtinfo);
		btn_getremotebinfo = (Button) findViewById(R.id.actifindbtdevice_btn_getremotebinfo);
		btn_getlocalbtinfo.setOnClickListener(this);
		btn_getalreadyconnetctedbtinfo.setOnClickListener(this);
		btn_getremotebinfo.setOnClickListener(this);
		
		et_locaobtinfo = (EditText) findViewById(R.id.actifindbtdevice_et_localbtinfo);
		et_alreadyconnectedbtinfo = (EditText) findViewById(R.id.actifindbtdevice_et_alreadyconnectedbtinfo);
		et_remotebtinfo = (EditText) findViewById(R.id.actifindbtdevice_et_remotebtinfo);
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.actifindbtdevice_btn_getlocalbtinfo:// 获取本地蓝牙信息
			
			et_locaobtinfo.setText("本机蓝牙设备信息如下：" + "\r\n名称：" + adapter.getName() + "\r\n地址：" + adapter.getAddress() + "\r\n状态："
					+ adapter.getState() + "\r\n搜索模式：" + adapter.getScanMode());
			break;

		case R.id.actifindbtdevice_btn_getalreadyconnetctedbtinfo:// 获取已配对蓝牙信息
			
			String info = "";
			Set<BluetoothDevice> device = adapter.getBondedDevices(); // 通过getBondedDevices方法来获取已经与本设备配对的设备

			if (device.size() > 0) {
				for (Iterator iterator = device.iterator(); iterator.hasNext();) {
					BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
					info += "名称：" + bluetoothDevice.getName() + "\r\n地址：" + bluetoothDevice.getAddress() + "\r\n绑定状态："
							+ bluetoothDevice.getBondState() + "\r\n类型：" + bluetoothDevice.getBluetoothClass() + "\r\nUUIDs LENGTH："
							+ bluetoothDevice.getUuids() + "\r\n\r\n";
				}
			}
			et_alreadyconnectedbtinfo.setText(info);
			break;

		case R.id.actifindbtdevice_btn_getremotebinfo:// 获取远端蓝牙信息
			//Intent startSearchDialog = new Intent(ActiFindBTDevice.this,ActiDiscoveredBtDevicesList.class);
			//startActivityForResult(startSearchDialog, RC_START_SEARCHING);
			
			Intent startSearchIntent = new Intent(UtilBluetooth.ACTION_START_DISCOVERY);
			sendBroadcast(startSearchIntent);
			break;

		}
	}

	@Override
	protected void onDestroy() {
		
		unregisterReceiver(receiveResultsReceiver);

		// 关闭后台Service
		Intent startService = new Intent(UtilBluetooth.ACTION_STOP_SERVICE);
		sendBroadcast(startService);

		super.onDestroy();
	}

	
	// --receiveResultsReceiver---------------------------------------------------------------------------------------------------------------
	
	/**
	 * receivResultsReceiver 接收后台服务发来的结果Receiver
	 */
	private BroadcastReceiver receiveResultsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (UtilBluetooth.ACTION_FOUND_DEVICE.equals(action)) {
				Log.i(TAG, "find a bt device."); // 搜索到外围蓝牙设备
				Toast.makeText(ActiFindBTDevice.this, "搜索到蓝牙设备了。", Toast.LENGTH_SHORT).show();
				
				//short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI); // 信号强度
				
			    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    
			    btList.add(device);// 把搜索到的bt设备添加到btList中
			    
				et_remotebtinfo.setText("获取到的外围蓝牙设备的信息：" + "\r\n名称：" + device.getName() + "\r\n地址：" + device.getAddress()
						+ "\r\n绑定状态（匹配状态）：" + device.getBondState() + "\r\n UUIDs：" + device.getUuids() );

				ll_forShowDiscoveredBtDevices = (LinearLayout) findViewById(R.id.actifindbtdevice_ll_forshowdiscoveredBtDevices);
				Button btDevice = new Button(ActiFindBTDevice.this);
				btDevice.setText(device.getName());
				btDevice.setTextSize(20);
				btDevice.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//点击 配对
						
						//TODO
						//--先在ActiMain中测试，之后改进---------------------------------------------------------------------------------------------
						Intent toPair = new Intent(UtilBluetooth.ACTION_SELECTED_DEVICE);
						Toast.makeText(ActiFindBTDevice.this, v.getId()+"被点击了。" + btList.get(v.getId()+1).getName() +btList.get(v.getId()+1).getAddress(), Toast.LENGTH_SHORT).show();
						toPair.putExtra(BluetoothDevice.EXTRA_DEVICE, btList.get( v.getId() + 1));
						sendBroadcast(toPair);
						
						
						//测试可用
						//BluetoothDevice device = adapter.getRemoteDevice (btList.get( v.getId() + 1).getAddress() ) ;
						//Toast.makeText(ActiMain.this, "搜索到的设备名称："+ device.getName() , Toast.LENGTH_SHORT).show();
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
					}
				});
				ll_forShowDiscoveredBtDevices.addView(btDevice);
				
				
			} else if (UtilBluetooth.ACTION_NOT_FOUND_DEVICE.equals(action)) {
				Log.i(TAG, "do not find any bt device."); // 未搜索到外围蓝牙设备
				Toast.makeText(ActiFindBTDevice.this, "未搜索到蓝牙设备。", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	
	
	
	
	
	
}