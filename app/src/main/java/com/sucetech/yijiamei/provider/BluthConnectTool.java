package com.sucetech.yijiamei.provider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.UserMsg;

import static com.sucetech.yijiamei.provider.BluthConnectTool.BluthStutaListener.coned;
import static com.sucetech.yijiamei.provider.BluthConnectTool.BluthStutaListener.failed;
import static com.sucetech.yijiamei.provider.BluthConnectTool.BluthStutaListener.weied;

public class BluthConnectTool {
    private Context context;
    private Handler mHandler;
    private Bluetooth_Scale mBl_Scale;
    private SCALENOW scalenow = new SCALENOW();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private String bluthMac;
    private BluthStutaListener bluthStutaListener;
    public  BluthConnectTool(final Context context,BluthStutaListener bluthStutacalback){
        this.context=context;
        this.bluthStutaListener=bluthStutacalback;
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Log.e("LLL", "1111");
                        Bundle bundle = msg.getData();
                        GetWeight(bundle.getByteArray("weight"));
                        if (scalenow.bOverFlag) {
                            ;
                        } else {
                            String wei = scalenow.sformatNetWeight.trim();
                            if (wei.contains("+")) {
                                wei = wei.replace("+", "");
                            }
                            wei = wei.trim();
                            bluthStutaListener.onBluthStutaListener(weied,wei);
//                            weightStr.setText(wei + " KG");
//                            mEventManager.notifyObservers(EventStatus.weight, wei);
                        }
                        break;
                    case 2:
                        if (msg.arg1 == 0) {
                            bluthStutaListener.onBluthStutaListener(failed,bluthMac);
                            Toast.makeText(context, "蓝牙链接失败", Toast.LENGTH_SHORT).show();
                        } else if (msg.arg1 == 1) {
                            bluthStutaListener.onBluthStutaListener(failed,bluthMac);
                            Toast.makeText(context, "蓝牙链接断开", Toast.LENGTH_SHORT).show();
                        } else if (msg.arg1 == 2) {
                            UserMsg.saveMac(bluthMac);
                            bluthStutaListener.onBluthStutaListener(coned,bluthMac);
                            Toast.makeText(context, "蓝牙链接成功", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("LLL", "2222");

                        break;
                    case 3:
                        connectBluth((String) msg.obj);
                        break;

                }
            }
        };
        mBl_Scale = new Bluetooth_Scale(context, mHandler);
    }
    public void startBlouth(String blouth) {
//        ((MainActivity) getContext()).showProgressDailogView("蓝牙链接中...");
//        this.setVisibility(View.VISIBLE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "此设备不支持蓝牙", Toast.LENGTH_SHORT).show();
//            ((MainActivity) getContext()).hideProgressDailogView();
            return;
        }
        connectBluth(blouth);
    }
    private void connectBluth(String bluth) {
        bluthMac = bluth;
        if (mBluetoothAdapter.enable()) {
            try {
                device = mBluetoothAdapter.getRemoteDevice(bluth);
                if (device != null) {
                    if (!mBl_Scale.connect(device)) {
                        Message message = Message.obtain();
                        message.what = 3;
                        message.obj = bluth;
                        mHandler.sendMessageDelayed(message, 1000);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, "二维码格式异常-->" + bluth, Toast.LENGTH_SHORT).show();
//                ((MainActivity) getContext()).hideProgressDailogView();
            }
        } else {
            Message message = Message.obtain();
            message.what = 3;
            message.obj = bluth;
            mHandler.sendMessageDelayed(message, 1000);
        }
    }

    public static class SCALENOW {
        public String sformatNetWeight = "0";
        public String sUnit = "0";
        public boolean bWeiStaFlag;
        public boolean bZeroFlag;
        public boolean bOverFlag;
    }
    void GetWeight(byte[] databuf) {
        int i, j, offset = 6;
        boolean StartFalg = false;
        scalenow.bZeroFlag = true;
        scalenow.bOverFlag = false;
        scalenow.bWeiStaFlag = false;
        switch (databuf[0]) {
            case 'o':
            case 'O':
                scalenow.bOverFlag = true;
                break;
            case 'u':
            case 'U':
                scalenow.bWeiStaFlag = false;
                offset = 6;    //6
                break;
            case 's':
            case 'S':
                scalenow.bWeiStaFlag = true;
                break;
        }
        if (databuf[5] == '-') offset = 5;
        for (i = 0; i < 14; i++) {
            if (databuf[i + offset] == '\'') databuf[i + offset] = '.';
            if (StartFalg) {
                if (((databuf[i + offset] > '9') || (databuf[i + offset] < '.')) && (!((databuf[i + offset] == ' ') && (databuf[i + offset + 1] <= '9')))) {
                    break;
                }
            } else if ((databuf[i + offset] >= '0') && (databuf[i + offset] <= '9')) {
                StartFalg = true;
                if (databuf[i + offset] != '0') scalenow.bZeroFlag = false;
            }
        }
        scalenow.sformatNetWeight = new String(databuf, offset, i);


        for (j = 0; j < 6; j++) {
            if (databuf[i + j + offset] < 0x20) {
                break;
            }
        }
        scalenow.sUnit = new String(databuf, i + offset, j);
    }
    public interface BluthStutaListener{
        public final int coning=1;
        public final int failed=2;
        public final int coned=3;
        public final int weied=4;
        public void onBluthStutaListener(int type,Object obj);
    }
}
