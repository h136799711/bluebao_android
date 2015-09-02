package com.itboye.bluebao.actiandfrag;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.UtilBluetooth;

public class ActiDiscoveredBtDevicesList extends Activity{
	
	private static final String TAG = "-----ActiDiscoveredBtDevicesList";
	LinearLayout ll_discoveredbtdeviceslist ;
	
	private List<BluetoothDevice> btList = new ArrayList<BluetoothDevice>();//存放搜索到的bt设备
	
	@Override
	protected void onStart() {
		
		btList.clear(); // 清空列表

		// 设置receiveResultsReceiver的Filter并注册
		IntentFilter receiveResultsFilter = new IntentFilter();
		receiveResultsFilter.addAction(UtilBluetooth.ACTION_FOUND_DEVICE);
		receiveResultsFilter.addAction(UtilBluetooth.ACTION_NOT_FOUND_DEVICE);
		registerReceiver(receiver, receiveResultsFilter);
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_discoveredbtdeviceslist);
		
		ll_discoveredbtdeviceslist = (LinearLayout) findViewById(R.id.ll_discoveredbtdeviceslist);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	
	// receiver--------------------------------------------------------------------------------
	/**
	 * receivResultsReceiver 接收后台服务发来的结果Receiver
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (UtilBluetooth.ACTION_FOUND_DEVICE.equals(action)) {
				Log.i(TAG, "find a bt device."); // 搜索到外围蓝牙设备

				Toast.makeText(ActiDiscoveredBtDevicesList.this, "搜索到蓝牙设备了。", Toast.LENGTH_SHORT).show();
			    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    
			    btList.add(device);// 把搜索到的bt设备添加到btList中
			    
				
				Button btDevice = new Button(ActiDiscoveredBtDevicesList.this);
				btDevice.setText(device.getName());
				btDevice.setTextSize(20);
				btDevice.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//点击 配对
						
						//TODO
						//--先在ActiMain中测试，之后改进---------------------------------------------------------------------------------------------
						Intent toPair = new Intent(UtilBluetooth.ACTION_SELECTED_DEVICE);
						Toast.makeText(ActiDiscoveredBtDevicesList.this, v.getId()+"被点击了。" + btList.get(v.getId()+1).getName() +btList.get(v.getId()+1).getAddress(), Toast.LENGTH_SHORT).show();
						toPair.putExtra(BluetoothDevice.EXTRA_DEVICE, btList.get( v.getId() + 1));
						sendBroadcast(toPair);
						
						
						//测试可用
						//BluetoothDevice device = adapter.getRemoteDevice (btList.get( v.getId() + 1).getAddress() ) ;
						//Toast.makeText(ActiMain.this, "搜索到的设备名称："+ device.getName() , Toast.LENGTH_SHORT).show();
						
						
		
						
					}
				});
				ll_discoveredbtdeviceslist.addView(btDevice);
				
				
			} else if (UtilBluetooth.ACTION_NOT_FOUND_DEVICE.equals(action)) {
				Log.i(TAG, "do not find any bt device."); // 未搜索到外围蓝牙设备
				Toast.makeText(ActiDiscoveredBtDevicesList.this, "未搜索到蓝牙设备。", Toast.LENGTH_SHORT).show();
			}
		}
	};


}
