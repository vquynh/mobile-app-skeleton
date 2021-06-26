package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;
import org.dieschnittstelle.mobile.android.skeleton.model.IDataItemCRUDOperations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    @Override
    public DataItem createDataItem(DataItem item) {
        return null;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Stream.of("Item 1", "Item 2", "Item 3",
                "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9",
                "Item 10", "Item 11", "Item 12", "Item 13")
                .map(name -> new DataItem(name, "Description of "+name))
                .collect(Collectors.toList());
    }

    @Override
    public DataItem readDataItem(long id) {
        return null;
    }

    @Override
    public DataItem updateDataItem(DataItem item) {
        return null;
    }

    @Override
    public boolean deleteDataItem(long id) {
        return false;
    }

    @Override
    public boolean deleteAllDataItem() {
        return false;
    }
}
