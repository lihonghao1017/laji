package com.sucetech.yijiamei.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sucetech.yijiamei.R;

public class BluthDailog extends Dialog {

    public BluthDailog(Context context) {
        super(context, R.style.BottomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bluth_dailog, null);

        setContentView(view);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x * 0.5); //宽度设置为屏幕宽度的0.5
        layoutParams.height = (int) (point.y * 0.5); //高度设置为屏幕高度的0.5
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(layoutParams);
    }
}
