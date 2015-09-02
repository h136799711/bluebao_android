package com.itboye.bluebao.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.itboye.bluebao.util.UtilBluetooth;
import com.itboye.bluebao.util.UtilBluetooth2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BtService extends Service {

	private final String TAG = "-----BtService";
	private final String TAG1 = TAG + "-discoveryReceiver";
	private final String TAG2 = TAG + "-controlReceiver";
	
	//TODO
	//--现在测试，之后改进---------------------------------------------------------------------------------------------
	//这条是蓝牙串口通用的UUID，不要更改
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothSocket btSocket = null; 
	
	
	//--现在ActiMain中测试，之后改进---------------------------------------------------------------------------------------------
	
	
	
	
	
	
	
	
	
	
	// 蓝牙适配器
	private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

	// 搜索到的外围蓝牙设备的集合
	private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

	//==================================================================
	/**
	 * 搜索外围蓝牙设备的广播接收器
	 */
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.i(TAG1, "start to discover bluetooth device."); // 开始搜索

			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.i(TAG1, "discover bluetooth device."); //发现外围蓝牙设备 
				
				//加入搜索到的外围蓝牙设备集合中；把搜索到的设备通过广播发送到ActiFindBTDevice中。
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(bluetoothDevice); //加入 搜索到的外围蓝牙设备集合中

				Intent toFrontWithDisconeredBTDevice = new Intent(UtilBluetooth.ACTION_FOUND_DEVICE);
				toFrontWithDisconeredBTDevice.putExtra(BluetoothDevice.EXTRA_DEVICE, bluetoothDevice);
				sendBroadcast(toFrontWithDisconeredBTDevice);

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.i(TAG1, "finish discover bluetooth device."); //搜索结束 
				
				if (discoveredDevices.isEmpty()) { // 若未找到设备，则发送 未发现设备广播
					Intent foundIntent = new Intent(UtilBluetooth.ACTION_NOT_FOUND_DEVICE);
					sendBroadcast(foundIntent);
				}
			}
		}
	};

	//==================================================================
	/**
      * 控制中枢。配对、连接、发送数据等 中央控制 广播接收器
	 */
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if (UtilBluetooth.ACTION_START_DISCOVERY.equals(action)) {
				Log.i(TAG2, "start to discover bluetooth device."); //开始搜索 
				
				discoveredDevices.clear(); //清空存放设备的集合
				
				if ( btAdapter!=null ) {
					if ( !btAdapter.enable() ) { // 如果本地蓝牙没有开启则开启之 
						Intent startBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						  startActivity(startBt);
					}
				}
				
				//开启蓝牙发现功能，时间设置为300秒
				Intent setBtDiscoveryDuration = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				setBtDiscoveryDuration.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				//包含FLAG_ACTIVITY_NEW_TASK的Intent启动Activity的Task如果正在运行, 则不会为该Activity创建新的Task,
                //而是将原有的Task返回到前台显示.
				setBtDiscoveryDuration.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(setBtDiscoveryDuration);
				
				//开始搜索 ，每找到一个蓝牙设备就发送一个BluetoothDevice.ACTION_FOUND广播
				btAdapter.startDiscovery(); 
				
			} else if ( UtilBluetooth.ACTION_STOP_SERVICE.equals(action) ) {
				Log.i(TAG2, "stop service."); //停止服务
				
				stopSelf(); // 停止自身
				
			} else if ( UtilBluetooth.ACTION_SELECTED_DEVICE.equals(action) ) {
				Log.i(TAG2, "to pair."); //开始配对
				
				
				
				
				//--先在ActiMain中测试，之后改进---------------------------------------------------------------------------------------------
				
				//首先停止搜索
				btAdapter.cancelDiscovery();
				
				BluetoothDevice remoteBtDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				BluetoothDevice device = btAdapter.getRemoteDevice(remoteBtDevice.getAddress());
				
				
				int connectState = device.getBondState(); // 连接状态 
				switch (connectState) {    
		                case BluetoothDevice.BOND_NONE: // 未配对
		                    try {    // 配对  
		                        //Method createBondMethod = BluetoothDevice.class.getMethod("createBond");    
		                        //createBondMethod.invoke(device);    
		                        
		                        //8.18==测试自动配对=====
		                        //Method createBondMethod2 = BluetoothDevice.class.getMethod("cancelBondProcess");

		                		//Boolean returnValue = (Boolean) createBondMethod2.invoke(device);
		                       // Log.i(TAG, "自动配对  " + returnValue);
		                        
		                        //===第二次测试
		                    	
		                    	//boolean b1= UtilBluetooth2.cancelPairingUserInput(BluetoothDevice.class, device);
		                  /*  	Method createBondMethod = device.getClass().getMethod("cancelPairingUserInput");
		                		Boolean b1 = (Boolean) createBondMethod.invoke(device);*/
		                    	
		                    	
		                    	//boolean b2= UtilBluetooth2.cancelBondProcess(BluetoothDevice.class, device);
		                    /*	Method createBondMethod1 = device.getClass().getMethod("cancelBondProcess");
		                		Boolean b2 = (Boolean) createBondMethod1.invoke(device);
		                		
		                    	Log.i(TAG, "自动配对  cancelPairingUserInput" + b1);
		                    	Log.i(TAG, "自动配对  cancelBondProcess" + b2);*/
		                    	
		                    	//UtilBluetooth2.printAllInform(device.getClass());
		                    	
		                    	
		                    	//Method createBondMethod2 = BluetoothDevice.class.getMethod("createBond");    
		                    	//createBondMethod2.invoke(device);  
		                    	
		                    	//Boolean b1 = autoBond(device.getClass(), device, "123456");
		                    	//Boolean b2 = createBond(device.getClass(), device);
		                    	
		                    	boolean b = UtilBluetooth2.setPin(device.getClass(), device, "0000");
		                    	Log.i(TAG, "自动配对  setPin  " + b );
		                    	
		                    	Method createBondMethod = BluetoothDevice.class.getMethod("createBond");    
		                        createBondMethod.invoke(device);
		                        
		                        boolean b1 = UtilBluetooth2.cancelPairingUserInput(BluetoothDevice.class, device);
		                    	Log.i(TAG, "自动配对  cancelPairingUserInput  " + b1 );
		                        
		                    	
		                    	//Method createBondMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput");
		                		//Boolean b1 = (Boolean) createBondMethod.invoke(device);
		                    	//Log.i(TAG, "自动配对  cancelPairingUserInput  " + b1 );
		                    	
		                    	//Log.i(TAG, "自动配对  autoBond  " + b1);
		                    	//Log.i(TAG, "自动配对  createBond  " + b2);
		                        
		                    } catch (Exception e) {     
		                        e.printStackTrace();    
		                    }    
		                    break;    
                     
		                case BluetoothDevice.BOND_BONDED:  // 已配对 
		                    try {    
		                    	// 原本是 connect(device);  // 连接
		                    	//借鉴下边注释中的代码修改成这样-----------------------------------------------------------------------------
		                    	//服务器端是当一个进入的连接被接受时才产生一个BluetoothSocket，
		                    	//客户端是在打开一个到服务器端的RFCOMM信道时获得BluetoothSocket的。
		                    	
		                    	Log.i(TAG, "device 的 BluetoothClass 为：" + device.getBluetoothClass() );
		                    	btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		    					btSocket.connect(); 
		    					
		    					//8.18测试---没反应----------------
		    					//BluetoothServerSocket  bss = btAdapter.listenUsingRfcommWithServiceRecord("123123TEST",MY_UUID);
		    					//Log.i(TAG, "BluetoothServerSocket 为：" + bss.toString() );
		    					//8.18测试-------------------
		    					
		    					
		    					
		                    	//connect(device);  // 连接
		                    } catch (IOException e) {    
		                        e.printStackTrace();    
		                    }    
		                    break;    
            }    
        } else if ( BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)  ) {
        	Log.i(TAG2, "bondstate changed."); //连接状态改变了
        	
        	// 状态改变的广播      
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);    
                int connectState = device.getBondState();    
                switch (connectState) {    
                    case BluetoothDevice.BOND_NONE:    
                        break;    
                    case BluetoothDevice.BOND_BONDING:    
                        break;    
                    case BluetoothDevice.BOND_BONDED:    
                        try {    
                        	// 原本是 connect(device);  // 连接
	                    	//借鉴下边注释中的代码修改成这样-----------------------------------------------------------------------------
	                    	btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
	    					btSocket.connect(); 
                           // connect(device);    // 连接  
                        } catch (IOException e) {    
                            e.printStackTrace();    
                        }    
                        break;    
                }    
            }    
        	
        	
        
				
				
				
				
				
				
				
				
			/*	----android示例代码 bluetoothChat中的代码--------------------------------------------------------------------------		
				
			//获得socket
				try {
					 // 创建一个用于监听的服务端socket，通过下面这个方法，
					//NAME参数没关系，MY_UUID是确定唯一通道的标示符，用于连接的socket也要通过它产生
					btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
					btSocket.connect(); 
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//此时可以通信了
				try {
					OutputStream outStream = btSocket.getOutputStream();   
					  
					InputStream inStream = btSocket.getInputStream(); //可在TextView里显示  
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				
				
				
				//--先在ActiMain中测试，之后改进---------------------------------------------------------------------------------------------
				
				
			}
			
			
			
			/*else if (BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)) {
				//选择了连接的服务器设备
				BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				
				//开启设备连接线程---------------------------看看可否借鉴----------------------------------------------------------------------
				new BluetoothClientConnThread(handler, device).start();
				
			} else if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {
				//停止后台服务
				if (communThread != null) {
					communThread.isRun = false;
				}
				stopSelf();
				
			} else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				//获取数据
				Object data = intent.getSerializableExtra(BluetoothTools.DATA);
				if (communThread != null) {
					communThread.writeObject(data);
				}
				
			}*/
		

	};

	// --继承Service实现的一些方法---------------------------------------------------------------------------------------------------------------
	/**
	 * Service创建时的回调函数 为discoveryReceiver和controlReceiver添加IntentFilter，并注册
	 */
	@Override
	public void onCreate() {

		// discoveryReceiver的IntentFilter
		IntentFilter discoveryFilter = new IntentFilter();
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);

		// controlReceiver的IntentFilter
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(UtilBluetooth.ACTION_START_DISCOVERY);// 开始搜索
		controlFilter.addAction(UtilBluetooth.ACTION_SELECTED_DEVICE); //配对
		controlFilter.addAction(UtilBluetooth.ACTION_STOP_SERVICE); //停止本服务
		controlFilter.addAction(UtilBluetooth.ACTION_DATA_TO_SERVICE);
		controlFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//连接状态改变
		

		registerReceiver(discoveryReceiver, discoveryFilter);
		registerReceiver(controlReceiver, controlFilter);
	}

	/**
	 * Service销毁时的回调函数 解除对 discoveryReceiver和controlReceiver 的绑定
	 */
	@Override
	public void onDestroy() {
		unregisterReceiver(discoveryReceiver);
		unregisterReceiver(controlReceiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 集成Service必须实现的方法
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//8.18加进来的=================================
	//自动配对设置Pin值  
    static public boolean autoBond(Class btClass,BluetoothDevice device,String strPin) throws Exception {   
        Method autoBondMethod = btClass.getMethod("setPin",new Class[]{byte[].class});  
        Boolean result = (Boolean)autoBondMethod.invoke(device,new Object[]{strPin.getBytes()});  
        return result;  
    }  
  
//开始配对  
    static public boolean createBond(Class btClass,BluetoothDevice device) throws Exception {   
        Method createBondMethod = btClass.getMethod("createBond");   
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);   
        return returnValue.booleanValue();   
    } 
}