package com.apicloud.moduleprint;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class Mo_PrintDataService {
	// private IBlueToothPrinter blueToothPrinter = new
	// IBlueToothPrinter(webView);
	private Handler ourHandler = null;
	private Context context = null;
	private String deviceAddress = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice device = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static OutputStream outputStream = null;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private boolean isConnection = false;
	final String[] items = { "复位打印机", "标准ASCII字体", "压缩ASCII字体", "字体不放大", "宽高加倍", "取消加粗模式", "选择加粗模式", "取消倒置打印", "选择倒置打印", "取消黑白反显", "选择黑白反显", "取消顺时针旋转90°",
			"选择顺时针旋转90°" };
	final byte[][] byteCommands = { { 0x1b, 0x40 },// 复位打印机
			{ 0x1b, 0x4d, 0x00 },// 标准ASCII字体
			{ 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
			{ 0x1d, 0x21, 0x00 },// 字体不放大
			{ 0x1d, 0x21, 0x11 },// 宽高加倍
			{ 0x1b, 0x45, 0x00 },// 取消加粗模式
			{ 0x1b, 0x45, 0x01 },// 选择加粗模式
			{ 0x1b, 0x7b, 0x00 },// 取消倒置打印
			{ 0x1b, 0x7b, 0x01 },// 选择倒置打印
			{ 0x1d, 0x42, 0x00 },// 取消黑白反显
			{ 0x1d, 0x42, 0x01 },// 选择黑白反显
			{ 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
			{ 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°
	};
	private int sendstate;

	public Mo_PrintDataService(Context context, BluetoothDevice device) {
		super();
		this.context = context;
		this.device = device;
		// this.ourHandler = new Handler(){
		// @Override
		// public void handleMessage(Message msg) {
		// if (msg.arg1==100) {
		// Toast.makeText(context, "yichang", Toast.LENGTH_LONG).show();
		// }
		// super.handleMessage(msg);
		// }
		// };
	}

	/**
	 * 获取设备名称
	 * 
	 * @return String
	 */
	public String getDeviceName() {
		return this.device.getName();
	}

	/**
	 * 连接蓝牙设备
	 */
	public boolean connection() {
		if (!this.isConnection) {
			try {
				bluetoothSocket = this.device.createRfcommSocketToServiceRecord(uuid);
				bluetoothSocket.connect();
				outputStream = bluetoothSocket.getOutputStream();
				outputStream.write(byteCommands[4]);
				System.out.println("执行bluetoothSocket.connect()");
				this.isConnection = true;
				if (this.bluetoothAdapter.isDiscovering()) {
					System.out.println("关闭适配器！");
					this.bluetoothAdapter.isDiscovering();
				}
			} catch (Exception e) {
				// Message message = ourHandler.obtainMessage();
				// message.arg1=100;
				// ourHandler.sendMessage(message);
				// Toast.makeText(this.context, "连接失败！", 1).show();
				this.isConnection = false;
			}
			this.isConnection = true;
			System.out.println(">------------------------");
			// Toast.makeText(this.context, this.device.getName() + "连接成功！",
			// Toast.LENGTH_SHORT).show();
		}
		return isConnection;

	}

	/**
	 * 断开蓝牙设备连接
	 */
	public void disconnect() {
		System.out.println("断开蓝牙设备连接");
		if (isConnection || sendstate == Mo_Config.SEND_SUCCESS) {
			try {
				bluetoothSocket.close();
				outputStream.close();
				isConnection = false;
				System.out.println("执行disconnect()");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 选择指令
	 */
	public void selectCommand() {
		new AlertDialog.Builder(context).setTitle("请选择指令").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					outputStream.write(byteCommands[which]);
				} catch (IOException e) {
					Toast.makeText(context, "设置指令失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}).create().show();
	}

	public void tryConnection() {
		disconnect();
		isConnection = false;
	}

	// public int send(List<String> p_list) {
	// sendstate = 0;
	// if (this.isConnection) {
	// System.out.println(device.getName() + "开始打印！！");
	// try {
	// String sendData="ddddddddddddddddd";
	// byte[] data = sendData.getBytes("gbk");
	// outputStream.write(data, 0, data.length);
	// outputStream.flush();
	// // outputStream.close();
	// sendstate = Mo_Config.SEND_SUCCESS;
	// } catch (IOException e) {
	// sendstate = Mo_Config.SEND_FAIL;
	// }
	// } else {
	// sendstate = Mo_Config.DEVICE_CONNECT_FAIL;
	// }
	// return sendstate;
	// }
	// /**
	// * 发送数据
	// */
	public int send(List<String> p_list) {
		sendstate = 0;
		if (this.isConnection) {
			System.out.println(device.getName() + "开始打印！！");

			try {
				for (int i = 0; i < p_list.size(); i++) {
					outputStream.write(byteCommands[4-i]);
					String sendData = p_list.get(i).toString();
					byte[] data = sendData.getBytes("gbk");
						outputStream.write(data, 0, data.length);
						outputStream.flush();
							Thread.sleep(1000);
					// outputStream.close();
					sendstate = Mo_Config.SEND_SUCCESS;
				}
			} catch (IOException e) {
				sendstate = Mo_Config.SEND_FAIL;
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			sendstate = Mo_Config.DEVICE_CONNECT_FAIL;
		}

		return sendstate;
	}

}
