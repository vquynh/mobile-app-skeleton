package org.quynhnguyen.mobile.android.todoApp.model.impl;

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
    //TODO: synchronise remote and local database
    @Override
    public DataItem createDataItem(DataItem item) {
        remoteCRUD.createDataItem(item);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return localCRUD.readAllDataItems();
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

    @Override
    public boolean isRemote() {
        return this.remoteCRUD != null;
    }

    public boolean deleteAllLocalDataItems() {
        return false;
    }

    public boolean deleteAllRemoteDataItems() {
        return remoteCRUD.deleteAllDataItem();
    }

    public boolean deleteAllRemoteDataItems(boolean remote) {
        return remote ? deleteAllRemoteDataItems() : deleteAllLocalDataItems();
    }
}
