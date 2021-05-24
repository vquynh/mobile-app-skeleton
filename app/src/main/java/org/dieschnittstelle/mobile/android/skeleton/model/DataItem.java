package org.dieschnittstelle.mobile.android.skeleton.model;

import android.util.Log;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class DataItem implements Serializable {
    protected static final String logTag = "DataItem";
    protected static long ID_GENERATOR = 0;
    public static long nextId() {
        return ++ID_GENERATOR;
    }
    private String itemName;
    private String description;
    private boolean checked;
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
        return itemName;
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
}
