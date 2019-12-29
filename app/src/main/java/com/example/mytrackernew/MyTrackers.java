package com.example.mytrackernew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MyTrackers extends AppCompatActivity {
    //adapter class
    ArrayList<AdapterItems>    listnewsData = new ArrayList<AdapterItems>();
    MyCustomAdapter myadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trackers);
        //add data and view it
        //listnewsData.add(new AdapterItems("Aqib","01677734142"));
        myadapter=new MyCustomAdapter(listnewsData);
        ListView lsNews=(ListView)findViewById(R.id.listView);
        lsNews.setAdapter(myadapter);//intisal with data
        Refresh();
        lsNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalInfo.MyTrackers.remove(listnewsData.get(position).PhoneNumber);
                DatabaseReference mDatabase =FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(listnewsData.get(position).PhoneNumber).child("Finders").child(GlobalInfo.PhoneNumber).removeValue();
                //GlobalInfo globalInfo= new GlobalInfo(this);
                //globalInfo.SaveData();
                Refresh();
            }
        });
    }
    //display news list
    void Refresh(){
        listnewsData.clear();
for(Map.Entry m:GlobalInfo.MyTrackers.entrySet()){
    listnewsData.add(new AdapterItems(m.getValue().toString(),m.getKey().toString()));
        }
        myadapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.goback:
                GlobalInfo globalInfo= new GlobalInfo(this);
                globalInfo.SaveData();
                finish();
                return true;
            case R.id.add:

                CheckUserPermsions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //access to permsions
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        PickContact();

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickContact();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void PickContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    // Declare
    static final int PICK_CONTACT=1;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="No number";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber =GlobalInfo.FormatPhoneNumber (phones.getString(phones.getColumnIndex("data1")));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        GlobalInfo.MyTrackers.put(cNumber,name);
                        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("Users").child(cNumber).child("Finders").child(GlobalInfo.PhoneNumber).setValue(true);
                        GlobalInfo globalInfo= new GlobalInfo(this);
                        globalInfo.SaveData();
                        Refresh();

                        //update firebase and
                        //update list
                        //update database
                    }
                }
                break;
        }
    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<AdapterItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<AdapterItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.single_row_conact, null);

            final   AdapterItems s = listnewsDataAdpater.get(position);

            TextView tv_user_name=( TextView)myView.findViewById(R.id.tv_user_name);
            tv_user_name.setText(s.UserName);
            TextView tv_phone_number=( TextView)myView.findViewById(R.id.tv_phone_number);
            tv_phone_number.setText(s.PhoneNumber);

            return myView;
        }

    }
}
