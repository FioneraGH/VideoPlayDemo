package com.fionera.videoplayerdemo;

import android.support.annotation.DrawableRes;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

public class OnlineVideoListItem extends VideoListItem {

    private final String mOnlineUrl;

    public OnlineVideoListItem(
            VideoPlayerManager<MetaData> videoPlayerManager,
            String title,
            @DrawableRes int imageResource,
            String onlineUrl
    ) {
        super(videoPlayerManager, title, imageResource);

        mOnlineUrl = onlineUrl;
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mOnlineUrl);
    }
}
