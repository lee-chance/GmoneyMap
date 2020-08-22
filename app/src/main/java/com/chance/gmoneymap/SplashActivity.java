package com.chance.gmoneymap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

public class SplashActivity extends AppCompatActivity {

    //location permission and values
    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermissions;

    @Override
    protected void onStart() {
        super.onStart();

        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                //detect current location
                if (checkLocationPermission()) {
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                    finish();
                } else {
                    requestLocationPermission();
                }
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init permissions array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted) {
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                    finish();
                } else {
                    //permission denied
                    CookieBar.build(this)
                            .setTitle("권한이 거부되었습니다")
                            .setTitleColor(android.R.color.white)
                            .setBackgroundColor(R.color.colorPrimary)
                            //.setIcon(R.drawable.icon)
                            //.setIconAnimation(R.animator.spin)
                            .setMessage("\n위치 권한을 동의해주세요\n")
                            .setCookiePosition(CookieBar.BOTTOM)
                            .setAction("설정하기", new OnActionClickListener() {
                                @Override
                                public void onClick() {
                                    onStart();
                                }
                            })
                            .setDuration(5000)
                            .show();
                }
            }
        }
    }
}
