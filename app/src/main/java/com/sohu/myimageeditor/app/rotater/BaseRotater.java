package com.sohu.myimageeditor.app.rotater;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by wangwang on 14-3-11.
 */
public abstract class BaseRotater {

    protected int mDegree;

    public Bitmap rotate(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setRotate(mDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        try {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmap != rotated) {
                bitmap.recycle();
                bitmap = rotated;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void destroy() {
    }

    abstract void setmDegree();

}
