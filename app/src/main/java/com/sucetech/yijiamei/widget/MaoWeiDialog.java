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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.adapter.LajiFenLeiAdapter;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.LaJiBean;
import com.sucetech.yijiamei.view.HomePage;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import static com.sucetech.yijiamei.provider.BluthConnectTool.BluthStutaListener.weied;

/**
 * Created by lihh on 2018/12/20.
 */

public class MaoWeiDialog extends Dialog implements View.OnClickListener,TextWatcher {
    private List<LaJiBean> lajis;
    private Context mcontext;
    private HomePage homePage;
    private TextView name, phone, carNub, priceStr;
    private LaJiBean laJiBean;
    private EditText weiText;
    private View priceLayout, jifenLayout;
    private TextView jifen, price;
    private String wei;
    private String Maowei;//毛重
    private double zhongliang;//净重


    public void setWei(String wei) {
        if (wei.equals(this.wei))return;
        this.wei = wei;
        if (weiText != null)
            weiText.setText(wei);
        zhongliang=(Double.parseDouble(Maowei)-Double.parseDouble(wei));
//        if (zhongliang<=0){
//            Toast.makeText(context,"皮重大于等于毛重!",Toast.LENGTH_LONG).show();
//        }
        if (laJiBean.rewardsMode.equals("Money")){
            priceLayout.setVisibility(View.VISIBLE);
            jifenLayout.setVisibility(View.GONE);
            if(laJiBean.money>0&&wei!=null){
                BigDecimal bg = new BigDecimal(laJiBean.money*zhongliang);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                price.setText(f1+"元");
            }
        }else if (laJiBean.rewardsMode.equals("Both")){
            priceLayout.setVisibility(View.VISIBLE);
            jifenLayout.setVisibility(View.VISIBLE);
            if(laJiBean.money>0&&wei!=null){
                BigDecimal bg = new BigDecimal(laJiBean.money*zhongliang);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                price.setText(f1+"元");
            }
            if(laJiBean.score>0&&wei!=null)
                jifen.setText((int)Math.ceil(laJiBean.score*zhongliang)+"");
        }else {
            priceLayout.setVisibility(View.GONE);
            jifenLayout.setVisibility(View.VISIBLE);
            if(laJiBean.score>0&&wei!=null)
                jifen.setText((int)Math.ceil(laJiBean.score*zhongliang)+"");

        }
    }

    public MaoWeiDialog(Context context, HomePage homePage) {
        super(context, R.style.BottomDialog);
        mcontext = context;
        this.homePage = homePage;
        initData();
        View view = LayoutInflater.from(context).inflate(R.layout.second_jumin_layout, null);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        carNub = view.findViewById(R.id.car);
        name.setText(homePage.juMinBean.name);
        phone.setText(homePage.juMinBean.phone);
        carNub.setText(homePage.juMinBean.carNub);


        weiText = view.findViewById(R.id.wei);
        weiText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        priceStr = view.findViewById(R.id.priceStr);
        jifen = view.findViewById(R.id.jifen);
        price = view.findViewById(R.id.price);
        view.findViewById(R.id.cansle).setOnClickListener(this);
        view.findViewById(R.id.commit).setOnClickListener(this);
        priceLayout = view.findViewById(R.id.priceLayout);
        jifenLayout = view.findViewById(R.id.jifenLayout);
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
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        if (homePage.BluthStuta==weied){
            name.setFocusable(true);
            name.setFocusableInTouchMode(true);
            name.requestFocus();
        }else{
            weiText.addTextChangedListener(this);
            weiText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager) ((MainActivity)mcontext).getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    inputMethodManager.showSoftInput(weiText,InputMethodManager.SHOW_FORCED);
                }
            },200);
        }


    }

    private JSONObject data;

    private void initData() {

        try {
            data = new JSONObject(UserMsg.getPizhongByCarId(homePage.juMinBean.carNub));
            Maowei=data.getString("weight");

            int lajiId = data.getInt("recycleTypeId");
            for (int i = 0; i < homePage.data.size(); i++) {
                if (homePage.data.get(i).id == lajiId) {
                    laJiBean = homePage.data.get(i);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        } else if (v.getId() == R.id.commit) {
            if (laJiBean==null||wei==null||wei.equals("")){
                Toast.makeText(mcontext,"请输入重量",Toast.LENGTH_LONG).show();
                return;
            }
            zhongliang=(Double.parseDouble(Maowei)-Double.parseDouble(wei));
            if (zhongliang<=0){
                Toast.makeText(mcontext,"毛重应大于皮重!",Toast.LENGTH_LONG).show();
           return;
            }
            if(data!=null){
                try {
                    data.put("weight",zhongliang+"");
                    if (laJiBean.rewardsMode.equals("Money")) {
                            BigDecimal bg = new BigDecimal(laJiBean.money*zhongliang);
                            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            data.put("money",f1 + "");
                    } else if (laJiBean.rewardsMode.equals("Both")) {
                        data.put("score",(int)Math.ceil(laJiBean.score*zhongliang)+"");
                        BigDecimal bg = new BigDecimal(laJiBean.money*zhongliang);
                        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        data.put("money",f1 + "");
                    } else {
                        data.put("score",(int)Math.ceil(laJiBean.score*zhongliang)+"");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            homePage.willCommit(null, data);
            dismiss();
        }
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
        if (wei!=null&&!wei.equals("")) {
            setWei(wei);
        }
    }
}
