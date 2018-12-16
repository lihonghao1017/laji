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
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.view.HomePage;

import java.io.IOException;

public class CommitView extends ScaleLinearLayout implements View.OnClickListener {
    private View camore, commit;
    private RecordButton mRecordButton;
    private HomePage homePage;
    private TextView name, phone, carNub, commitMsg, lajiType, wei;
    private ImageView img, voice;
    private String audioPath;
    private AnimationDrawable animationDrawable;
    private MediaPlayer mediaPlayer;


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
        name.setText(homePage.juMinBean.name);
        phone.setText(homePage.juMinBean.phone);
        carNub.setText(homePage.juMinBean.carNub);
        lajiType.setText(commitLajiBean.lajiName);
        wei.setText(commitLajiBean.wei + "");
        if (commitLajiBean.isMoney) {
            commitMsg.setText("本次称重可以获得" + commitLajiBean.price + "元现金");
        } else {
            commitMsg.setText("本次称重可以获得" + commitLajiBean.price + "积分");
        }
    }

    public void showImg(FormImage formImage) {
        if (formImage != null) {
            img.setImageBitmap(formImage.mBitmap);
        }
    }

    private void startPlay() {
        try {
            //设置播放监听事件
            if (mediaPlayer == null&&audioPath!=null) {
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
                ((MainActivity) getContext()).requestPicture(R.id.bottom01);
                break;
            case R.id.bottom02:
                break;
            case R.id.bottom03:
                break;
            case R.id.voice:
                playAudio();
                break;
        }

    }
}
