package org.quynhnguyen.mobile.android.todoApp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;
import org.quynhnguyen.mobile.android.todoApp.databinding.ActivityDetailviewBinding;
import org.quynhnguyen.mobile.android.todoApp.databinding.ContactListItemBinding;
import org.quynhnguyen.mobile.android.todoApp.model.Contact;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    public static final String DATE_SEPARATOR = "-";
    public static final String TIME_SEPARATOR = ":";
    public static final int PICK_CONTACT = 0;
    private static final String DETAIL_VIEW_ACTIVITY = "DetailViewActivity";
    private DataItem item;
    private ActivityDetailviewBinding dataBindingHandle;
    private String errorStatus;
    private EditText dateText, timeText;
    private ListView listView;
    private ArrayAdapter<Contact> listViewAdapter;


    private class ContactItemsAdapter extends ArrayAdapter<Contact> {
        private final int layoutResource;
        public ContactItemsAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull List<Contact> objects) {
            super(context, resource, objects);
            this.layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View recyclableItemView, @NonNull ViewGroup parent) {
            String logTag = "DetailView";
            Log.i(logTag,"getView(): for position" + position + " and convertView" + recyclableItemView);
            View itemView = null;
            Contact currentItem = getItem(position);
            if(recyclableItemView != null){

                TextView textView = (TextView) recyclableItemView.findViewById(R.id.displayName);
                if(textView != null) {

                    Log.i(logTag,"getView(): itemName in convertView: " + textView.getText());
                }
                itemView = recyclableItemView;
                ContactListItemBinding recycleBinding = (ContactListItemBinding) itemView.getTag();
                recycleBinding.setItem(currentItem);

            }else{
                ContactListItemBinding currentBinding = DataBindingUtil
                        .inflate(getLayoutInflater(), this.layoutResource, parent, false);
                currentBinding.setItem(currentItem);
                currentBinding.setController(DetailViewActivity.this);
                itemView =  currentBinding.getRoot();
                itemView.setTag(currentBinding);
            }
            return itemView;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);
        this.dateText = findViewById(R.id.in_date);
        this.timeText = findViewById(R.id.in_time);
        item = (DataItem) getIntent().getSerializableExtra(ARG_ITEM);

        if(item==null){
            item = new DataItem();
        }
        if(item.getExpiry() != -1L && item.getExpiry() != 0L){
            LocalDateTime expiry = LocalDateTime.ofInstant(Instant.ofEpochSecond(item.getExpiry()), ZoneId.systemDefault());
            setDateTimeText(expiry);
        }else {
            LocalDateTime now = LocalDateTime.now();
            item.setExpiry(now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            setDateTimeText(now);
        }

        Log.i("DetailViewActivity", "got contact ids: " + item.getContacts());
        List<Contact> contacts = new ArrayList<>();

        item.getContacts().forEach(id ->
                contacts.add(getContactDetailsFromContactInternalId(Long.parseLong(id))));
        this.listView = findViewById(R.id.contactList);
        this.listViewAdapter = new DetailViewActivity.ContactItemsAdapter(
                this,R.layout.contact_list_item,
                contacts.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        this.listView.setAdapter(this.listViewAdapter);
        this.dataBindingHandle.setController(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateTimeText(LocalDateTime expiry) {
        dateText.setText(formatDate(expiry.getYear(), expiry.getMonthValue(), expiry.getDayOfMonth()));
        timeText.setText(formatTime(expiry.getHour(), expiry.getMinute()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSaveItem() {
        Intent returnIntent = new Intent();
        String[] date = dateText.getText().toString().split(DATE_SEPARATOR);
        String[] time = timeText.getText().toString().split(TIME_SEPARATOR);
        long expiry = LocalDateTime.of(
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1])).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        item.setExpiry(expiry);
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
        } else if(item.getItemId() == R.id.sendSMS){
            sendSms();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendSms() {
        Uri smsUri = Uri.parse("smsto:00000");
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
        smsIntent.putExtra("sms_body", item.getItemName() + ": " + item.getDescription());
        startActivity(smsIntent);
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
                Contact contact = getContactDetailsFromContactInternalId(internalContactId);
                if(contact != null){
                    this.listViewAdapter.add(contact);
                    this.listViewAdapter.notifyDataSetChanged();
                }
            }

            getContactDetailsFromContactInternalId(internalContactId);
        }else {
            Log.i("DetailviewActivity", "no contact found");

        }
    }

    public Contact getContactDetailsFromContactInternalId(long id){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);
        String displayName;

        if(cursor.moveToNext()){
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("DetailViewActivity", "Found display name: " + displayName + " for internal id: "+ id);

        }else{
            Log.i("DetailViewActivity", "no contact found for id: " + id);
            return null;
        }

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);

        String mobileNumber = null;
        while (cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            if(phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
                mobileNumber = number;
            }
        }
        String emailAddress = null;
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);
        while (cursor.moveToNext()){
            emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

        }
        return new Contact(id, displayName, mobileNumber, emailAddress);
    }

    public void onNameInputChanged() {
        if(this.errorStatus != null){
            this.errorStatus = null;
            this.dataBindingHandle.setController(this);
        }
    }

    public void onContactListSelected() {
        if(this.errorStatus != null){
            this.errorStatus = null;
            this.dataBindingHandle.setController(this);
        }
    }

    public void onNameInputCompleted(boolean hasFocus) {
        Log.i(DETAIL_VIEW_ACTIVITY, "onNameInputCompleted(): " + hasFocus);
        if(!hasFocus){
            String itemName = item.getItemName();
            if(itemName != null && itemName.length() >= 3){
                Log.i(DETAIL_VIEW_ACTIVITY, "Validation successful: " + itemName);
                this.errorStatus = null;

            }else {
                Log.i(DETAIL_VIEW_ACTIVITY, "Validation failed: " + itemName);
                this.errorStatus = "Item's name is too short.";
                this.dataBindingHandle.setController(this);
            }
        }
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((EditText) getActivity().findViewById(R.id.in_time)).setText(formatTime(hourOfDay, minute));
        }
    }

    @NotNull
    private static String formatTime(int hourOfDay, int minute) {
        return String.format(Locale.GERMANY, "%d:%d", hourOfDay, minute);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((EditText) getActivity().findViewById(R.id.in_date)).setText(formatDate(year, month, day));
        }
    }

    @NotNull
    private static String formatDate(int year, int month, int day) {
        return String.format(Locale.GERMANY, "%d-%d-%d", year, month, day);
    }


}
