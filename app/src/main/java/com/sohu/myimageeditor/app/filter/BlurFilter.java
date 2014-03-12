package com.sohu.myimageeditor.app.filter;

import android.content.res.Resources;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.widget.SeekBar;

public class BlurFilter extends BaseFilter {

    static final private int MAX_RADIUS = 25;
    private ScriptIntrinsicBlur mIntrinsic;
    private float mRadius = MAX_RADIUS / 2.0f;

    @Override
    public void createFilter(Resources res) {
        mIntrinsic = ScriptIntrinsicBlur.create(mRS, Element.U8_4(mRS));
        mIntrinsic.setInput(mInPixelsAllocation);
        mIntrinsic.setRadius(mRadius);
    }

    @Override
    public void runFilter() {
        mIntrinsic.forEach(mOutPixelsAllocation);
    }

    @Override
    public void onBar1Changed(int progress) {
        super.onBar1Changed(progress);
        mRadius = ((float) progress) / 100.0f * MAX_RADIUS;
        if (mRadius <= 0.10f) {
            mRadius = 0.10f;
        }
        mIntrinsic.setRadius(mRadius);
    }

    @Override
    public boolean onBar1Setup(SeekBar bar) {
        bar.setProgress(50);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
    }

}
