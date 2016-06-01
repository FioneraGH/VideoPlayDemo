package com.fionera.videoplayerdemo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.List;

import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.LoadListener;
import me.xiaopan.sketch.Sketch;

public class ActiveVideoAdapter
        extends RecyclerView.Adapter<ActiveVideoAdapter.VideoViewHolder> {

    private final List<VideoListItem> mList; // 视频项列表

    // 构造器
    public ActiveVideoAdapter(List<VideoListItem> list) {
        mList = list;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_active_video_item, parent, false);

        // 必须要设置Tag, 否则无法显示
        VideoViewHolder holder = new VideoViewHolder(view);
        view.setTag(holder);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        VideoListItem videoItem = mList.get(position);
        holder.bindTo(videoItem);
        holder.setIsRecyclable(false);
    }

    @Override public int getItemCount() {
        return mList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoPlayerView mVpvPlayer; // 播放控件
        ImageView mIvCover; // 覆盖层
        TextView mTvTitle; // 标题
        TextView mTvPercents; // 百分比

        public VideoPlayerView getVpvPlayer() {
            return mVpvPlayer;
        }

        public ImageView getIvCover() {
            return mIvCover;
        }

        public TextView getTvTitle() {
            return mTvTitle;
        }

        public TextView getTvPercents() {
            return mTvPercents;
        }

        private Context mContext;
        private MediaPlayerWrapper.MainThreadMediaPlayerListener mPlayerListener;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mVpvPlayer = (VideoPlayerView) itemView.findViewById(R.id.item_video_vpv_player);
            mIvCover = (ImageView) itemView.findViewById(R.id.item_video_iv_cover);
            mTvTitle = (TextView) itemView.findViewById(R.id.item_video_tv_title);
            mTvPercents = (TextView) itemView.findViewById(R.id.item_video_tv_percents);

            mContext = itemView.getContext().getApplicationContext();
            mPlayerListener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
                @Override
                public void onVideoSizeChangedMainThread(int width, int height) {
                }

                @Override
                public void onVideoPreparedMainThread() {
                    // 视频播放隐藏前图
                    mIvCover.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                }

                @Override
                public void onErrorMainThread(int what, int extra) {
                }

                @Override
                public void onBufferingUpdateMainThread(int percent) {
                }

                @Override
                public void onVideoStoppedMainThread() {
                    // 视频暂停显示前图
                    mIvCover.setVisibility(View.VISIBLE);
                }
            };

            mVpvPlayer.addMediaPlayerListener(mPlayerListener);
        }

        public void bindTo(VideoListItem vli) {
            mTvTitle.setText(vli.getTitle());
            mIvCover.setVisibility(View.VISIBLE);
            Sketch.with(mContext).loadFromResource(vli.getImageResource(), new LoadListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted(Drawable drawable, ImageFrom imageFrom, String mimeType) {
                    mIvCover.setImageDrawable(drawable);
                }

                @Override
                public void onFailed(FailCause failCause) {

                }

                @Override
                public void onCanceled(CancelCause cancelCause) {

                }
            }).commit();
        }
    }
}
