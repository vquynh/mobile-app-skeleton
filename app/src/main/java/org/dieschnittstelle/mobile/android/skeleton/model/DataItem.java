package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.Serializable;

public class DataItem implements Serializable {
    //private static final long serializedUid = -1L;
    private String itemName;
    private String description;
    private boolean checked;

    public DataItem() {
    }

    public DataItem(String itemName, String description) {
        this.itemName = itemName;
        this.description = description;
        this.checked = false;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", selected=" + checked +
                '}';
    }
}
