package com.qin.bt;

import android.app.Activity;
import android.os.Bundle;

import java.util.Iterator;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{

    private static String TAG = "Bluetooth_State";

    private BluetoothAdapter mBluetoothAdapter; // 本机蓝牙适配器对象

    private TextView btDesc;

    private Button btOpen;

    private Button btClose;

    private Button btOpenBySystem; // 调用系统API去打开蓝牙

    private Button btDiscoveryDevice;

    private Button btCancelDiscovery;
    
    private Button btDiscoveryBySystem;  //调用系统Api去扫描蓝牙设备

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 获得本机蓝牙适配器对象引用
        if (mBluetoothAdapter == null)
        {
            toast("对不起 ，您的机器不具备蓝牙功能");
            return;
        }
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        this.registerReceiver(BluetoothReciever, bluetoothFilter);

        //蓝牙扫描相关设备
        IntentFilter btDiscoveryFilter = new IntentFilter();
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(BTDiscoveryReceiver, btDiscoveryFilter);

        int initialBTState = mBluetoothAdapter.getState();
        printBTState(initialBTState); // 初始时蓝牙状态

        initialViews();

        btDesc.setText(" Name : " + mBluetoothAdapter.getName() + " Address : "
                + mBluetoothAdapter.getAddress() + " Scan Mode --" + mBluetoothAdapter.getScanMode());
        
        //打印处当前已经绑定成功的蓝牙设备
        Set<BluetoothDevice> bts = mBluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator  = bts.iterator();
        while(iterator.hasNext())
        {
            BluetoothDevice bd = iterator.next() ;
            Log.i(TAG , " Name : " + bd.getName() + " Address : "+ bd.getAddress() ); ;
            Log.i(TAG, "Device class" + bd.getBluetoothClass());    
        }
        
        BluetoothDevice findDevice =  mBluetoothAdapter.getRemoteDevice("00:11:22:33:AA:BB");
        
        Log.i(TAG , "findDevice Name : " + findDevice.getName() + "  findDevice Address : "+ findDevice.getAddress() ); ;
        Log.i(TAG , "findDevice class" + findDevice.getBluetoothClass()); 
              
    }

    private void initialViews()
    {
        btDesc = (TextView) findViewById(R.id.btDesc);
        btOpen = (Button) findViewById(R.id.btOpen);
        btClose = (Button) findViewById(R.id.btClose);
        btOpenBySystem = (Button) findViewById(R.id.btOpenBySystem);
        btDiscoveryDevice = (Button) findViewById(R.id.btDiscoveryDevice);
        btCancelDiscovery = (Button) findViewById(R.id.btCancelDiscovery);
        btDiscoveryBySystem = (Button) findViewById(R.id.btDiscoveryBySystem);
        
        btOpen.setOnClickListener(this);
        btClose.setOnClickListener(this);
        btOpenBySystem.setOnClickListener(this);
        btDiscoveryDevice.setOnClickListener(this);
        btCancelDiscovery.setOnClickListener(this);
        btDiscoveryBySystem.setOnClickListener(this);
    }

    //蓝牙开个状态以及扫描状态的广播接收器
    private BroadcastReceiver BluetoothReciever = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### Bluetooth State has changed ##");

                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);

                printBTState(btState);
            }
            else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### ACTION_SCAN_MODE_CHANGED##");
                int cur_mode_state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                int previous_mode_state = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                
                Log.v(TAG, "### cur_mode_state ##" + cur_mode_state + " ~~ previous_mode_state" + previous_mode_state);
                
            }
        }

    };

    //蓝牙扫描时的广播接收器
    private BroadcastReceiver BTDiscoveryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_DISCOVERY_STARTED ##");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_DISCOVERY_FINISHED ##");
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT BluetoothDevice.ACTION_FOUND ##");
                
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                if(btDevice != null)
                    Log.v(TAG , "Name : " + btDevice.getName() + " Address: " + btDevice.getAddress());
                    
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()))
            {
                Log.v(TAG, "### BT ACTION_BOND_STATE_CHANGED ##");
                
                int cur_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                int previous_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE);
                
                
                Log.v(TAG, "### cur_bond_state ##" + cur_bond_state + " ~~ previous_bond_state" + previous_bond_state);
            }
        }

    };
    
    private void printBTState(int btState)
    {
        switch (btState)
        {
            case BluetoothAdapter.STATE_OFF:
                toast("蓝牙状态:已关闭");
                Log.v(TAG, "BT State ： BluetoothAdapter.STATE_OFF ###");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                toast("蓝牙状态:正在关闭");
                Log.v(TAG, "BT State :  BluetoothAdapter.STATE_TURNING_OFF ###");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                toast("蓝牙状态:正在打开");
                Log.v(TAG, "BT State ：BluetoothAdapter.STATE_TURNING_ON ###");
                break;
            case BluetoothAdapter.STATE_ON:
                toast("蓝牙状态:已打开");
                Log.v(TAG, "BT State ：BluetoothAdapter.STATE_ON ###");
                break;
            default:
                break;
        }
    }

    private final int REQUEST_OPEN_BT_CODE = 1;
    private final int REQUEST_DISCOVERY_BT_CODE = 2;
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

        boolean wasBtOpened = mBluetoothAdapter.isEnabled(); // 是否已经打开

        switch (v.getId())
        {
            case R.id.btOpen: // 打开

                boolean result = mBluetoothAdapter.enable();

                if (result)
                    toast("蓝牙打开操作成功");
                else if (wasBtOpened)
                    toast("蓝牙已经打开了");
                else
                    toast("蓝牙打开失败");

                break;
            case R.id.btClose: // 关闭
                boolean result1 = mBluetoothAdapter.disable();
                if (result1)
                    toast("蓝牙关闭操作成功");
                else if (!wasBtOpened)
                    toast("蓝牙已经关闭");
                else
                    toast("蓝牙关闭失败.");

                break;
            case R.id.btOpenBySystem:  //调用系统API打开蓝牙设备

                Log.e(TAG, " ## click btOpenBySystem ##");
                if (!wasBtOpened) //未打开蓝牙，才需要打开蓝牙
                {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
                }
                else
                    toast("Hi ，蓝牙已经打开了，不需要在打开啦 ~~~");
                break;
            case R.id.btDiscoveryDevice: //扫描时，必须先打开蓝牙
                if (!mBluetoothAdapter.isDiscovering()){
                    Log.i(TAG, "btCancelDiscovery ### the bluetooth dont't discovering");
                    mBluetoothAdapter.startDiscovery();
                }
                else
                    toast("蓝牙正在搜索设备了 ---- ");
                break;
            case R.id.btCancelDiscovery:   //取消扫描
                if (mBluetoothAdapter.isDiscovering()){
                    Log.i(TAG, "btCancelDiscovery ### the bluetooth is isDiscovering");
                    mBluetoothAdapter.cancelDiscovery();
                }
                else
                    toast("蓝牙并未搜索设备 ---- ");
                break;
            
            case R.id.btDiscoveryBySystem :  //使蓝牙能被扫描
                Intent discoveryintent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryintent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoveryintent, REQUEST_DISCOVERY_BT_CODE);
                break ;
        }
    }

    //请求码
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_OPEN_BT_CODE){
            if (resultCode == RESULT_CANCELED)
            {
                toast("Sorry , 用户拒绝了您的打开蓝牙请求.");
            }
            else
                toast("Year , 用户允许了您的打开蓝牙请求.");
        }
        else if(requestCode == REQUEST_DISCOVERY_BT_CODE)
        {
            if (resultCode == RESULT_CANCELED)
            {
                toast("Sorry , 用户拒绝了，您的蓝牙不能被扫描.");
            }
            else
                toast("Year , 用户允许了，您的蓝牙能被扫描");
        }
            
    }

    private void toast(String str)
    {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}
