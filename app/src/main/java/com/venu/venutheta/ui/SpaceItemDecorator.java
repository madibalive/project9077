package com.venu.venutheta.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Madiba on 12/6/2016.
 */

public class SpaceItemDecorator extends RecyclerView.ItemDecoration {
    private int mSpace;
    /**
     * Constructor.
     * @param space the spacing, in pixels, to apply.
     */
    public SpaceItemDecorator(final int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, final RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;
    }
}
