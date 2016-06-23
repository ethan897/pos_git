package com.apicloud.moduleprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Mo_ourUtils {
	private Context ourContext;
	private String str;
	private List<BluetoothDevice> list_bondedDivce = new ArrayList<BluetoothDevice>();
	private List<String> listPrintString = new ArrayList<String>();

	public Mo_ourUtils(Context ourContext) {
		this.ourContext = ourContext;
	}

	public List<BluetoothDevice> getBondDevice(Set<BluetoothDevice> setBondedDevice) {
		Iterator<BluetoothDevice> it = setBondedDevice.iterator();
		while (it.hasNext()) {
			BluetoothDevice bondedPrinter = (BluetoothDevice) it.next();
			System.out.println("输出绑定设备的名称===" + bondedPrinter.getName());
			list_bondedDivce.add(bondedPrinter);
		}
		return list_bondedDivce;
	}

	public void initPtintSystem(BluetoothAdapter ourBluetoothAdapter, Set<BluetoothDevice> setBondedDevice) {
		// 1.判断本机是否有蓝牙
		if (ourBluetoothAdapter == null) {
			Toast.makeText(ourContext, "您使用的设备没有蓝牙", Toast.LENGTH_LONG).show();
		}
		// sbongedDeviceAdderr.clear();
		// getBondDeviceAddress();
		// 2.判断本机蓝牙是否打开
		if (!ourBluetoothAdapter.isEnabled()) {
			// 启动蓝牙适配器
			Intent openBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// 启动一个Activity 提示用户打开蓝牙
			((Activity) ourContext).startActivityForResult(openBluetoothIntent, 1);
		}

		// 如果本机未绑定蓝牙打印机,弹出一个dialongActivity，进行搜索周边蓝牙设备。
		if (setBondedDevice.size() <= 0) {
			Intent intent = new Intent();
			intent.setClass(ourContext, Mo_BluetoothActivity.class);
			ourContext.startActivity(intent);
		}
	}

	/**
	 * 该方法用来解析web端传来菜单字符串
	 * 
	 * @param menuJson
	 *            菜单字符串
	 * @return
	 */

	public List<String> analysis(String menuJson) {
		
		StringBuffer sb = new StringBuffer();

		
		//sb.append(title);
		String ordernum = "\n" + "订单编号" + " : ";
		try {
			JSONObject menuJesonObj = new JSONObject(menuJson);
			//sb.
			//sb.append(ordernum);
			
			//String DeskID1 = menuJesonObj.getString("DeskID");
			
			String DeskID ="\n桌号:E"+"20"+"\n";
			listPrintString.add(0, DeskID);
			String nullStr = "                        ";
			listPrintString.add(1, nullStr);
			String title = "\n       " + "小恒水饺华贸店（上菜单）" + "   " + "\n";
			listPrintString.add(2, title);
			String orderID = "\n" + "下单编号" + " : ";
			sb.append(orderID);
			String OrderID = menuJesonObj.getString("OrderID");
			sb.append(OrderID);
			String ordertime = "\n" + "下单时间" + " : ";
			sb.append(ordertime);
			String OrderTime = menuJesonObj.getString("OrderTime");
			sb.append(OrderTime);
			
			
			String splitline = "\n********************************\n";
			sb.append(splitline);
			String rowName = "菜品名称       " + "单价 " + " 数量  " + " 单位"+ "\n";
			sb.append(rowName);
			// JSONArray menuJesonObj = new JSONObject(menuJson);
			JSONArray OrderItemsJson = new JSONArray(menuJesonObj.getString("OrderItemsJson"));// menuJesonObj.getJSONArray("OrderItemsJson");
			for (int i = 0; i < OrderItemsJson.length(); i++) {
				JSONObject con = OrderItemsJson.getJSONObject(i);
				String Dish_Name = con.getString("Dish_Name");
				System.out.println("Dish_Name" + Dish_Name);
				String Dish_Price = con.getString("Dish_Price");
				String Dish_Number = con.getString("Dish_Number");
				String remark = con.getString("remark");
				String account = con.getString("account");
				//String Dish_Unit = con.getString("Dish_Unit");
				if (account.equals("10")) {
					account = "";
				}
				if (Dish_Name.length() < 14) {
					for (int j = 0; j < 14 - Dish_Name.length(); j++) {
						Dish_Name = Dish_Name + "  ";
					}
				}
				String lan = "\n"+Dish_Name + "" + Dish_Price + "  " + Dish_Number +" "+"盘"+"";
				sb.append(lan);
				String other = "    "+remark + "  " + account + "\n";
				sb.append(other);
				String innerSplitLine ="...............................";
				sb.append(innerSplitLine);
			}
			String totalPrice = "\n" + "应付金额" + " : ";
			sb.append(totalPrice);
			String TotalPrice = menuJesonObj.getString("TotalPrice");
			sb.append(TotalPrice);
			String payMoney = "\n" + "折后金额" + " : ";
			sb.append(payMoney);
			String PayMoney = menuJesonObj.getString("PayMoney");
			sb.append(PayMoney);
			
			String splitLine = "\n*******************************\n";
			sb.append(splitLine);
			String slogan = "" + "----->吃饺子是件时尚的事" + "<-----";
			sb.append(slogan);
			String adress = "\n" + "饭店地址：蓝堡国际2号楼SB02" + "";
			sb.append(adress);
			String tel = "\n" + "订餐电话：010-8599 9890" + "";
			sb.append(tel);
			String sysNum = "\n" + "餐饮管理：13521770971" + "";
			sb.append(sysNum);
			
			
			
			String over = "\n\n\n\n";
			
			String last =sb.toString();
			listPrintString.add(3,last);
			listPrintString.add(4,over);
		} catch (Exception e) {
			Toast.makeText(ourContext, "菜单解析异常", Toast.LENGTH_LONG).show();
		}

		return listPrintString;
	}

}
