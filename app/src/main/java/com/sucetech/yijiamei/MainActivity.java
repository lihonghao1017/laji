package com.sucetech.yijiamei;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mapbar.android.control.AppActivity;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.OnDialogListener;
import com.mapbar.android.model.PageObject;
import com.mapbar.android.model.VersionInfo;
import com.sucetech.yijiamei.control.MainController;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.provider.BitmapUtils;
import com.sucetech.yijiamei.provider.PhotoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppActivity {
    private final static String TAG = "MainActivity";

    private MainController mMainController;
    public NfcAdapter mNfcAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showContacts();
        getPersimmions();
        UserMsg.initUserMsg(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		long time = System.currentTimeMillis();
        setContentView(R.layout.layout_main);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xff4AA9FD);
        Log.isDebug = true;

        mMainController = new MainController(this);
//		Log.e(TAG, TAG+":onCreate->time="+(System.currentTimeMillis()-time));

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!ifNFCUse(this)) {
            return;
        }
    }
    protected Boolean ifNFCUse(Context context) {
        if (mNfcAdapter == null) {
            Toast.makeText(context, "设备不支持NFC！", Toast.LENGTH_LONG).show();
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            Toast.makeText(context, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();
    private String permissionInfo;
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 777);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }
    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, 333);
        } else {
            //   init();
            // getLoc();
        }
    }
    @Override
    public PageObject createPage(int index) {
        return mMainController.createPage(index);
    }

    @Override
    public int getAnimatorResId() {
        return R.id.animator;
    }

    @Override
    public int getMainPosition() {
        return Configs.VIEW_POSITION_Main;
    }

    @Override
    public int getOutPosition() {
        return Configs.VIEW_POSITION_NONE;
    }

    @Override
    public int getNonePositioin() {
        return Configs.VIEW_POSITION_NONE;
    }

    @Override
    public int getViewNoneFlag() {
        return Configs.VIEW_FLAG_NONE;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            sendToPage(Configs.VIEW_POSITION_Main,Configs.VIEW_POSITION_Main,intent);
        }
    }
    /**
     * 正常的Activity onResume，
     * 不在此处做任何初始化工作
     * 一般是应用前台后台来回切换时
     * 按须做处理即可
     */
    @Override
    public void onResume() {
        mMainController.onResume();
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        super.onResume();
    }
    private PendingIntent mPendingIntent;
    @Override
    protected void onStart() {
        super.onStart();
        //此处adapter需要重新获取，否则无法获取message
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //一旦截获NFC消息，就会通过PendingIntent调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }

    /**
     * 正常的Activity onPause
     */
    @Override
    protected void onPause() {
        mMainController.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }
    private int id;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private File fileUri;
    private Uri imageUri;
    public void requestPicture(int id) {
        this.id = id;
        if (hasSdcard()) {
            fileUri = creatFile();
            imageUri = Uri.fromFile(fileUri);
            //通过FileProvider创建一个content类型的Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.sucetech.yijiamei", fileUri);
            }
            PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "设备没有SD卡！", Toast.LENGTH_LONG).show();
        }
    }
    private File creatFile() {
        File iifile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        iifile.getParentFile().mkdirs();
        return iifile;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
           if (requestCode == CODE_CAMERA_REQUEST) {
//                Bitmap bitmap = PhotoUtils.getBitmapFromUri(imageUri, this);
                String newFile = fileUri.getParent() + "yijiamei_" + fileUri.getName();
                Bitmap bitmap = BitmapUtils.getSmallBitmap(fileUri.getPath(), 680, 680, new File(newFile));
                FormImage formImage = new FormImage();
                formImage.mBitmap = bitmap;
                formImage.mFileName = newFile;
                formImage.id = id;
               sendToPage(Configs.VIEW_POSITION_Main,Configs.VIEW_POSITION_Main,formImage);

           }
        }

    }
    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 正常的Activity onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainController.onDestroy();
    }

    private boolean isCanExit = false;

    /**
     * 界面有切换动作时，会调用到
     */
    @Override
    public void onPageActivity() {
        isCanExit = false;
    }

    /**
     * 是否可以正常退出
     */
    @Override
    public boolean canExit() {
        if (!isCanExit) {
            this.isCanExit = true;
            this.showAlert(R.string.toast_againto_exit);
            return false;
        }
        return true;
    }

    /**
     * 正常的Activity onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMainController.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 统一Dialog样式，可在此做处理
     */
    @Override
    public void showDialog(int style, int resId, String title, String content, String cancelText, String okText,
                           OnDialogListener listener) {
        showDialog(style, resId, title, content,
                cancelText, okText, listener, Gravity.CENTER);
    }

    /**
     * 统一Dialog样式，可在此做处理
     */
    @Override
    public void showDialog(int style, int resId, String title, String content,
                           String cancelText, String okText, final OnDialogListener listener, int gravity) {
		/*
		final Dialog dialog = new Dialog(this, style);
		dialog.setContentView(resId);
		dialog.setCanceledOnTouchOutside(false);
		if(!TextUtils.isEmpty(title))
		{
			TextView motorcade_tv_title = (TextView)dialog.findViewById(R.id.motorcade_tv_title);
			if(motorcade_tv_title != null)
			{
				motorcade_tv_title.setVisibility(View.VISIBLE);
				motorcade_tv_title.setText(title);
			}
		}
		TextView tv = (TextView) dialog.findViewById(R.id.motorcade_tv_context);
		tv.setGravity(gravity);
		tv.setText(content);
		TextView tv_yes = (TextView) dialog.findViewById(R.id.motorcade_tv_yes);
		if(!TextUtils.isEmpty(okText))
			tv_yes.setText(okText);
		tv_yes.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (dialog != null && dialog.isShowing())
					dialog.cancel();
				if(listener != null)
					listener.onOk();
			}
		});
		TextView tv_no = (TextView) dialog.findViewById(R.id.motorcade_tv_no);
		if(TextUtils.isEmpty(cancelText))
		{
			tv_no.setVisibility(View.GONE);
			View h_line = dialog.findViewById(R.id.motorcade_dialog_h_line);
			h_line.setVisibility(View.GONE);
		}
		else
		{
			tv_no.setText(cancelText);
		}
		tv_no.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				if(listener != null)
					listener.onCancel();
			}
		});
		if (!dialog.isShowing())
			dialog.show();
		*/
    }

    /**
     * 由框架获取的上下文
     */
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 有新版本更新时，会被调用
     */
    @Override
    public void onNewVersionUpdate(final VersionInfo vi) {
    }

    /**
     * 应用将要退出时，会被调用
     */
    @Override
    public void onRelease() {
        super.onRelease();
    }

    /**
     * 当Activity界面初始化完毕后
     * 系统进入初始化前被调用
     */
    @Override
    public void appWillEnterBackgroundInit(int flag) {
        Log.e(TAG, "appWillEnterBackgroundInit");
    }

    /**
     * 设置是否需要等待进入后台初始化
     * 如果存在一些比较耗时的初始化时
     * 将此方法打开
     * 并在appDidEnterBackgroundInit中进行初始化工作
     * 等待进入后台初始化时
     * 需要在等待结束后调用doEnterBackgroundInit进行初始化的完成工作
     */
    @Override
    public boolean waitEnterBackgroundInit(int flag) {
        Log.e(TAG, "waitEnterBackgroundInit");
        return false;
    }

    /**
     * 当waitEnterBackgroundInit返回false时
     * 该方法将会被调用
     * 一些比较耗时的初始化工作将在此进行
     */
    @Override
    public void appDidEnterBackgroundInit(int flag) {
        Log.e(TAG, "appDidEnterBackgroundInit");
    }

    /**
     * 初始化完成，可以进行界面切换
     */
    @Override
    public void onFinishedInit(int flag) {
        Log.e(TAG, "onFinishedInit");
        mMainController.onResume(flag);
    }
}
