package com.yihao.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.yihao.test.R;
import java.util.ArrayList;
import java.util.List;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class ZbarActivity extends Activity implements ZBarScannerView.ResultHandler{
    public static final String EXTRA_RESULT = "result";

    private ZBarScannerView mScannerView;

    private View mCancelView;

    private ImageView mLightView;

    private boolean mIsLightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbar_new);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("isLandScape", false)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initViews();
    }

    private void initViews(){
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        mScannerView = new ZBarScannerView(this);
        container.addView(mScannerView);

        mCancelView = findViewById(R.id.back);
        mCancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                mVibrator.vibrate(50);
                setResult(100);
                finish();
            }
        });

        mCancelView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((TextView) v).setTextColor(Color.rgb(0x22, 0x95, 0xC2));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((TextView) v).setTextColor(Color.rgb(0x33, 0xB5, 0xE5));
                }
                return false;
            }
        });

        mLightView = (ImageView) findViewById(R.id.lighter);
        mLightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScannerView.setFlash(!mIsLightOn);
                mLightView.setImageResource(mIsLightOn ? R.drawable.light_off : R.drawable.light_on);
                mIsLightOn = !mIsLightOn;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

        formats.add(BarcodeFormat.QRCODE);
        formats.add(BarcodeFormat.EAN13);
        formats.add(BarcodeFormat.UPCA);
        formats.add(BarcodeFormat.UPCE);
        formats.add(BarcodeFormat.EAN8);
        formats.add(BarcodeFormat.CODE128);
        formats.add(BarcodeFormat.CODE39);
        mScannerView.setFormats(formats);
        mScannerView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result result) {
        final String resultString = result.getContents();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, resultString);
        setResult(Activity.RESULT_OK, intent);
        finish();

        //第三方库中文档建议等待2s后再恢复相机预览，原因不明
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ZbarActivity.this);

            }
        }, 2000);
    }
}
