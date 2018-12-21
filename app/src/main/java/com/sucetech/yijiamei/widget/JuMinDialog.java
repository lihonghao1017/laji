package com.sucetech.yijiamei.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JuMinDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener ,TextWatcher {
    private List<LaJiBean> lajis;
    private GridView lajiGridView;
    private LajiFenLeiAdapter bluthAdapter;
    private Context context;
    private HomePage homePage;
    private TextView name, phone, carNub,priceStr;
    private LaJiBean laJiBean;
    private EditText weiText;
    private View priceLayout,jifenLayout;
    private TextView jifen,price;
    private String wei;
    public void setWei(String wei){
        this.wei=wei;
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
        weiText.addTextChangedListener(this);
        weiText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        priceStr=view.findViewById(R.id.priceStr);
        jifen=view.findViewById(R.id.jifen);
        price=view.findViewById(R.id.price);
        view.findViewById(R.id.cansle).setOnClickListener(this);
        view.findViewById(R.id.commit).setOnClickListener(this);
        priceLayout=view.findViewById(R.id.priceLayout);
        jifenLayout=view.findViewById(R.id.jifenLayout);
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
        lajis = homePage.data;
        bluthAdapter = new LajiFenLeiAdapter(context, lajis);
        lajiGridView.setAdapter(bluthAdapter);
        lajiGridView.setOnItemClickListener(this);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        bluthAdapter.notifyDataSetChanged();
    }

private int position=-1;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position=position;
        for (int i = 0; i < lajis.size(); i++) {
            lajis.get(i).isSelected = false;
        }
        laJiBean=lajis.get(position);
        laJiBean.isSelected = true;
        bluthAdapter.notifyDataSetChanged();
        if (laJiBean.rewardsMode==null){
            Toast.makeText(context,"数据异常",Toast.LENGTH_LONG).show();
            return;
        }
        if (laJiBean.rewardsMode.equals("Money")){
            priceLayout.setVisibility(View.VISIBLE);
            jifenLayout.setVisibility(View.GONE);
            if(laJiBean.money>0&&wei!=null)
            price.setText(laJiBean.money*Integer.parseInt(wei)+"元");
        }else if (laJiBean.rewardsMode.equals("Both")){
            priceLayout.setVisibility(View.VISIBLE);
            jifenLayout.setVisibility(View.VISIBLE);
            if(laJiBean.money>0&&wei!=null)
                price.setText(laJiBean.money*Integer.parseInt(wei)+"元");
            if(laJiBean.score>0&&wei!=null)
                jifen.setText(laJiBean.score*Integer.parseInt(wei)+"");
        }else {
            priceLayout.setVisibility(View.GONE);
            jifenLayout.setVisibility(View.VISIBLE);
            if(laJiBean.score>0&&wei!=null)
                jifen.setText(laJiBean.score*Integer.parseInt(wei)+"");

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
            if (laJiBean==null||wei==null){
                Toast.makeText(context,"请选择垃圾类型",Toast.LENGTH_LONG).show();
                return;
            }
            CommitLajiBean commitLajiBean=new CommitLajiBean();
            commitLajiBean.laJiBean=laJiBean;
            commitLajiBean.wei=wei;
            commitLajiBean.lajiName=laJiBean.name;
            commitLajiBean.type=laJiBean.rewardsMode;
            if (commitLajiBean.type.equals("Money")){
                commitLajiBean.price=laJiBean.money*Integer.parseInt(wei)+"";
            }else if (commitLajiBean.type.equals("Both")){
                commitLajiBean.price=laJiBean.money*Integer.parseInt(wei)+"";
                commitLajiBean.jifen=laJiBean.score*Integer.parseInt(wei)+"";
            }else{
                commitLajiBean.jifen=laJiBean.score*Integer.parseInt(wei)+"";
            }


            homePage.willCommit(commitLajiBean,creatJson(commitLajiBean));
            dismiss();
        }
    }

    private JSONObject creatJson(CommitLajiBean commitLajiBean) {
        JSONObject rootJson = new JSONObject();
        try {
            rootJson.put("description", "diyici");
            rootJson.put("id", 0);
            rootJson.put("money", commitLajiBean.price!=null?commitLajiBean.price:"0");
            rootJson.put("recycleTypeId", commitLajiBean.laJiBean.id);
            rootJson.put("residentsId", homePage.juMinBean.carNub);
            rootJson.put("score", commitLajiBean.jifen!=null?commitLajiBean.jifen:"0");
            rootJson.put("weight", commitLajiBean.wei);
            return rootJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        wei=s.toString();
        if (this.position>-1)
        onItemClick(null,null, this.position,-1);
    }
}
