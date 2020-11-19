package com.forkan.calllogtrace;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_CODE_FOR_CONTACTS = 2;
    private EditText mEditTextNumber;
    private ImageView mCallImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextNumber = findViewById(R.id.edit_text_number);
        mCallImage = findViewById(R.id.image_call);


        mCallImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserPermisions();
                makeCall();

            }
        });
    }

    //Check permission to get contacts list from your phone contacts

    void CheckUserPermisions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_CONTACTS
                }, REQUEST_CODE_FOR_CONTACTS);
                return;
            }
        }
        readContact(); //init the contact list
    }

    //getContacts list from your mobile
    ArrayList<Contacts> contactList = new ArrayList<Contacts>();

    void readContact() {
        //selection // String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ' %h%'";
        // sortOder // String = "updater("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC"

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new Contacts(name, phoneNumber));
        }

        ContactsAdapter mAdapter = new ContactsAdapter(contactList);
        ListView contactListView = (ListView) findViewById(R.id.contact_list);
        contactListView.setAdapter(mAdapter);
       // mAdapter.notifyDataSetChanged();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //make a call from your phone
    private void makeCall() {
        String number = mEditTextNumber.getText().toString().trim();
        if (number.length() > 0) {

            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(getApplicationContext(), "Enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL && requestCode == REQUEST_CODE_FOR_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
                readContact();

            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Adapter for list//////////////////////////////////////////////////////////
    public class ContactsAdapter extends BaseAdapter {

        public ArrayList<Contacts> contactList;

        public ContactsAdapter(ArrayList<Contacts> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View myView = inflater.inflate(R.layout.list_item, null);
            final Contacts contacts = contactList.get(position);

            TextView Name = (TextView) myView.findViewById(R.id.contact_name);
            Name.setText(contacts.name);

            TextView Phone = (TextView) myView.findViewById(R.id.phone_number);
            Phone.setText(contacts.phoneNumber);

            return myView;
        }
    }

}