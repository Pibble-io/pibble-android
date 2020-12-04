package com.star.pibbledev.wallet.send;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QRcodeScan extends BaseActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    RelativeLayout relative_qr;
    ImageButton img_back;

    private ZXingScannerView mScannerView;
    public static final int requestcamera=1;
    public boolean isflash;
    private List<BarcodeFormat> formats;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLightStatusBar();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            //Checking camera access permission
            if(checkpermission());
            else{
                //Requesting camera access permission
                requestpermission();
            }
        }


        setContentView(R.layout.activity_wallet_qrcodescan);
        relative_qr = (RelativeLayout)findViewById(R.id.relative_qr);
        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        createQR();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createQR() {
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomZXingScannerView(context);
            }
        };

        relative_qr.addView(mScannerView);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.getBackgroundTintMode();

        formats = new ArrayList<BarcodeFormat>();
        //setting the types barcode formats
        formats.add(BarcodeFormat.UPC_A);
        formats.add(BarcodeFormat.UPC_E);
        formats.add(BarcodeFormat.EAN_8);
        formats.add(BarcodeFormat.EAN_13);
        formats.add(BarcodeFormat.RSS_14);
        formats.add(BarcodeFormat.CODE_39);
        formats.add(BarcodeFormat.CODE_93);
        formats.add(BarcodeFormat.CODE_128);
        formats.add(BarcodeFormat.ITF);

        mScannerView.getMatrix();
        //setting the all types of qr code formats
        mScannerView.setFormats(ZXingScannerView.ALL_FORMATS);
        isflash=false;
    }

    private void requestpermission() {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},requestcamera);
    }

    private boolean checkpermission() {
        return (ContextCompat.checkSelfPermission(QRcodeScan.this, CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case requestcamera :
                if(grantResults.length>0)
                {
                    boolean cameraac=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    //grantResult[0] stores the the boolean value if permission is granted
                    if(cameraac) {
                        createQR();
                    }
                    else {
                        Toast.makeText(this, "Permission Denided", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//Permissions for Marshmallow and higher versions
                        {
                            if(shouldShowRequestPermissionRationale(CAMERA))
                            {
                                requestalert("You need to allow the camera access",

                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},requestcamera);
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    }
                }
                break;

        }
    }
    public void requestalert(String message, DialogInterface.OnClickListener listnser)
    {
        new AlertDialog.Builder(QRcodeScan.this)
                .setMessage(message)
                .setPositiveButton("OK",listnser)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    @Override
    public void onPause() {
        //turn off flash when paused
        super.onPause();
        if(isflash)
            mScannerView.setFlash(false);
    }

    @Override

    public void onResume()
    {
        //Resuming the scanner and flash
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkpermission())
            {

            }
            else{
                requestpermission();
            }
        }
        super.onResume();
        if(isflash)
            mScannerView.setFlash(true);
        mScannerView.resumeCameraPreview(this);
    }
    @Override
    public void onDestroy()
    {
        //Destroying the camera when appllication is Destroyed
        super.onDestroy();
        mScannerView.stopCamera();
    }
    @Override
    public void handleResult(Result rawResult)
    {
        //Scanned Result of Code

        if(rawResult !=null && !rawResult.getText().toString().isEmpty()) {
            Utility.g_QRcodeScanResult = rawResult.getText();
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }
        else
        {
            Toast.makeText(this, "Scanning Calceled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == img_back) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }
    }

}
