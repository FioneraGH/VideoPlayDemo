package com.fionera.videoplayerdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import java.util.List;

public class ActiveVideoAdapter
        extends RecyclerView.Adapter<VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<VideoListItem> mList;
    private final Context mContext;

    public ActiveVideoAdapter(VideoPlayerManager videoPlayerManager, Context context,
                              List<VideoListItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        VideoListItem videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup,
                mContext.getResources().getDisplayMetrics().widthPixels);
        return (VideoViewHolder) resultView.getTag();
    }

    @Override
    public void onBindViewHolder(VideoViewHolder viewHolder, int position) {
        VideoListItem videoItem = mList.get(position);
        videoItem.update(position, viewHolder, mVideoPlayerManager);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
