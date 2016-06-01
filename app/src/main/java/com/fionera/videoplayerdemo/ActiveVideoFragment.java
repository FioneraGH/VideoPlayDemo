package com.fionera.videoplayerdemo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;

import java.util.ArrayList;

public class ActiveVideoFragment
        extends Fragment {

    private AutoRecyclerView recyclerView; // 列表视图

    private final ArrayList<VideoListItem> mList; // 视频项的列表
    private final ListItemsVisibilityCalculator mVisibilityCalculator; // 可视估计器
    private final VideoPlayerManager<MetaData> mVideoPlayerManager;

    private ActiveLinearLayoutManager mLayoutManager; // 布局管理器
    private ItemsPositionGetter mItemsPositionGetter; // 位置提取器
    private int mScrollState = AutoRecyclerView.SCROLL_STATE_IDLE; // 滑动状态

    public ActiveVideoFragment() {
        mList = new ArrayList<>();
        mVisibilityCalculator = new SingleListViewItemActiveCalculator(
                new DefaultSingleItemCalculatorCallback(), mList);
        mVideoPlayerManager = new SingleVideoPlayerManager(metaData -> {
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_active_video, container, false);
        recyclerView = (AutoRecyclerView) view.findViewById(R.id.rv_active_video);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initOnlineVideoList();

        recyclerView.setHasFixedSize(true);

        mLayoutManager = new ActiveLinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(new ActiveVideoAdapter(mList));

        recyclerView.loadMoreComplete(true);

        mItemsPositionGetter = new RecyclerViewPositionGetterFix(mLayoutManager, recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    mVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, mLayoutManager
                            .findFirstVisibleItemPosition(), mLayoutManager
                                                                    .findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mList.isEmpty()) {
                    mVisibilityCalculator.onScroll(mItemsPositionGetter,
                                                   mLayoutManager.findFirstVisibleItemPosition(),
                                                   mLayoutManager
                                                           .findLastVisibleItemPosition() -
                                                           mLayoutManager
                                                           .findFirstVisibleItemPosition() + 1,
                                                   mScrollState);
                }
            }
        });
    }

    // 初始化在线视频, 需要缓冲
    private void initOnlineVideoList() {
        final int count = 10;
        for (int i = 0; i < count; ++i) {
            mList.add(new OnlineVideoListItem(mVideoPlayerManager, "测试", R.mipmap.ic_launcher,
                                              "http://dn-chunyu.qbox" + "" +
                                                      ".me/fwb/static/images/home/video/video_aboutCY_A.mp4"));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            mVideoPlayerManager.resetMediaPlayer();
        } else {
            recyclerView.post(() -> {
                if (!mList.isEmpty()) {
                    mVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, mLayoutManager
                            .findFirstVisibleItemPosition(), mLayoutManager
                                                                    .findLastVisibleItemPosition());
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (((MainActivity) getContext())
                .getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mLayoutManager.setCanScrollVertical(false);
        } else {
            mLayoutManager.setCanScrollVertical(true);
        }
    }
}
