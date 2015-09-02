package com.itboye.bluebao.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();
	private Gson gson = new Gson();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	
	public static final UUID SERVIE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	public static final UUID UUID_I_CARE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
	
	
	//start 8.26 added
	public static final UUID UUID_SERVICE_WANT = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	public static final UUID UUID_CHARACTERISTIC_WANT = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	public final static String ACTION_GATT_SERVICE_NOT_FOUND = "com.example.bluetooth.le.ACTION_GATT_SERVICE_NOT_FOUND";
	public final static String ACTION_GATT_CHARA_NOT_FOUND = "com.example.bluetooth.le.ACTION_GATT_CHARA_NOT_FOUND";
	//end 8.26 added
	
	
	// Implements callback methods for GATT events that the app cares about.
	//For example, connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			
			String intentAction;
			
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(TAG, "onConnectionStateChange----绑定状态：" + newState);
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "已连接到 GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "试图发现服务，结果是：" + mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(TAG, "onConnectionStateChange----绑定状态：" + newState);
				connect(mBluetoothDeviceAddress);//8.29 added 断开时重新连接
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onServicesDiscovered----发现服务了，状态：" + status);
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				
				//start 8.26 added
				BluetoothGattService mBlutGattService = gatt.getService(UUID_SERVICE_WANT);
				if ( mBlutGattService!=null ) {
					BluetoothGattCharacteristic mBlutGattCharacteristic = mBlutGattService.getCharacteristic(UUID_CHARACTERISTIC_WANT);
					if( mBlutGattCharacteristic!=null ){
						readCharacteristic(mBlutGattCharacteristic);
						setCharacteristicNotification(mBlutGattCharacteristic, true);
					}else{//需要的characteristic没有找到
						broadcastUpdate(ACTION_GATT_CHARA_NOT_FOUND);
					}
					
				}else{// 需要的service没有找到
					broadcastUpdate(ACTION_GATT_SERVICE_NOT_FOUND);
				}
				//end 8.26 added
				
				
			} else {
				Log.i(TAG, "onServicesDiscovered----没有发现服务，状态：" + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "onCharacteristicRead----读取到characteristic，是否成功读取完成：" + status);
				//Log.i(TAG, "onCharacteristicRead----读取到characteristic，值为：" + characteristic.getValue().toString());
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged----characteristic值改变了。");
			//Log.i(TAG, "onCharacteristicChanged----characteristic值改变了。新值为：" + characteristic.getValue().toString());
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	//要发送到前台页面的数据在这里解析
	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		
		final Intent intent = new Intent(action);
		
		 if (UUID_CHARACTERISTIC_WANT.equals(characteristic.getUuid())) {
			
			byte[] data = characteristic.getValue();

			// start 8.26 added 在此解析数据，之后发送到前台
			DataToShowBean dataToShowBean = parseData(data);
			String dataToShowBeanStr = gson.toJson(dataToShowBean);
			// end 8.26 added
			
			intent.putExtra(EXTRA_DATA, dataToShowBeanStr);
			sendBroadcast(intent);
			Log.i(TAG, "数据已通过广播发送到前台");

		}
	}

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address
	 *            The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. 
	 * 		The connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		
		if (mBluetoothAdapter == null || address == null) {
			Log.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
			Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		
		
		if (device == null) {
			Log.i(TAG, "Device not found.  Unable to connect.");
			return false;
		}

		mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
		Log.i(TAG, "8.30 added. mBluetoothGatt.getDevice().getUuids() is: " + mBluetoothGatt.getDevice().getUuids() );
		Log.i(TAG, "8.30 added. mBluetoothGatt.getDevice().getAddress() is: " + mBluetoothGatt.getDevice().getAddress() );
		
		Log.i(TAG, "正在连接......");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		Log.i(TAG, "device.connectGatt(this, true, mGattCallback)----绑定状态：" + device.getBondState());
		
	/*	if ( device.getBondState()==10 ) { //如果没有绑定成功就一直绑定
			Log.i(TAG, "device.connectGatt(this, true, mGattCallback)没有连接上----绑定状态：" + device.getBondState());
			mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
			Log.i(TAG, "device.connectGatt(this, true, mGattCallback)重新连接----绑定状态：" + device.getBondState());
		}*/
		
/*		if( device.getBondState()==10 ){
			Log.i(TAG, "device.connectGatt(this, true, mGattCallback)没有连接上----绑定状态：" + device.getBondState());
			mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
			Log.i(TAG, "device.connectGatt(this, true, mGattCallback)重新连接----绑定状态：" + device.getBondState());
		}*/
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. 
	 * The disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. 
	 * The read result is reported asynchronously through the
	 * {@code BluetoothGattCallback#
	 * onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)} callback.
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}


	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	
		//start 8.26 added  这一段代码非常重要
		if (UUID_I_CARE.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
					.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
		//stop 8.26 added
		
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null){
			return null;
		}

		return mBluetoothGatt.getServices();
	}

	//==解析工作======
	
	public DataToShowBean parseData(byte[] data){
		DataToShowBean dataToShow = new DataToShowBean();
		
		//1 16进制数据->string
		String strTemp = DataUtil.getStringByBytes(data);
		Log.i(TAG, "-----parseData 0:" + strTemp);

		// 2 时间解析
		String timeInData = strTemp.substring(6, 10);//subString() 包括开始，不包括结束
		Log.i(TAG, "-----parseData--原始时间----" + timeInData);
		String timeToShow = getTime(timeInData);
		Log.i(TAG, "-----parseData--解析后时间----" + timeToShow);
		
		// 3 时间速度
		String speedInData = strTemp.substring(10, 14);
		Log.i(TAG, "-----parseData--原始速度----" + speedInData);
		String speedToShow = getSpeed(speedInData);
		Log.i(TAG, "-----parseData--解析后速度----" + speedToShow);
		
		// 3 距离值
		String milesInData = strTemp.substring(14, 20);
		Log.i(TAG, "-----parseData--原始距离值----" + milesInData);
		String milesToShow = getMiles(milesInData);
		Log.i(TAG, "-----parseData--解析后距离值----" + milesToShow);
		
		// 4 卡路里
		String carsInData = strTemp.substring(20, 24);
		Log.i(TAG, "-----parseData--原始卡路里----" + carsInData);
		String carsToShow = getCars(carsInData);
		Log.i(TAG, "-----parseData--解析后卡路里----" + carsToShow);
		
		// 5 总距离
		String totalMilesInData = strTemp.substring(24, 28);
		Log.i(TAG, "-----parseData--原始总距离----" + totalMilesInData);
		String totalMilesToShow = getTotalMiles(totalMilesInData);
		Log.i(TAG, "-----parseData--解析后总距离----" + totalMilesToShow);
		
		// 5 心率
		String xinlvInData = strTemp.substring(28, 30);
		Log.i(TAG, "-----parseData--原始心率----" + xinlvInData);
		String xinlvToShow = getXinlv(xinlvInData);
		Log.i(TAG, "-----parseData--解析后心率----" + xinlvToShow);
		
		
		dataToShow.setMiles(milesToShow);
		dataToShow.setCars(carsToShow);
		dataToShow.setXinlv(xinlvToShow);
		dataToShow.setSpeed(speedToShow);
		dataToShow.setTime(timeToShow);
		
		return dataToShow;
	}
	
	
	public String getTime(String str){
		String timeToShow;
		//解析前两位：分钟
		String minute = str.substring(0, 2);
		int minuteTemp = DataUtil.hexStringX2bytesToInt(minute);
		//解析后两位：秒
		String second = str.substring(2,4);
		int secondTemp = DataUtil.hexStringX2bytesToInt(second);
		long mills = ( minuteTemp*60 + secondTemp ) * 1000;
		timeToShow = DataUtil.getHHMMSS(mills);
		return timeToShow;
		
	}
	
	public String getSpeed(String str){
		int speedTemp = DataUtil.hexStringX2bytesToInt(str);
		return speedTemp/10+"km/h";
	}

	public String getMiles(String str){
		int beforePoint = DataUtil.hexStringX2bytesToInt(str.substring(2, 4));
		int afterPoint = DataUtil.hexStringX2bytesToInt(str.substring(4, 6));
		String strtemp = beforePoint+"."+String.valueOf(afterPoint).substring(0, 2);
		return strtemp+"km";
	}
	
	public String getCars(String str){
		int carsTemp = DataUtil.hexStringX2bytesToInt(str);
		return carsTemp/10 +"卡";
	}
	
	public String getTotalMiles(String str){
		int beforePoint = DataUtil.hexStringX2bytesToInt(str.substring(0, 2));
		int afterPoint = DataUtil.hexStringX2bytesToInt(str.substring(2, 4));
		return beforePoint+"."+afterPoint+"km";
	}
	
	public String getXinlv(String str){
		int xinlvTemp = DataUtil.hexStringX2bytesToInt(str);
		return xinlvTemp+"";
	}


}
