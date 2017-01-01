package com.venu.venutheta.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Madiba on 9/26/2016.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {


    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    private boolean drawBorder = false;
    private Drawable mDivider;

    private int mOrientation;

    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public DividerItemDecoration(Context context, int orientation, boolean drawBorder) {
        this.drawBorder = drawBorder;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();

        int columnCount = 1;
        int rowCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (null != layoutManager && layoutManager instanceof GridLayoutManager) {
            columnCount = ((GridLayoutManager) layoutManager).getSpanCount();
            rowCount = (0 == childCount % columnCount) ? childCount / columnCount : childCount / columnCount + 1;
        } else {
            rowCount = childCount;
        }

        for (int i = 0; i < childCount; i++) {
            if (!drawBorder && i >= (rowCount - 1) * columnCount)
                return;

            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);


            if (drawBorder && i <= columnCount) {
                int top1 = child.getTop() - params.topMargin;
                int bottom1 = top1 + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top1, right, bottom1);
                mDivider.draw(c);
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (null != layoutManager && layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            if (!drawBorder && i % spanCount == (spanCount - 1))
                return;

            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);

            if (drawBorder && i % spanCount == 0) {
                int left1 = child.getLeft() - params.leftMargin;
                int right1 = left1 + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left1, top, right1, bottom);
                mDivider.draw(c);
            }

        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}