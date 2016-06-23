package com.apicloud.moduleprint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.apicloud.pkg.sdk.R;

public class Mo_BluetoothActivity extends Activity {
	private ListView unbondDevicesListView;
	// 用来显示已被绑定的蓝牙设备
	private ListView bondDevicesListView;

	private BluetoothAdapter ourBluetoothAdapter;

	private ArrayList<BluetoothDevice> unbondDevice = null;
	private ArrayList<BluetoothDevice> bondDevices = null;// 用于存放已配对蓝牙设备
	public static List<String> jsonStr = new ArrayList<String>();
	private Context context = null;
	private Mo_BluetoothService ourBluetoothService = null;

	// private BluetoothActivity bluetoothActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.mo_bluetooth_layout);
		this.initView();
		setTitle("蓝牙设备");
		
		
	

	}

	private void initView() {

		// 用来显示扫描到设备，但扫描到的设备已绑定，则自动被移除
		unbondDevicesListView = (ListView) this.findViewById(R.id.unbondDevices);
		// 用来显示已经绑定的设备
		bondDevicesListView = (ListView) this.findViewById(R.id.bondDevices);
		// 用来存放绑定蓝牙设备
		bondDevices = new ArrayList<BluetoothDevice>();
		// 用于存放扫描到附近的蓝牙设备
		unbondDevice = new ArrayList<BluetoothDevice>();
		// 初始化广播过滤器
		initIntentFilter();

		// 打开蓝牙
		OpenBlueTooth();
		// 设置蓝牙可见
		setdiscory();
		// 扫描周边蓝牙
		scannerBlueToothDevice();

	}

	// 屏蔽返回键的代码:
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 打卡本机蓝牙
	public void OpenBlueTooth() {
		// 判断本机是否有蓝牙
		if (ourBluetoothAdapter == null) {
			ourBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		// 判断蓝牙设备是否可用
		if (!ourBluetoothAdapter.isEnabled()) {
			// 启动蓝牙适配器
			// 启动一个Activity 提示用户打开蓝牙
			Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(mIntent, 1);
		}
		// Set<BluetoothDevice> device = ourBluetoothAdapter.getBondedDevices();
		// if (device.size()>0) {
		// for (Iterator iterator = device.iterator();iterator.hasNext();) {
		// BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
		// Toast.makeText(mContext, bluetoothDevice.getName(),
		// Toast.LENGTH_LONG).show();
		// }
		//
		// }
	}

	/**
	 * 设置蓝牙的可见性
	 */
	private void setdiscory() {
		Intent disIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		disIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivity(disIntent);
	}

	/**
	 * 扫描周边的蓝牙设备
	 */
	private void scannerBlueToothDevice() {
		// 蓝牙扫描是一个异步任务，每发现一个蓝牙设备，发一次广播。
		ourBluetoothAdapter.startDiscovery();
	}

	private void addBondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.bondDevices.size();
		System.out.println("已绑定设备数量：" + count);
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.bondDevices.get(i).getName());
			data.add(map);// 把item项的数据加到data中
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.device_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(Mo_BluetoothActivity.this, data, R.layout.mo_bonddevice_item, from, to);
		// 把适配器装载到listView中
		this.bondDevicesListView.setAdapter(simpleAdapter);

		this.bondDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BluetoothDevice device = bondDevices.get(arg2);
				Mo_PrintData pd = new Mo_PrintData(Mo_BluetoothActivity.this, device);
				pd.initListener();
				// send
			}
		});
	}

	/**
	 * 添加未绑定蓝牙设备到list集合
	 * 
	 * @param device
	 */
	public void addUnbondDevices(BluetoothDevice device) {
		System.out.println("未绑定设备名称：" + device.getName());
		if (!this.unbondDevice.contains(device)) {
			this.unbondDevice.add(device);
		}
	}

	/**
	 * 添加已绑定蓝牙设备到list集合
	 * 
	 * @param device
	 */
	public void addBandDevices(BluetoothDevice device) {
		System.out.println("已绑定设备名称：" + device.getName() + "add" + device.getAddress());
		if (!this.bondDevices.contains(device)) {
			this.bondDevices.add(device);
		}
	}

	/**
	 * 添加未绑定蓝牙设备到ListView
	 */
	private void addUnbondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.unbondDevice.size();
		System.out.println("未绑定设备数量：" + count);
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.unbondDevice.get(i).getName());
			data.add(map);// 把item项的数据加到data中
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.device_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(Mo_BluetoothActivity.this, data, R.layout.mo_unbonddevice_item, from, to);

		// 把适配器装载到listView中
		this.unbondDevicesListView.setAdapter(simpleAdapter);

		// 为每个item绑定监听，用于设备间的配对
		this.unbondDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				try {
					Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
					createBondMethod.invoke(unbondDevice.get(arg2));
					// 将绑定好的设备添加的已绑定list集合
					bondDevices.add(unbondDevice.get(arg2));
					// 将绑定好的设备从未绑定list集合中移除
					unbondDevice.remove(arg2);
					addBondDevicesToListView();
					addUnbondDevicesToListView();
				} catch (Exception e) {
					Toast.makeText(Mo_BluetoothActivity.this, "配对失败！", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private void initIntentFilter() {
		// 设置广播信息过滤
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// 注册广播接收器，接收并处理搜索结果
		context.registerReceiver(receiver, intentFilter);
	}

	/**
	 * 广播接收器，接收扫描到的周边蓝牙设备
	 * 
	 * @author winner
	 * 
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		ProgressDialog progressDialog = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					addBandDevices(device);
				} else {
					addUnbondDevices(device);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				progressDialog = ProgressDialog.show(context, "请稍等...", "搜索蓝牙设备中...", true);

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				System.out.println("设备搜索完毕");
				progressDialog.dismiss();

				addUnbondDevicesToListView();	
				addBondDevicesToListView();
				// bluetoothAdapter.cancelDiscovery();
			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (ourBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
					System.out.println("--------打开蓝牙-----------");

					// searchDevices.setEnabled(true);
					bondDevicesListView.setEnabled(true);
					unbondDevicesListView.setEnabled(true);
				} else if (ourBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
					System.out.println("--------关闭蓝牙-----------");
					// switchBT.setText("打开蓝牙");
					// searchDevices.setEnabled(false);
					bondDevicesListView.setEnabled(false);
					unbondDevicesListView.setEnabled(false);
				}
			}

		}

	};

}
