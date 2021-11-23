package org.quynhnguyen.mobile.android.todoApp.model.impl;

import org.jetbrains.annotations.NotNull;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.User;

import java.util.List;

public class SyncedDataItemCRUDOperations {
    private final IDataItemCRUDOperations localCRUD;
    private final IDataItemCRUDOperations remoteCRUD;

    public SyncedDataItemCRUDOperations(RoomDataItemCRUDOperationsImpl localCRUD, RetrofitRemoteDataItemCRUDOperationsImpl remoteCRUD) {
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }

    public DataItem createDataItem(DataItem item) {
        DataItem createdItem = localCRUD.createDataItem(item);
        if(remoteCRUD!= null){
            remoteCRUD.createDataItem(createdItem);
        }
        return item;
    }

    public List<DataItem> readAllDataItems() {
        return getDataItemsSynchronised();
    }

    @NotNull
    private List<DataItem> getDataItemsSynchronised() {
        List<DataItem> dataItems = localCRUD.readAllDataItems();
        if(remoteCRUD != null){
            if(!dataItems.isEmpty()){
                remoteCRUD.deleteAllDataItem();
                dataItems.forEach(remoteCRUD::createDataItem);
            }else {
                dataItems = remoteCRUD.readAllDataItems();
                dataItems.forEach(localCRUD::createDataItem);
            }
        }
        return dataItems;
    }

    public DataItem readDataItem(long id) {
        return null;
    }

    public DataItem updateDataItem(DataItem item) {
        DataItem createdItem = localCRUD.updateDataItem(item);
        if(remoteCRUD != null){
            remoteCRUD.updateDataItem(createdItem);
        }
        return item;
    }

    public boolean deleteDataItem(long id) {
        if(remoteCRUD!= null){
            remoteCRUD.deleteDataItem(id);
        }
        return localCRUD.deleteDataItem(id);
    }

    public boolean deleteAllDataItem(boolean remote) {
        return remote ? remoteCRUD.deleteAllDataItem() : localCRUD.deleteAllDataItem();
    }

    public boolean authenticateUser(User user) {
        return this.remoteCRUD != null && this.remoteCRUD.authenticateUser(user);
    }

    public List<DataItem> synchroniseData() {
       return getDataItemsSynchronised();
    }
}
