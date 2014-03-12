package com.sohu.myimageeditor.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.sohu.myimageeditor.app.filter.BWFilter;
import com.sohu.myimageeditor.app.filter.BaseFilter;
import com.sohu.myimageeditor.app.filter.BlurFilter;
import com.sohu.myimageeditor.app.filter.CopyFilter;
import com.sohu.myimageeditor.app.rotater.BaseRotater;
import com.sohu.myimageeditor.app.rotater.CCWRotater;
import com.sohu.myimageeditor.app.rotater.CWRotater;
import com.sohu.myimageeditor.app.widget.CropImageView;


public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;
    private RenderScript mRS;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;
    private BaseFilter mFilter;
    private BaseRotater mRotater;

    private ImageView mOriginalDisplayView;
    private CropImageView mCropDisplayView;
    private HorizontalScrollView mCropListView;
    private Button mCropCancel;
    private Button mCropOK;
    private HorizontalScrollView mRotateListView;
    private Button mRotateCW;
    private Button mRotateCCW;
    private HorizontalScrollView mFilterListView;
    private Button mFilterBW;
    private Button mFilterBlur;
    private Button mFilterOriginal;
    private Button mClipBtn;
    private Button mRotateBtn;
    private Button mFilterBtn;
    private SeekBar mFilterBar1;

    private enum RotaterName {

        CW_ROTATER("CW Rotater"),
        CCW_ROTATER("CCW Rotater");

        private final String name;

        private RotaterName(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }

    }

    private enum FilterName {

        BW_FILTER("BW Filter"),
        BLUR_FILTER("BLUR Filter"),
        ORIGINAL_FILTER("ORIGINAL Filter");

        private final String name;

        private FilterName(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmapIn = loadBitmap(R.drawable.myfilter);
        // mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());
        mBitmapOut = mBitmapIn.copy(mBitmapIn.getConfig(), true);
        mRS = RenderScript.create(this);
        mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);

        mOriginalDisplayView = (ImageView) findViewById(R.id.original_display);
        mCropDisplayView = (CropImageView) findViewById(R.id.crop_display);
        mCropListView = (HorizontalScrollView) findViewById(R.id.crop_list);
        mCropCancel = (Button) findViewById(R.id.crop_cancel);
        mCropOK = (Button) findViewById(R.id.crop_ok);
        mRotateListView = (HorizontalScrollView) findViewById(R.id.rotate_list);
        mRotateCW = (Button) findViewById(R.id.rotate_cw);
        mRotateCCW = (Button) findViewById(R.id.rotate_ccw);
        mFilterListView = (HorizontalScrollView) findViewById(R.id.filter_list);
        mFilterBW = (Button) findViewById(R.id.filter_bw);
        mFilterBlur = (Button) findViewById(R.id.filter_blur);
        mFilterOriginal = (Button) findViewById(R.id.filter_original);
        mClipBtn = (Button) findViewById(R.id.clip);
        mRotateBtn = (Button) findViewById(R.id.rotate);
        mFilterBtn = (Button) findViewById(R.id.filter);
        mFilterBar1 = (SeekBar) findViewById(R.id.filter_bar1);

        mOriginalDisplayView.setImageBitmap(mBitmapOut);
        mCropCancel.setOnClickListener(mOnClickListener);
        mCropOK.setOnClickListener(mOnClickListener);
        mRotateCW.setOnClickListener(mOnClickListener);
        mRotateCCW.setOnClickListener(mOnClickListener);
        mFilterBW.setOnClickListener(mOnClickListener);
        mFilterBlur.setOnClickListener(mOnClickListener);
        mFilterOriginal.setOnClickListener(mOnClickListener);
        mClipBtn.setOnClickListener(mOnClickListener);
        mRotateBtn.setOnClickListener(mOnClickListener);
        mFilterBtn.setOnClickListener(mOnClickListener);
        mFilterBar1.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                Toast.makeText(this, getResources().getString(R.string.action_save), Toast.LENGTH_SHORT).show();
                cleanEditView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setupBars() {
        mFilterBar1.setVisibility(View.VISIBLE);
        mFilter.onBar1Setup(mFilterBar1);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.clip:
                    showCropView();
                    break;
                case R.id.rotate:
                    showRotateView();
                    break;
                case R.id.filter:
                    showFilteView();
                    break;
                case R.id.filter_bw:
                    Log.i(TAG, FilterName.BW_FILTER.toString());
                    executeFilter(FilterName.BW_FILTER);
                    break;
                case R.id.filter_blur:
                    Log.i(TAG, FilterName.BLUR_FILTER.toString());
                    executeFilter(FilterName.BLUR_FILTER);
                    break;
                case R.id.filter_original:
                    Log.i(TAG, FilterName.ORIGINAL_FILTER.toString());
                    executeFilter(FilterName.ORIGINAL_FILTER);
                    break;
                case R.id.rotate_cw:
                    Log.i(TAG, RotaterName.CW_ROTATER.toString());
                    executeRotater(RotaterName.CW_ROTATER);
                    break;
                case R.id.rotate_ccw:
                    Log.i(TAG, RotaterName.CCW_ROTATER.toString());
                    executeRotater(RotaterName.CCW_ROTATER);
                    break;
                case R.id.crop_ok:
                    mBitmapOut = mCropDisplayView.getCropBitmap();
                    mBitmapIn = mBitmapOut.copy(mBitmapOut.getConfig(), true);
                    mOriginalDisplayView.setImageBitmap(mBitmapOut);
                    mOriginalDisplayView.invalidate();
                    mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn);
                    mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
                    cleanEditView();
                    break;
                case R.id.crop_cancel:
                    cleanEditView();
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                if (seekBar == mFilterBar1) {
                    mFilter.onBar1Changed(i);
                }
                mFilter.runFilter();
                updateDisplay();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }

    private void executeFilter(FilterName filterName) {
        if (mFilter != null) {
            mFilter.destroy();
        }
        switch (filterName) {
            case BW_FILTER:
                mFilter = new BWFilter();
                break;
            case BLUR_FILTER:
                mFilter = new BlurFilter();
                break;
            case ORIGINAL_FILTER:
                mFilter = new CopyFilter();
                break;
        }
        mFilter.initBaseFilter(this.getApplicationContext(), mRS, mBitmapIn.getWidth(), mBitmapIn.getHeight(),
                mInPixelsAllocation, mOutPixelsAllocation);
        setupBars();
        mFilter.runFilter();
        updateDisplay();
    }

    private void executeRotater(RotaterName rotaterName) {
        if (mRotater != null) {
            mRotater.destroy();
        }
        switch (rotaterName) {
            case CW_ROTATER:
                mRotater = new CWRotater();
                break;
            case CCW_ROTATER:
                mRotater = new CCWRotater();
                break;
        }
        mBitmapOut = mRotater.rotate(mBitmapOut);
        mBitmapIn = mRotater.rotate(mBitmapIn);
        updateDisplay();
    }

    private void updateDisplay() {
        if (mFilterListView.isShown()) {
            mFilter.update(mBitmapOut);
            mOriginalDisplayView.invalidate();
            return;
        }
        if (mRotateListView.isShown()) {
            mOriginalDisplayView.setImageBitmap(mBitmapOut);
            mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn);
            mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
            mOriginalDisplayView.invalidate();
            return;
        }
    }

    private void showCropView() {
        if (!mCropListView.isShown()) {
            mCropListView.setVisibility(View.VISIBLE);

            mCropDisplayView.setBitmap(mBitmapOut);
            mCropDisplayView.reset();
            mCropDisplayView.setVisibility(View.VISIBLE);
            mOriginalDisplayView.setVisibility(View.GONE);
        } else {
            mCropListView.setVisibility(View.GONE);

            mCropDisplayView.setVisibility(View.GONE);
            mOriginalDisplayView.setVisibility(View.VISIBLE);
        }
        if (mRotateListView.isShown()) {
            mRotateListView.setVisibility(View.GONE);
        }
        if (mFilterListView.isShown()) {
            mFilterListView.setVisibility(View.GONE);
        }
    }

    private void showRotateView() {
        if (!mOriginalDisplayView.isShown()) {
            mCropDisplayView.setVisibility(View.GONE);
            mOriginalDisplayView.setVisibility(View.VISIBLE);
        }

        if (mCropListView.isShown()) {
            mCropListView.setVisibility(View.GONE);
        }
        if (!mRotateListView.isShown()) {
            mRotateListView.setVisibility(View.VISIBLE);
        } else {
            mRotateListView.setVisibility(View.GONE);
        }
        if (mFilterListView.isShown()) {
            mFilterListView.setVisibility(View.GONE);
        }
    }

    private void showFilteView() {
        if (!mOriginalDisplayView.isShown()) {
            mCropDisplayView.setVisibility(View.GONE);
            mOriginalDisplayView.setVisibility(View.VISIBLE);
        }

        if (mCropListView.isShown()) {
            mCropListView.setVisibility(View.GONE);
        }
        if (mRotateListView.isShown()) {
            mRotateListView.setVisibility(View.GONE);
        }
        if (!mFilterListView.isShown()) {
            mFilterListView.setVisibility(View.VISIBLE);
        } else {
            mFilterListView.setVisibility(View.GONE);
        }
    }

    private void cleanEditView() {
        if (!mOriginalDisplayView.isShown()) {
            mCropDisplayView.setVisibility(View.GONE);
            mOriginalDisplayView.setVisibility(View.VISIBLE);
        }

        if (mCropListView.isShown()) {
            mCropListView.setVisibility(View.GONE);
        }
        if (mRotateListView.isShown()) {
            mRotateListView.setVisibility(View.GONE);
        }
        if (mFilterListView.isShown()) {
            mFilterListView.setVisibility(View.GONE);
        }
    }

}
