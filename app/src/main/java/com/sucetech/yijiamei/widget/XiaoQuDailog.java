package com.sucetech.yijiamei.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.adapter.XiaoQuAdapter;
import com.sucetech.yijiamei.model.XiaoQuBean;
import com.sucetech.yijiamei.view.HomePage;

import java.util.ArrayList;
import java.util.List;

public class XiaoQuDailog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<XiaoQuBean> xiaoqus;
    private ListView listView;
    private XiaoQuAdapter bluthAdapter;
    private Context context;
    private HomePage homePage;
    private EditText search;

    public XiaoQuDailog(Context context, HomePage homePage) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.homePage = homePage;
        View view = LayoutInflater.from(context).inflate(R.layout.xiaoqu_layout, null);
        listView = view.findViewById(R.id.boluthList);
        view.findViewById(R.id.bluthClose).setOnClickListener(this);
        search= view.findViewById(R.id.input);
        setContentView(view);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String searText = v.getText().toString();
                    if (searText != null && !searText.equals(""))

                    return true;
                }
                return false;
            }
        });

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x); //宽度设置为屏幕宽度的0.5
        layoutParams.height = (int) (point.y); //高度设置为屏幕高度的0.5
        window.setAttributes(layoutParams);
        xiaoqus = Configs.xiaoQuBeanList;
        bluthAdapter = new XiaoQuAdapter(context, xiaoqus);
        listView.setAdapter(bluthAdapter);
        listView.setOnItemClickListener(this);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
//        for (int i = 0; i < 5; i++) {
//            XiaoQuBean xiaoQuBean = new XiaoQuBean();
//            xiaoQuBean.name = "欢腾小区" + i;
//            xiaoqus.add(xiaoQuBean);
//        }
        bluthAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        homePage.XiaoQuBean = xiaoqus.get(position);
        dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode,  KeyEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bluthClose) {
            if (homePage.XiaoQuBean != null && !homePage.XiaoQuBean.equals("")) {
                dismiss();
            } else {
                Toast.makeText(context,"请选择小区",Toast.LENGTH_LONG).show();
            }
        }
    }
}
