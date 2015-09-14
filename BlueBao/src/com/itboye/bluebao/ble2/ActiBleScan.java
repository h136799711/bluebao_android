package com.itboye.bluebao.ble2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itboye.bluebao.R;
import com.umeng.analytics.MobclickAgent;

public class ActiBleScan extends Activity implements OnClickListener{
	
	private static final String TAG = "----ActiBleScan-NEW";
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private static final long SCAN_PERIOD = 8000;
	private boolean mScanning;
	private Handler mHandler;
	
	private Button btn_scan;//"重新搜索"按钮
	private TextView tv_sw;//"正在搜索"
	private ImageView iv_simg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_new_scandevices);
		
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableBtIntent);
		}
		
		btn_scan = (Button) findViewById(R.id.acti_new_scandevices_btn_scanagain);
		tv_sw = (TextView) findViewById(R.id.acti_new_tv_searchword);
		iv_simg = (ImageView) findViewById(R.id.acti_new_simg);
		btn_scan.setVisibility(View.INVISIBLE);//搜索过程中不可见
		btn_scan.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
		case R.id.acti_new_scandevices_btn_scanagain:
			scanLeDevice(true);//重新搜索
			btn_scan.setVisibility(View.INVISIBLE);
			tv_sw.setVisibility(View.VISIBLE);
			//iv_simg.startAnimation(AnimationUtils.loadAnimation(ActiBleScan.this, R.anim.scandevice_animation));//9.11 add
			break;
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		
		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.enable()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(enableBtIntent);
			}
		}else{
			if( bluetoothManager != null ){
				mBluetoothAdapter = bluetoothManager.getAdapter();
			}else{
				bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
			}
		}
		
		if( mScanning == false ){
			Log.i(TAG, "mScanning == false,开始搜索");
			scanLeDevice(true);//搜索ble设备
			//iv_simg.startAnimation(AnimationUtils.loadAnimation(ActiBleScan.this, R.anim.scandevice_animation));//9.11 add
		}
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		scanLeDevice(false);//以后如果想把搜索到的设备都加进去，则在这里添加逻辑
		super.onPause();
	}
	
	//搜索设备逻辑
	private void scanLeDevice(final boolean enable) {
		mHandler = new Handler();//9.11 log中出现空指针异常，推测是handler为空，所以把初始化handler的逻辑放到这里,但发现不是handler为空
		if( mBluetoothAdapter==null ){//可能是adapter为空
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}
		
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					btn_scan.setVisibility(View.VISIBLE);//重新搜索按钮可见
					tv_sw.setVisibility(View.INVISIBLE);
					//iv_simg.clearAnimation();//9.11 add
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

	// Device scan callback. 发现蓝牙设备时的处理逻辑
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			//9.10 目前先这么做
			if( device.getName().startsWith("UART-BLE")){//以UART-BLE开头的ble设备
				Log.i(TAG, "搜索到设备了");
				Log.i(TAG, "搜索到设备了,设备的address is： " + device.getAddress());
				
				Intent intent2 = new Intent(ActiBleScan.this,ActiDevices.class);
				startActivity(intent2);
				
				try {
					Thread.sleep(1000);
					Intent intent = new Intent("FOUND_A_DEVICE");
					intent.putExtra("device", device);
					sendBroadcast(intent);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				ActiBleScan.this.finish();
			}
		}
	};
}
