package com.fionera.videoplayerdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;
import com.volokh.danylo.visibility_utils.utils.Config;

import java.util.ArrayList;

public class ActiveVideoFragment
        extends Fragment {

    private final ArrayList<VideoListItem> mList = new ArrayList<>();

    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator = new
            SingleListViewItemActiveCalculator(
            new DefaultSingleItemCalculatorCallback(), mList);

    private AutoRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ItemsPositionGetter mItemsPositionGetter;
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(
            metaData -> {
            });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));
        mList.add(ItemFactory.createItemFromUrl("title1",
                "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4",
                R.mipmap.ic_launcher, getActivity(), mVideoPlayerManager));

        View rootView = inflater.inflate(R.layout.fragment_video_recycler_view, container, false);

        mRecyclerView = (AutoRecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setLoadDataListener(() -> {
            mRecyclerView.postDelayed(() -> {
                mRecyclerView.loadMoreComplete(true);
            },2000);
        });

        ActiveVideoAdapter activeVideoAdapter = new ActiveVideoAdapter(mVideoPlayerManager,
                getContext(), mList);

        mRecyclerView.setAdapter(activeVideoAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    tryToFindViewToPlaySet();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mList.isEmpty()) {
                    mVideoVisibilityCalculator.onScroll(mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager
                                    .findFirstVisibleItemPosition() + 1, mScrollState);
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

        mRecyclerView.setRecyclerListener(holder -> {
            mVideoPlayerManager.stopAnyPlayback();
        });
        return rootView;
    }

    private void tryToFindViewToPlaySet() {
        try {
            mVideoVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter,
                    mLayoutManager.findFirstVisibleItemPosition(),
                    mLayoutManager.findLastVisibleItemPosition() == mList.size() ?
                            mLayoutManager
                            .findLastVisibleItemPosition() - 1 : mLayoutManager.findLastVisibleItemPosition());
        }catch (Exception e){
            System.out.println("index out of bounds");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!mList.isEmpty()) {
                tryToFindViewToPlaySet();
            }
        } else {
            mVideoPlayerManager.stopAnyPlayback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !mList.isEmpty()) {
            tryToFindViewToPlaySet();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoPlayerManager.stopAnyPlayback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoPlayerManager.resetMediaPlayer();
    }
}