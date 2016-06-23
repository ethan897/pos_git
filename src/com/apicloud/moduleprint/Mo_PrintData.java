package com.apicloud.moduleprint;

import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class Mo_PrintData {  
    private Context context = null;  
    private BluetoothDevice device;
    private Mo_PrintDataService printDataService = null; 
    public Mo_PrintData(Context context,BluetoothDevice device) {
		this.device=device;
		this.context=context;
		//initListener();  
	}
    public  void initListener() {  
    	printDataService = new Mo_PrintDataService(this.context,  
    			device);  
    	//System.out.println("绑定的蓝牙地址="+address);
        String bsb="bianshengguang";
       
        	 printDataService.connection();
        	// printDataService.send(bsb);
       
  
       // EditText printData = (EditText) this.findViewById(R.id.print_data);  
    }  
  
      
}
