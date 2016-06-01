package com.fionera.videoplayerdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by fionera on 16-6-1.
 */

public class ActiveLinearLayoutManager  extends LinearLayoutManager{

    public ActiveLinearLayoutManager(Context context) {
        super(context);
    }

    private boolean canScrollVertical = true;

    public boolean isCanScrollVertical() {
        return canScrollVertical;
    }

    public void setCanScrollVertical(boolean canScrollVertical) {
        this.canScrollVertical = canScrollVertical;
    }

    @Override
    public boolean canScrollVertically() {
        return canScrollVertical && super.canScrollVertically();
    }
}
