package com.fionera.videoplayerdemo;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class AutoRecyclerView
        extends RecyclerView
        implements Runnable {

    private Context mContext;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;

    private boolean isLoadingData = false;

    private LoadDataListener mLoadDataListener;

    public interface LoadDataListener {
        void onLoadMore();
    }

    public AutoRecyclerView(Context context) {
        this(context, null);
    }

    public AutoRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setOverScrollMode(OVER_SCROLL_NEVER);
        post(this);
    }

    @Override
    public void run() {
        LayoutManager manager = getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            layoutGridAttach((GridLayoutManager) manager);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            layoutStaggeredGridHeadAttach((StaggeredGridLayoutManager) manager);
        }
        if (mAdapter != null) {
            if (((WrapAdapter) mAdapter).getFootersCount() > 0) {
                mFootViews.get(0).setVisibility(GONE);
            }
        }
    }

    public void addHeaderView(View view) {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    public void showFootView() {
        postDelayed(() -> {
            if (mFootViews.size() > 0) {
                mFootViews.get(0).setVisibility(VISIBLE);
            }
        }, 500);
    }

    /**
     * 设置加载更多数据的监听
     */
    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    /**
     * 加载更多数据完成后调用，必须在UI线程中
     */
    public void loadMoreComplete(boolean isOver) {
        isLoadingData = false;

        if (mFootViews.size() > 0) {
            if (isOver) {
                ((LinearLayout) mFootViews.get(0)).getChildAt(0).setVisibility(GONE);
                ((LinearLayout) mFootViews.get(0)).getChildAt(1).setVisibility(VISIBLE);
                mFootViews.get(0).setVisibility(VISIBLE);
                mLoadDataListener = null;
            } else {
                ((LinearLayout) mFootViews.get(0)).getChildAt(1).setVisibility(GONE);
                ((LinearLayout) mFootViews.get(0)).getChildAt(0).setVisibility(VISIBLE);
                mFootViews.get(0).setVisibility(GONE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mFootViews.isEmpty()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rv_loadmore_footer, this,
                    false);
            mFootViews.add(view);
        }
        adapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    private void layoutGridAttach(final GridLayoutManager manager) {
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((WrapAdapter) mAdapter).isHeader(position) || ((WrapAdapter) mAdapter)
                        .isFooter(position) ? manager.getSpanCount() : 1;
            }
        });
        requestLayout();
    }

    private void layoutStaggeredGridHeadAttach(StaggeredGridLayoutManager manager) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (((WrapAdapter) mAdapter).isHeader(i)) {
                View view = getChildAt(i);
                ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(
                        true);
                view.requestLayout();
            } else {
                break;
            }
        }
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 当前不滚动，且不是正在刷新或加载数据
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadDataListener != null &&
                !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            // 获取最后一个正在显示的Item的位置
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager
                    .getItemCount() - 1) {
                if (mFootViews.size() > 0) {
                    mFootViews.get(0).setVisibility(VISIBLE);
                }
                // 加载更多
                isLoadingData = true;
                mLoadDataListener.onLoadMore();
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 自定义带有头部/脚部的适配器
     */
    private class WrapAdapter
            extends Adapter<ViewHolder> {

        private Adapter originAdapter;

        private ArrayList<View> mHeaderViews;

        private ArrayList<View> mFootViews;

        final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();

        private int headerPosition = 0;

        WrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews,
                    Adapter originAdapter) {
            this.originAdapter = originAdapter;
            if (mHeaderViews == null) {
                this.mHeaderViews = EMPTY_INFO_LIST;
            } else {
                this.mHeaderViews = mHeaderViews;
            }
            if (mFootViews == null) {
                this.mFootViews = EMPTY_INFO_LIST;
            } else {
                this.mFootViews = mFootViews;
            }
        }

        /**
         * 当前布局是否为Header
         */
        boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * 当前布局是否为Footer
         */
        boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        /**
         * Footer的数量
         */
        int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return RecyclerView.INVALID_TYPE;
            }
            int adjPosition = position - headerPosition;
            if (originAdapter != null) {
                int adapterCount = originAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return originAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                mFootViews.get(0).setLayoutParams(params);
                return new HeaderViewHolder(mFootViews.get(0));
            }
            return originAdapter.onCreateViewHolder(parent, viewType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(holder.getAdapterPosition())) {
                return;
            }
            int adjPosition = holder.getAdapterPosition() - mHeaderViews.size();
            if (originAdapter != null) {
                int adapterCount = originAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    originAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (originAdapter != null) {
                return mHeaderViews.size() + mFootViews.size() + originAdapter.getItemCount();
            } else {
                return mHeaderViews.size() + mFootViews.size();
            }
        }

        private class HeaderViewHolder
                extends ViewHolder {
            HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
