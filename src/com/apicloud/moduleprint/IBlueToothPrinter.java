package com.apicloud.moduleprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.R.menu;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apicloud.pkg.sdk.R;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class IBlueToothPrinter extends UZModule {
	private Set<BluetoothDevice> setBondedDevice;
	private List<BluetoothDevice> listBondedDevice;
	private BluetoothAdapter ourBluetoothAdapter;
	private boolean isinit;
	private String str;
	private Mo_ourUtils ourUtils = new Mo_ourUtils(mContext);
	public static List<String> jsonStr = new ArrayList<String>();
	private Handler ourHandler = null;

	boolean deviceisConnect;
	private Mo_PrintDataService printDataService;
	private BluetoothDevice bluetoothDevice;
	private BluetoothDevice bonedDevice;
	private List<String> printString_list = new ArrayList<String>();

	public IBlueToothPrinter(UZWebView webView) {

		super(webView);
		listBondedDevice = new ArrayList<BluetoothDevice>();
		deviceisConnect = false;
		// 初始化蓝牙适配器
		isinit = false;
		ourBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		setBondedDevice = ourBluetoothAdapter.getBondedDevices();
		if (!isinit) {
			System.out.println("初始化-----------------");
			initPrintsystem();
		}
		ourHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg2) {
				case Mo_Config.SEND_SUCCESS:
					Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public void jsmethod_startPrint(UZModuleContext moduleContext) {
		//Toast.makeText(mContext, "diaoyongle", Toast.LENGTH_LONG).show();
		JSONObject demo = moduleContext.optJSONObject("demo");
		str = demo.toString();
		Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
		printString_list = ourUtils.analysis(str);
		for (int i = 0; i <printString_list.size(); i++) {
			System.out.println(printString_list.get(i).toString());
		}
		
		
		if (printDataService==null) {
			creatSoket();
		}
		
		//if (ourPrintThread.isAlive()) {
		 new Threadpriter().start();
		//}
		
		

	}

	public void initPrintsystem() {
		ourBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// listbondedDeviceAddress.clear();
		if (!ourBluetoothAdapter.isEnabled()) {
			// 启动蓝牙适配器
			Intent openBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// 启动一个Activity 提示用户打开蓝牙
			startActivityForResult(openBluetoothIntent, 1);
		}

		// 如果本机未绑定蓝牙打印机,弹出一个dialongActivity，进行搜索周边蓝牙设备。
		if (setBondedDevice.size() <= 0) {
			Intent intent = new Intent(mContext, Mo_BluetoothActivity.class);
			startActivity(intent);
		}
		if (listBondedDevice.isEmpty()) {
			listBondedDevice = ourUtils.getBondDevice(setBondedDevice);
			System.out.println("listBondedDevice"+listBondedDevice.size());
		}
		isinit = true;
	}

	public void creatSoket() {
		if (listBondedDevice.size() <= 0) {
			initPrintsystem();
			System.out.println("重复执行initPrintsystem()");
		}
		
		bonedDevice = listBondedDevice.get(0);
		if (printDataService == null) {
			System.out.println("重复执行new Mo_PrintDataService");
			printDataService = new Mo_PrintDataService(mContext, bonedDevice);
		}
		
	}

	private class Threadpriter  extends Thread{

		public void run() {
			System.out.println("是否第二次启动线程");
//			if (printDataService ==null) {
//				bonedDevice = listBondedDevice.get(0); 
//				printDataService= new Mo_PrintDataService(mContext, bonedDevice);
//				System.out.println("第二次重复执行new Mo_PrintDataService");
//			}
			printDataService.connection();
			printDataService.send(printString_list);
				Message message = ourHandler.obtainMessage();
		        message.arg2 =Mo_Config.SEND_SUCCESS;
		        ourHandler.sendMessage(message);
		}
        
	};

}
