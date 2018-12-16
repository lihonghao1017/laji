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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.adapter.LajiFenLeiAdapter;
import com.sucetech.yijiamei.adapter.XiaoQuAdapter;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.LaJiBean;
import com.sucetech.yijiamei.model.XiaoQuBean;
import com.sucetech.yijiamei.view.HomePage;

import java.util.ArrayList;
import java.util.List;

public class JuMinDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<LaJiBean> lajis;
    private GridView lajiGridView;
    private LajiFenLeiAdapter bluthAdapter;
    private Context context;
    private HomePage homePage;
    private TextView name, phone, carNub,priceStr;
    private LaJiBean laJiBean;
    private TextView weiText;
    public void setWei(String wei){
        if (weiText!=null)
        weiText.setText(wei+"kg");
    }

    public JuMinDialog(Context context, HomePage homePage) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.homePage = homePage;
        View view = LayoutInflater.from(context).inflate(R.layout.jumin_dailog, null);
        lajiGridView = view.findViewById(R.id.lajiGridView);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        carNub = view.findViewById(R.id.car);
        name.setText(homePage.juMinBean.name);
        phone.setText(homePage.juMinBean.phone);
        carNub.setText(homePage.juMinBean.carNub);
        weiText= view.findViewById(R.id.wei);
        priceStr=view.findViewById(R.id.priceStr);
        view.findViewById(R.id.cansle).setOnClickListener(this);
        view.findViewById(R.id.commit).setOnClickListener(this);

//        view.findViewById(R.id.bluthClose).setOnClickListener(this);

        setContentView(view);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕宽高
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes(); //获取当前对话框的参数值
        layoutParams.width = (int) (point.x); //宽度设置为屏幕宽度的0.5
        layoutParams.height = (int) (point.y); //高度设置为屏幕高度的0.5
        window.setAttributes(layoutParams);
        lajis = new ArrayList<>();
        bluthAdapter = new LajiFenLeiAdapter(context, lajis);
        lajiGridView.setAdapter(bluthAdapter);
        lajiGridView.setOnItemClickListener(this);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        for (int i = 0; i < 5; i++) {
            LaJiBean xiaoQuBean = new LaJiBean();
            xiaoQuBean.name = "欢腾小区" + i;
            lajis.add(xiaoQuBean);
            xiaoQuBean.price=i+1;
            if (i == 2) {
                xiaoQuBean.isMoney = true;
            }
        }
        bluthAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < lajis.size(); i++) {
            lajis.get(i).isSelected = false;
        }
        laJiBean=lajis.get(position);
        laJiBean.isSelected = true;
        bluthAdapter.notifyDataSetChanged();
        if (laJiBean.isMoney){
            priceStr.setText("金额");
        }else{
            priceStr.setText("积分");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cansle) {
            dismiss();
        }else if(v.getId() == R.id.commit){
            if (laJiBean==null){
                Toast.makeText(context,"请选择垃圾类型",Toast.LENGTH_LONG).show();
                return;
            }
            CommitLajiBean commitLajiBean=new CommitLajiBean();
            commitLajiBean.lajiName=laJiBean.name;
            commitLajiBean.price=99;
            homePage.willCommit(commitLajiBean);
        }
    }
}
