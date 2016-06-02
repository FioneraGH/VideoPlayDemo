package com.fionera.videoplayerdemo;

import android.view.View;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * Use this class if you have direct path to the video source
 */
public class OnlineVideoListItem
        extends VideoListItem {

    private final String mDirectUrl;
    private final String mTitle;

    private final Picasso mImageLoader;
    private final int mImageResource;

    public OnlineVideoListItem(String title, String directUr, VideoPlayerManager videoPlayerManager,
                               Picasso imageLoader, int imageResource) {
        super(videoPlayerManager);
        mDirectUrl = directUr;
        mTitle = title;
        mImageLoader = imageLoader;
        mImageResource = imageResource;
    }

    @Override
    public void update(int position, VideoViewHolder viewHolder,
                       VideoPlayerManager videoPlayerManager) {
        viewHolder.mPlayer.setOnClickListener(null);
        viewHolder.mTitle.setText(mTitle);
        viewHolder.mCover.setVisibility(View.VISIBLE);
        viewHolder.mCover.setImageResource(R.mipmap.ic_launcher);
        mImageLoader.load(mImageResource).into(viewHolder.mCover);
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player,
                             VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
