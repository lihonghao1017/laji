package com.sucetech.yijiamei.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.scale.ScaleLinearLayout;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.provider.FileUtils;
import com.sucetech.yijiamei.provider.PhontoUtils;
import com.sucetech.yijiamei.provider.TaskManager;
import com.sucetech.yijiamei.view.HomePage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommitView extends ScaleLinearLayout implements View.OnClickListener {
    private View camore, commit;
    private RecordButton mRecordButton;
    private HomePage homePage;
    private TextView name, phone, carNub, commitMsg, lajiType, wei;
    private ImageView img, voice;
    private String audioPath;
    private MediaPlayer mediaPlayer;
    private CommitLajiBean mCommitLajiBean;


    public CommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.commit_layout, null);
        camore = view.findViewById(R.id.bottom01);
        mRecordButton = view.findViewById(R.id.yuyinIcon);
        commit = view.findViewById(R.id.bottom03);
        img = view.findViewById(R.id.img);
        camore.setOnClickListener(this);
        voice = view.findViewById(R.id.voice);
        voice.setOnClickListener(this);
        voice.setImageResource(R.drawable.sound_anim);
        mRecordButton.setAudioRecord(new AudioRecorder());
        //语音发送的回调
        mRecordButton.setRecordListener(new RecordButton.RecordListener() {
            @Override
            public void recordEnd(String filePath, float time) {
                audioPath = filePath;
                voice.setVisibility(View.VISIBLE);
            }
        });
        commit.setOnClickListener(this);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        carNub = view.findViewById(R.id.car);
        commitMsg = view.findViewById(R.id.commitMsg);
        lajiType = view.findViewById(R.id.lajiType);
        wei = view.findViewById(R.id.wei);
        this.addView(view, -1, -1);
    }

    public CommitView(Context context) {
        super(context);

    }

    public void setHomePage(HomePage homePage) {
        this.homePage = homePage;
    }

    public void showWillCommit(CommitLajiBean commitLajiBean) {
        this.mCommitLajiBean=commitLajiBean;
        name.setText(homePage.juMinBean.name);
        phone.setText(homePage.juMinBean.phone);
        carNub.setText(homePage.juMinBean.carNub);
        lajiType.setText(commitLajiBean.lajiName);
        wei.setText(commitLajiBean.wei + "");
        if (commitLajiBean.type.equals("Money")) {
            commitMsg.setText("本次称重可以获得" + commitLajiBean.price + "元现金");
        } else if (commitLajiBean.type.equals("Both")) {
            commitMsg.setText("本次称重可以获得" + commitLajiBean.price + "元现金," + commitLajiBean.price + "积分");
        } else {
            commitMsg.setText("本次称重可以获得" + commitLajiBean.jifen + "积分");
        }
    }

    public void showImg(FormImage formImage) {
        if (formImage != null) {
            img.setImageBitmap(formImage.mBitmap);
            img.setTag(formImage);
        }
    }

    private void startPlay() {
        try {
            //设置播放监听事件
            if (mediaPlayer == null && audioPath != null) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();
                    int du = mediaPlayer.getDuration();
                } catch (IOException e) {
                    e.printStackTrace();
                    mediaPlayer = null;
                }
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放完成
                    playEndOrFail();
                }
            });


            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
//            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            //播放失败正理
            playEndOrFail();
        }

    }

    private volatile boolean isPlaying;

    private void playEndOrFail() {
        isPlaying = false;
        ((AnimationDrawable) voice.getDrawable()).stop();
        if (null != mediaPlayer) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playAudio() {
        if (!isPlaying) {
            isPlaying = true;
            ((AnimationDrawable) voice.getDrawable()).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPlay();
                }
            }).start();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom01:
//                PhontoUtils.take_photo(((MainActivity) getContext()));
                ((MainActivity) getContext()).requestPicture(R.id.bottom01);
                break;
            case R.id.bottom02:
                break;
            case R.id.bottom03:
                TaskManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        upLoadFile();
                    }
                });

                break;
            case R.id.voice:
                playAudio();
                break;
        }

    }
    private void upLoadFile(){
//        RequestBody requestBody1 = RequestBody.create(MediaType.get("application/json"), data);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
//        builder.addFormDataPart("data", null, requestBody1);
        if (img != null && img.getTag() != null ) {
            FormImage formImage = (FormImage) img.getTag();
            builder.addFormDataPart("data", formImage.mFileName,
                    RequestBody.create(MediaType.get("image/jpg"), FileUtils.getFile(formImage.mFileName)));
        }
        if (audioPath != null) {
                File audioFile = new File(audioPath);
                if (audioFile.exists()) {
                    builder.addFormDataPart("data", audioFile.getName(),
                            RequestBody.create(MediaType.get("audio/amr"), FileUtils.getFile(audioPath)));
                }
        }

        String url = Configs.baseUrl + ":8081/datong/v1/upload";
        Request request = new Request.Builder()
                .url(url)
//                .header("Authorization", UserMsg.getToken())
                .post(builder.build())
                .build();
        try {
            Response response = ((MainActivity) getContext()).client.newCall(request).execute();
            if(response.isSuccessful()){
                String rrr=response.body().string().replace("\"", "");
                JSONObject json=creatJson(rrr);
                Log.e("LLL","response--json->"+json.toString());
                sendData(json);
            }else{
                Log.e("LLL","response--111->"+response.body().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject creatJson(String files) {
        JSONObject rootJson = new JSONObject();
        try {
            String[]  fifi=files.split(",");
            if (fifi!=null&&fifi.length>0){

                if (fifi[0].contains(".jpg")){
                    rootJson.put("image", fifi[0]);
                }
                if (fifi.length>1){
                    rootJson.put("audio", fifi[1]);
                }
            }

//            rootJson.put("audio", "");
//            rootJson.put("image", "");
            rootJson.put("description", "diyici");
            rootJson.put("id", 0);
            rootJson.put("money", mCommitLajiBean.price!=null?mCommitLajiBean.price:"0");
            rootJson.put("recycleTypeId", mCommitLajiBean.laJiBean.id);
            rootJson.put("residentsId", 0);
            rootJson.put("score", mCommitLajiBean.jifen!=null?mCommitLajiBean.jifen:"0");
            rootJson.put("weight", mCommitLajiBean.wei);

//            JSONObject typeObj=new JSONObject();
//
//            typeObj.put("id",mCommitLajiBean.laJiBean.id);
//            typeObj.put("money",mCommitLajiBean.laJiBean.money);
//            typeObj.put("name",mCommitLajiBean.laJiBean.name);
//            typeObj.put("recycleMode",mCommitLajiBean.laJiBean.recycleMode);
//            typeObj.put("rewardsMode",mCommitLajiBean.laJiBean.rewardsMode);
//            typeObj.put("score",mCommitLajiBean.laJiBean.score);
//
//            rootJson.put("recycleType", typeObj);

            return rootJson;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendData(JSONObject json){

        RequestBody body = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(Configs.baseUrl+":8081/datong/v1/recycle")
                .post(body)
                .build();
        try {
            final Response response = ((MainActivity) getContext()).client.newCall(request).execute();
            if (response.isSuccessful()) {

                Log.e("LLL", "ok--->"+response.body().string());

//                UserMsg.saveToken(response.header("Authorization"));
//                this.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((MainActivity)getContext()).hideProgressDailogView();
//                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        LoginView.this.setVisibility(View.GONE);
////                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        Toast.makeText(getContext(),"chengong -->",Toast.LENGTH_LONG);
//                    }
//                });
//
            } else {
                Log.e("LLL", "shibai--->");

//                Toast.makeText(getContext(),"shibai -->"+response.message(),Toast.LENGTH_LONG);
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LLL", "IOException--->"+e.toString());
        }
    }
}
