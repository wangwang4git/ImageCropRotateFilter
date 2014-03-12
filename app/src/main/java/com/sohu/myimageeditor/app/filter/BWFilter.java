package com.sohu.myimageeditor.app.filter;


import android.content.res.Resources;

public class BWFilter extends BaseFilter {

    private ScriptC_bwfilter mScript;

    @Override
    public void createFilter(Resources res) {
        mScript = new ScriptC_bwfilter(mRS);
    }

    @Override
    public void runFilter() {
        mScript.invoke_prepareBwFilter(50, 50, 50);
        mScript.forEach_bwFilterKernel(mInPixelsAllocation, mOutPixelsAllocation);
    }

    @Override
    public void finish() {
        super.finish();
    }

}
