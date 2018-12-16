package com.sucetech.yijiamei.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.scale.ScaleLinearLayout;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.view.HomePage;

public class CommitView  extends ScaleLinearLayout implements View.OnClickListener {
    private View camore, voice, commit;
    private HomePage homePage;
    private TextView name, phone, carNub,commitMsg,lajiType,wei;
    private ImageView img;

    public CommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view= LayoutInflater.from(context).inflate(R.layout.commit_layout,null);
        camore = view.findViewById(R.id.bottom01);
        voice = view.findViewById(R.id.bottom02);
        commit = view.findViewById(R.id.bottom03);
        img=view.findViewById(R.id.img);
        camore.setOnClickListener(this);
        voice.setOnClickListener(this);
        commit.setOnClickListener(this);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        carNub = view.findViewById(R.id.car);
        commitMsg=view.findViewById(R.id.commitMsg);
        lajiType=view.findViewById(R.id.lajiType);
        wei=view.findViewById(R.id.wei);
        this.addView(view,-1,-1);
    }
    public CommitView(Context context) {
        super(context);

    }

    public void setHomePage(HomePage homePage){
        this.homePage=homePage;
    }
    public void showWillCommit(CommitLajiBean commitLajiBean){
        name.setText(homePage.juMinBean.name);
        phone.setText(homePage.juMinBean.phone);
        carNub.setText(homePage.juMinBean.carNub);
        lajiType.setText(commitLajiBean.lajiName);
        wei.setText(commitLajiBean.wei+"");
        if (commitLajiBean.isMoney){
            commitMsg.setText("本次称重可以获得"+commitLajiBean.price+"元现金");
        }else{
            commitMsg.setText("本次称重可以获得"+commitLajiBean.price+"积分");
        }
    }
    public void showImg(FormImage formImage){
        if (formImage!=null){
            img.setImageBitmap(formImage.mBitmap);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom01:
                ((MainActivity) getContext()).requestPicture(R.id.bottom01);
                break;
            case R.id.bottom02:
                break;
            case R.id.bottom03:
                break;
        }

    }
}
