package com.fionera.videoplayerdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fionera.videoplayerdemo.ijkplayer.OnCustomBufferingListener;
import com.fionera.videoplayerdemo.ijkplayer.VideoView;
import com.fionera.videoplayerdemo.util.AcFunDanmakuParser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SimpleTextCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayActivity
        extends AppCompatActivity
        implements OnCustomBufferingListener {

    private IDanmakuView mDanmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;
    private VideoView mVideoView;
    private View mMediaController;
    private View mBufferingIndicator;
    private TextView tvTips;
    private View recyclerView;

    private ImageView ivPlayerFinish;
    private TextView tvPlayerTitle;
    private ImageView ivPlayerCollect;
    private TextView tvPlayerTime;
    private AppCompatSeekBar spPlayerProcess;
    private ImageView ivPlayerPause;
    private ImageView ivPlayerFullscreen;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        mContext = this;

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        init();
    }

    private void init() {

        // VideoView
        mVideoView = (VideoView) findViewById(R.id.videoview);
        // DanmakuView
        mDanmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
        // Controller
        mMediaController = findViewById(R.id.player_controller);
        mMediaController.setOnClickListener(v -> mMediaController.setVisibility(View.GONE));
        mMediaController.setVisibility(View.GONE);
        mBufferingIndicator = findViewById(R.id.buffering_indicator);

        ivPlayerFinish = (ImageView) findViewById(R.id.iv_player_finish);
        ivPlayerFinish.setOnClickListener(v -> dealWithBack());
        tvPlayerTitle = (TextView) findViewById(R.id.tv_player_title);
        tvPlayerTitle.setText("这是标题");
        ivPlayerCollect = (ImageView) findViewById(R.id.iv_player_collect);
        ivPlayerCollect.setOnClickListener(v -> System.out.println("功能"));
        ivPlayerPause = (ImageView) findViewById(R.id.iv_player_pause);
        ivPlayerPause.setOnClickListener(v -> togglePlay());
        ivPlayerFullscreen = (ImageView) findViewById(R.id.iv_player_fullscreen);
        ivPlayerFullscreen.setOnClickListener(v -> toggleFullscreen());
        tvPlayerTime = (TextView) findViewById(R.id.tv_player_time);
        spPlayerProcess = (AppCompatSeekBar) findViewById(R.id.sp_player_process);
        spPlayerProcess.setEnabled(true);
        spPlayerProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final long releasePosition = (mVideoView.getDuration() * seekBar
                        .getProgress()) / 100;
                mVideoView.seekTo(releasePosition);
            }
        });
        tvTips = (TextView) findViewById(R.id.tv_video_play_tips);
        recyclerView = findViewById(R.id.rv_video_play_recommand);

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        danmakuContext = DanmakuContext.create();
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SimpleTextCacheStuffer(), null).setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        if (mDanmakuView != null) {
            mParser = createParser(null);
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
                @Override
                public boolean onDanmakuClick(IDanmakus iDanmakus) {
                    return false;
                }

                @Override
                public boolean onViewClick(IDanmakuView iDanmakuView) {
                    return false;
                }
            });
            mDanmakuView.prepare(mParser, danmakuContext);
            mDanmakuView.showFPS(false);
            mDanmakuView.enableDanmakuDrawingCache(true);
            ((View) mDanmakuView)
                    .setOnClickListener(view -> mMediaController.setVisibility(View.VISIBLE));
        }

        if (mVideoView != null) {
            mVideoView.setUserAgent("Shenyou Android Client/1.0.0 (fionera@outlook.com)");
            mVideoView.setVideoPath("http://devimages.apple.com.edgekey" + "" +
                                            ".net/streaming/examples/bipbop_4x3/gear1/prog_index" +
                                            ".m3u8");
            mVideoView.setMediaController(null);
            mVideoView.setOnCustomBufferingListener(this);
            mVideoView.setOnBufferingUpdateListener(
                    (mp1, percent) -> spPlayerProcess.setSecondaryProgress(percent));
            mVideoView.setMediaBufferingIndicator(mBufferingIndicator);
            mVideoView.setOnPreparedListener(mp -> {
                if (mDanmakuView != null) {
                    mBufferingIndicator.setVisibility(View.GONE);
                    ivPlayerPause.setImageResource(android.R.drawable.ic_media_pause);
                    mDanmakuView.start();
                    asyncTimer = new Timer();
                    asyncTimer.schedule(new AsyncAddTask(), 1000, 1000);
                    fetchProgressTimer = new Timer();
                    fetchProgressTimer.schedule(new FetchProgressTask(), 1000, 1000);
                    mp.start();
                    mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
                }
            });
            mVideoView.setOnCompletionListener(mp -> {
                mDanmakuView.stop();
                mBufferingIndicator.setVisibility(View.GONE);
                new AlertDialog.Builder(mContext).setMessage("播放完毕，是否退出")
                        .setPositiveButton("是", (dialog, which) -> finish())
                        .setNegativeButton("否", null).show();
            });
            mVideoView.requestFocus();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            tvTips.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvTips.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        /*
          reset to fit the new size , insure the method is correct
         */
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        togglePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        togglePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        IjkMediaPlayer.native_profileEnd();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (asyncTimer != null) {
            asyncTimer.cancel();
            asyncTimer = null;
        }
        if (fetchProgressTimer != null) {
            fetchProgressTimer.cancel();
            fetchProgressTimer = null;
        }
    }

    @Override
    public void onBackPressed() {
        dealWithBack();
    }

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_ACFUN);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new AcFunDanmakuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private Timer asyncTimer;
    private Timer fetchProgressTimer;

    private class AsyncAddTask
            extends TimerTask {

        @Override
        public void run() {
            System.out.println("tap");
            addDanmaku(true);
        }
    }

    private class FetchProgressTask
            extends TimerTask {

        @Override
        public void run() {
            final int currentProgress = mVideoView.getCurrentPosition() * 100 / mVideoView
                    .getDuration();
            spPlayerProcess.setProgress(currentProgress);

            tvPlayerTime.post(() -> tvPlayerTime.setText(
                    generateTime(mVideoView.getCurrentPosition()) + "/" + generateTime(
                            mVideoView.getDuration())));
        }
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds);
        }
    }

    private void addDanmaku(boolean isLive) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory
                .createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = "这是一条新添加的弹幕" + System.nanoTime();
        danmaku.padding = 5;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = isLive;
        danmaku.timeOffset = mDanmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.BLACK;
        danmaku.borderColor = 0;
        mDanmakuView.addDanmaku(danmaku);
    }

    private void togglePlay() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            if (mVideoView.isPlaying()) {
                mDanmakuView.pause();
                mVideoView.pause();
                ivPlayerPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mDanmakuView.resume();
                mVideoView.resume();
                ivPlayerPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    private void toggleFullscreen() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (tvTips.isShown()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
        }
    }

    private void dealWithBack() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            toggleFullscreen();
        } else {
            finish();
        }
    }

    @Override
    public void onBufferingStart() {
        mDanmakuView.pause();
        ivPlayerPause.setEnabled(false);
        ivPlayerPause.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onBufferingStop() {
        mDanmakuView.resume();
        ivPlayerPause.setEnabled(true);
        ivPlayerPause.setImageResource(android.R.drawable.ic_media_pause);
    }
}
