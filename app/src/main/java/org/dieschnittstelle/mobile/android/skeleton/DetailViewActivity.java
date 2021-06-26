package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    public static final int PICK_CONTACT = 0;
    private static final String DETAIL_VIEW_ACTIVITY = "DetailViewActivity";
    private DataItem item;
    private ActivityDetailviewBinding dataBindingHandle;
    private EditText itemNameText;
    private EditText itemDescription;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        item = (DataItem) getIntent().getSerializableExtra(ARG_ITEM);

        if(item==null){
            item = new DataItem();
        }

        Log.i("DetailViewActivity", "got contact ids: " + item.getContacts());
        item.getContacts().forEach(id ->
                showContactDetailsForInternalId(Long.parseLong(id)));
        this.dataBindingHandle.setController(this);
    }

    public void onSaveItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);
        this.setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public DataItem getItem() {
        return item;
    }

    public void setItem(DataItem item) {
        this.item = item;
    }

    public void showContacts(){
        Intent contactSelectionIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactSelectionIntent, 0);
    }

    protected void selectContact(){
        Intent contactSelectionIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactSelectionIntent, PICK_CONTACT);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.selectContact){
            this.selectContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK){
            Log.i("DetailViewActivity", "onActivityResult: got data: " + data);
            showContactDetails(data.getData());
        }else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void showContactDetails(Uri contactId){
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if(hasReadContactsPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        Cursor cursor = getContentResolver().query(contactId, null, null, null, null);
        if(cursor.moveToFirst()){
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            long internalContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if(!this.item.getContacts().contains(String.valueOf(internalContactId))){
                this.item.getContacts().add(String.valueOf(internalContactId));
            }

            Log.i("DetailviewActivity", "got contact with name " + contactName + " id " + internalContactId);
            showContactDetailsForInternalId(internalContactId);
        }else {
            Log.i("DetailviewActivity", "no contact found");

        }
    }

    public void showContactDetailsForInternalId(long id){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);

        if(cursor.moveToNext()){
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("DetailViewActivity", "Found display name: " + displayName + " for internal id: "+ id);

        }else{
            Log.i("DetailViewActivity", "no contact found for id: " + id);
        }


        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);

        while (cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            Log.i("DetailViewActivity", "Found number " + number + " of type " + phoneNumberType
                    + ", mobile: " + (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE));
        }
    }

    public boolean onNameInputCompleted(TextView v, int actionId, KeyEvent event) {
        Log.i(DETAIL_VIEW_ACTIVITY, "onEditAction(): " + actionId + ", " + event);
        if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){

        }
        return false;
    }

    public void onNameInputCompleted(View v, boolean hasFocus) {
        Log.i(DETAIL_VIEW_ACTIVITY, "onNameInputCompleted(): " + v);
        if(!hasFocus){
            String itemName = item.getItemName();
            if(itemName != null && itemName.length() >= 3){
                Log.i(DETAIL_VIEW_ACTIVITY, "Validation successful: " + itemName);

            }else {
                Log.i(DETAIL_VIEW_ACTIVITY, "Validation failed: " + itemName);
            }
        }
    }
}
