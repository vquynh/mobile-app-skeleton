package org.quynhnguyen.mobile.android.todoApp.model.impl;

import org.jetbrains.annotations.NotNull;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.User;

import java.util.List;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    private final IDataItemCRUDOperations localCRUD;
    private final IDataItemCRUDOperations remoteCRUD;

    public SyncedDataItemCRUDOperationsImpl(RoomDataItemCRUDOperationsImpl localCRUD, RetrofitRemoteDataItemCRUDOperationsImpl remoteCRUD) {
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }
    @Override
    public DataItem createDataItem(DataItem item) {
        DataItem createdItem = localCRUD.createDataItem(item);
        remoteCRUD.createDataItem(createdItem);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return getDataItemsSynchronised();
    }

    @NotNull
    private List<DataItem> getDataItemsSynchronised() {
        List<DataItem> dataItems = localCRUD.readAllDataItems();
        if(!dataItems.isEmpty()){
            remoteCRUD.deleteAllDataItem();
            dataItems.forEach(remoteCRUD::createDataItem);
        }else{
            dataItems = remoteCRUD.readAllDataItems();
            dataItems.forEach(localCRUD::createDataItem);
        }
        return dataItems;
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

    @Override
    public boolean authenticateUser(User user) {
        return this.remoteCRUD != null && this.remoteCRUD.authenticateUser(user);
    }

    public boolean deleteAllDataItems(boolean remote) {
        return remote ? remoteCRUD.deleteAllDataItem() : localCRUD.deleteAllDataItem();
    }

    public void synchroniseData() {
        getDataItemsSynchronised();
    }
}
