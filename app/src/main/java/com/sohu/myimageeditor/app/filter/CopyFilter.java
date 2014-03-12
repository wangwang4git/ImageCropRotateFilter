package com.sohu.myimageeditor.app.filter;

import android.content.res.Resources;

public class CopyFilter extends BaseFilter {

    private ScriptC_copy mScript;

    @Override
    public void createFilter(Resources res) {
        mScript = new ScriptC_copy(mRS);
    }

    @Override
    public void runFilter() {
        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
    }

    @Override
    public void finish() {
        super.finish();
    }

}
