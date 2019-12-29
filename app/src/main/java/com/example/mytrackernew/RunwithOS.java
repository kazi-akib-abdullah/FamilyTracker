package com.example.mytrackernew;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

public class RunwithOS extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){
            GlobalInfo globalInfo = new GlobalInfo(context);
            globalInfo.LoadData();
            //start location track
            if (!TrackLocation.isRunning) {
                TrackLocation trackLocation = new TrackLocation();
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, trackLocation);
            }
            if (!MyService.IsRunning){
                Intent intent1=new Intent(context,MyService.class);
                context.startService(intent1);

            }

        }

    }
}
