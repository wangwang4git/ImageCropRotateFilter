package com.sohu.myimageeditor.app.rotater;

import android.graphics.Bitmap;

/**
 * Created by wangwang on 14-3-11.
 */
public class CCWRotater extends BaseRotater {

    @Override
    void setmDegree() {
        mDegree = -90;
    }

    @Override
    public Bitmap rotate(Bitmap bitmap) {
        setmDegree();
        return super.rotate(bitmap);
    }
}
