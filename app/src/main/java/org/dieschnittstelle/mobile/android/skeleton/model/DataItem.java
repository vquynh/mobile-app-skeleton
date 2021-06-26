package org.dieschnittstelle.mobile.android.skeleton.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class DataItem implements Serializable {

    public static class ArrayListToStringDatabaseConverter {
        @TypeConverter
        public static ArrayList<String> fromString(String value){
            if (value == null || value.isEmpty()){
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(value.split(",")));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @TypeConverter
        public static String fromArrayList(ArrayList<String> value){
            if(value == null || value.isEmpty()){
                return "";
            }
            return String.join(",", value);
        }
    }

    protected static final String logTag = "DataItem";
    protected static long ID_GENERATOR = 0;
    public static long nextId() {
        return ++ID_GENERATOR;
    }
    @SerializedName("name")
    private String itemName;
    private String description;

    @SerializedName("done")
    private boolean checked;

    //@Embedded
    @TypeConverters(ArrayListToStringDatabaseConverter.class)
    private ArrayList<String> contacts;

    @PrimaryKey(autoGenerate = true)
    private long id;

    public DataItem() {
    }

    public DataItem(String itemName, String description) {
        this.itemName = itemName;
        this.description = description;
        this.checked = false;
    }

    public String getItemName() {
        Log.i(logTag," getName(): " + itemName);
        return itemName == null ? "" : itemName;
    }

    public String getDescription() {
        Log.i(logTag," getDescription(): " + description);
        return description;
    }

    public void setItemName(String itemName) {
        Log.i(logTag," getDescription(): " + description);

        this.itemName = itemName;
    }

    public void setDescription(String description) {
        Log.i(logTag," setDescription(): " + description);
        this.description = description;
    }

    public boolean isChecked() {
        Log.i(logTag," isChecked(): " + checked);
        return checked;
    }

    public void setChecked(boolean checked) {
        Log.i(logTag," setChecked(): " + checked);
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataItem dataItem = (DataItem) o;
        return id == dataItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ArrayList<String> getContacts() {
        if(this.contacts == null){
            this.contacts = new ArrayList<>();
        }
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }
}
