package com.example.mytrackernew;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends IntentService {
    public static boolean IsRunning=false;
    DatabaseReference databaseReference;

    public MyService(){
        super("MyService");
        IsRunning=true;
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        databaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Location location=TrackLocation.location;
                databaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("lat").setValue(TrackLocation.location.getLatitude());
                databaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("lag").setValue(TrackLocation.location.getLongitude());
                DateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date = new Date();
                databaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location").child("LastOnlineDate").setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
