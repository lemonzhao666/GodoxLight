package com.zlm.base.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.LogUtils;
import com.zlm.base.PublicUtil;
import com.zlm.base.R;
import android.widget.VideoView;

public class PlayView extends FrameLayout  {

    private ImageButton ibtnPlay;
    private StatusButtom statusButtom;
    private TextView tvProgress;
    private CompatSeekbar sbProgress;
    private TextView tvTotalTime;
    private VideoView videoView;
    private Handler handler = new Handler();
    PlayStatus currentState = PlayStatus.IDLE;
    private Boolean hasSource = false;
    private Boolean isSeeking =  false;
    public PlayView(@NonNull Context context) {
        this(context, null);
    }

    public PlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_palyview, this);
        videoView = view.findViewById(R.id.videoView);
        ibtnPlay = view.findViewById(R.id.ibtnPlay);
        statusButtom = view.findViewById(R.id.ibPlay);
        tvProgress = view.findViewById(R.id.tvProgress);
        sbProgress = view.findViewById(R.id.sbProgress);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        init();
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            tvProgress.setText(PublicUtil.stringToTime(videoView.getCurrentPosition()));
            sbProgress.setProgress(videoView.getCurrentPosition());
            handler.postDelayed(this, 50);
        }
    };


    private void init() {
        statusButtom.setBgBackground(new Integer[]{R.mipmap.play, R.mipmap.pause});
        statusButtom.setBtnStatusListener(new StatusButtom.StatusListener() {
            @Override
            public void status(boolean status) {
                switch (currentState) {
                    case IDLE:
                        videoView.start();
                        currentState = PlayStatus.PLAYING;
                        handler.post(run);
                        ibtnPlay.setVisibility(GONE);
                        break;
                    case PAUSE:
                        videoView.start();
                        currentState = PlayStatus.PLAYING;
                        handler.post(run);
                        ibtnPlay.setVisibility(GONE);
                        break;
                    case PLAYING:
                        videoView.pause();
                        currentState = PlayStatus.PAUSE;
                        handler.removeCallbacks(run);
                        ibtnPlay.setVisibility(VISIBLE);
                        break;
                }
            }
        });
        ibtnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
                currentState = PlayStatus.PLAYING;
                handler.post(run);
                ibtnPlay.setVisibility(GONE);
                statusButtom.setBtnStatus(true);
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtils.dTag("videoView.getDuration() = "+videoView.getDuration());
                sbProgress.setMax(videoView.getDuration());
                tvTotalTime.setText(PublicUtil.stringToTime(videoView.getDuration()));
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentState = PlayStatus.IDLE;
                tvProgress.setText(PublicUtil.stringToTime(videoView.getDuration()));
                ibtnPlay.setVisibility(VISIBLE);
                handler.removeCallbacks(run);
                statusButtom.setBtnStatus(false);
            }
        });

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isSeeking && hasSource) {
                    videoView.seekTo(progress);
                    tvProgress.setText(PublicUtil.stringToTime(videoView.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
                ibtnPlay.setVisibility(View.GONE);
                handler.removeCallbacks(run);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                if(!videoView.isPlaying()){
                    ibtnPlay.setVisibility(View.VISIBLE);
                }
                handler.post(run);
            }
        });
    }

    public void setDataPath(String path) {
        videoView.setVideoPath(path);
        videoView.seekTo(20);
        currentState = PlayStatus.IDLE;
        hasSource = true;

    }
    public void releasePlayer(){
        videoView.seekTo(20);
        sbProgress.setProgress(0);
        statusButtom.setBtnStatus(false);
        ibtnPlay.setVisibility(VISIBLE);
        tvProgress.setText(PublicUtil.stringToTime(0));
        handler.removeCallbacks(run);
        videoView.stopPlayback();
        currentState = PlayStatus.IDLE;

    }

    public boolean isPlaying() {
       return videoView.isPlaying();
    }
    public void pause(){
        videoView.pause();
        currentState = PlayStatus.PAUSE;
        handler.removeCallbacks(run);
        ibtnPlay.setVisibility(VISIBLE);
        statusButtom.setBtnStatus(false);
    }

    enum PlayStatus {
        PLAYING, PAUSE, IDLE
    }
}
