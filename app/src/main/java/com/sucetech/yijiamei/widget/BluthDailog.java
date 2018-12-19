package com.sucetech.yijiamei.widget;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.adapter.BluthAdapter;
import com.sucetech.yijiamei.model.BluetoothDeviceBean;
import com.sucetech.yijiamei.view.HomePage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluthDailog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private List<BluetoothDeviceBean> bluetoothDeviceList;
    private ListView listView;
    private BluthAdapter bluthAdapter;
    private BluetoothReceiver mBluetoothReceiver;
    private Context context;
    private HomePage homePage;
    private View close;

    public BluthDailog(Context context, HomePage homePage) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.homePage = homePage;
        View view = LayoutInflater.from(context).inflate(R.layout.bluth_dailog, null);
        listView = view.findViewById(R.id.boluthList);
        close = view.findViewById(R.id.bluthClose);
        close.setOnClickListener(this);
        close.setVisibility(View.VISIBLE);
        setContentView(view);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x); //宽度设置为屏幕宽度的0.5
        layoutParams.height = (int) (point.y); //高度设置为屏幕高度的0.5
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(layoutParams);
        bluetoothDeviceList = new ArrayList<>();
        bluthAdapter = new BluthAdapter(context, bluetoothDeviceList);
        listView.setAdapter(bluthAdapter);
        listView.setOnItemClickListener(this);
//        initdata();
        findBluthDevice();
    }

    private void initdata() {
        bluetoothDeviceList.clear();
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                BluetoothDeviceBean bluetoothDeviceBean = new BluetoothDeviceBean();
                bluetoothDeviceBean.bluetoothDevice = device;
                bluetoothDeviceBean.status = 0;
                bluetoothDeviceList.add(bluetoothDeviceBean);
            }
            bluthAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluthClose:
                this.dismiss();
                if (mBluetoothReceiver!=null)
                this.getContext().unregisterReceiver(mBluetoothReceiver);
                break;
        }

    }

    private void findBluthDevice() {
        if (mBluetoothReceiver == null) registerBluthBroadCast();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initdata();
        if (mBluetoothAdapter.enable()) {
            mBluetoothAdapter.startDiscovery();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode,  KeyEvent event) {
        return true;
    }

    private void registerBluthBroadCast() {
        mBluetoothReceiver = new BluetoothReceiver();
// 动态注册注册广播接收器。接收蓝牙发现讯息
        IntentFilter btFilter = new IntentFilter();
        btFilter.setPriority(1000);
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        this.getContext().registerReceiver(mBluetoothReceiver, btFilter);
    }

    private boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < bluetoothDeviceList.size(); i++) {
            if (bluetoothDeviceList.get(i).status == 1) {
                Toast.makeText(context, "链接中请稍后...", Toast.LENGTH_LONG).show();
         return;
            }
        }
        bluetoothDeviceList.get(position).status = 1;
        bluthAdapter.notifyDataSetChanged();
        homePage.bluthConnectTool.startBlouth(bluetoothDeviceList.get(position).bluetoothDevice.getAddress());
    }

    public void setBluthConFailed(String mac) {
        for (int i = 0; i < bluetoothDeviceList.size(); i++) {
            if (bluetoothDeviceList.get(i).bluetoothDevice.getAddress().equals(mac)) {
                bluetoothDeviceList.get(i).status = 0;
            }
        }
        bluthAdapter.notifyDataSetChanged();
    }

    public void setBluthConOk(String mac) {
        for (int i = 0; i < bluetoothDeviceList.size(); i++) {
            if (bluetoothDeviceList.get(i).bluetoothDevice.getAddress().equals(mac)) {
                bluetoothDeviceList.get(i).status = 2;
            }
        }
        bluthAdapter.notifyDataSetChanged();
        close.setVisibility(View.VISIBLE);
        this.dismiss();
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); //得到action
            Log.e("action1=", action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  //发现设备
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice.getName() != null && !btDevice.getName().equals("")) {
                    for (int i = 0; i <bluetoothDeviceList.size() ; i++) {
                        if (bluetoothDeviceList.get(i).bluetoothDevice.getName()!=null
                                &&bluetoothDeviceList.get(i).bluetoothDevice.getName().equals(btDevice.getName())){
                            return;
                        }
                    }

                    BluetoothDeviceBean bluetoothDeviceBean = new BluetoothDeviceBean();
                    bluetoothDeviceBean.bluetoothDevice = btDevice;
                    bluetoothDeviceBean.status = 0;
                    bluetoothDeviceList.add(bluetoothDeviceBean);
                    bluthAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothDevice.ACTION_CLASS_CHANGED.equals(action)) {
                Toast.makeText(getContext(), "ACTION_CLASS_CHANGED", Toast.LENGTH_SHORT).show();
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                startBlouth(btDevice.getAddress());
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    Method setPairingConfirmation = btDevice.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
                    setPairingConfirmation.invoke(btDevice, true);
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    Method removeBondMethod = btDevice.getClass().getDeclaredMethod("setPin",
                            new Class[]
                                    {byte[].class});
                    Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                            new Object[]
                                    {"1234".getBytes()});
                    Log.e("returnValue", "" + returnValue);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
