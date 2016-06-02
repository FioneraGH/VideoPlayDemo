package com.fionera.videoplayerdemo;

import android.app.Activity;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

public class ItemFactory {

    public static VideoListItem createItemFromUrl(String title, String url, int imageResource,
                                                  Activity activity,
                                                  VideoPlayerManager<MetaData> videoPlayerManager){
        return new OnlineVideoListItem(title, url, videoPlayerManager, Picasso.with(activity),
                                       imageResource);
    }
}
