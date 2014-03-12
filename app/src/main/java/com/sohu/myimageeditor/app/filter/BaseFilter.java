package com.sohu.myimageeditor.app.filter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by wangwang on 14-3-10.
 */
public abstract class BaseFilter {

    private final String TAG = BaseFilter.class.getSimpleName();

    protected RenderScript mRS;
    protected Allocation mInPixelsAllocation;
    protected Allocation mOutPixelsAllocation;
    protected int mWidth;
    protected int mHeight;
    protected Context mContent;

    public void initBaseFilter(Context mContent, RenderScript mRS, int mWidth, int mHeight,
                               Allocation mInPixelsAllocation, Allocation mOutPixelsAllocation) {
        this.mContent = mContent;
        this.mRS = mRS;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mInPixelsAllocation = mInPixelsAllocation;
        this.mOutPixelsAllocation = mOutPixelsAllocation;

        createFilter(this.mContent.getResources());
    }

    abstract public void createFilter(Resources res);

    abstract public void runFilter();

    public void onBar1Changed(int progress) {
    }

    public boolean onBar1Setup(SeekBar bar) {
        bar.setVisibility(View.GONE);
        return false;
    }

    public void finish() {
        mRS.finish();
    }

    public void destroy() {
    }

    public void update(Bitmap bitmap) {
        mOutPixelsAllocation.copyTo(bitmap);
    }

}
