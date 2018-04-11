package com.someshthakur.jpmconlinescheduler;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        webView = findViewById(R.id.webView);
        final TextView jpmc = findViewById(R.id.jpmc);
        final TextView textView = findViewById(R.id.textView);
        final String[] url = {"https://beeapp.000webhostapp.com/"};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("url");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url[0] = (String) dataSnapshot.getValue();
                webView.loadUrl(url[0]);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setGeolocationEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {
                                               @Override
                                               public void onProgressChanged(WebView view, int newProgress) {
                                                   //  super.onProgressChanged(view, newProgress);
                                                   if (newProgress == 100) {
                                                       jpmc.setVisibility(View.GONE);
                                                       textView.setVisibility(View.GONE);
                                                       webView.setVisibility(View.VISIBLE);
                                                       findViewById(R.id.loading).setVisibility(View.GONE);
                                                   }
                                               }

                                               @Override
                                               public void onPermissionRequest(PermissionRequest request) {
//                        super.onPermissionRequest(request);
                                                   request.grant(request.getResources());
                                               }

                                               @Override
                                               public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                                                   //super.onGeolocationPermissionsShowPrompt(origin, callback);
                                                   // Toast.makeText(MainActivity.this, "Location re", Toast.LENGTH_LONG).show();
                                                   callback.invoke(origin, true, false);
                                               }
                                           }
                );
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setGeolocationDatabasePath(MainActivity.this.getFilesDir().getPath());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void checkPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO,
        }, 0);


        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location is Not enabled");
            dialog.setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MainActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
//            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!webView.canGoBack()) {
            super.onBackPressed();
        }
        webView.goBack();
    }
}
