package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;
import org.dieschnittstelle.mobile.android.skeleton.model.IDataItemCRUDOperations;

import java.util.List;

public class RoomDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    @Database(entities = {DataItem.class}, version = 1)
    public static abstract class RoomDataItemDatabase extends RoomDatabase {

        public abstract RoomDataItemCRUDAccess getDao();
    }

    @Dao
    public static interface RoomDataItemCRUDAccess {

        @Insert
        public long createItem(DataItem item);

        @Query("select * from dataitem")
        public List<DataItem> readAllItems();

        @Query("select * from dataitem where id = (:id)")
        public DataItem readItem(long id);

        @Update
        public int updateItem(DataItem item);

    }

    private RoomDataItemCRUDAccess roomAccessor;
    public  RoomDataItemCRUDOperationsImpl(Context databaseOwner){
        RoomDataItemDatabase db = Room.databaseBuilder(databaseOwner, RoomDataItemDatabase.class, "data-items-database").build();
        this.roomAccessor = db.getDao();
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        long newId = roomAccessor.createItem(item);
        item.setId(newId);
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return roomAccessor.readAllItems();
    }

    @Override
    public DataItem readDataItem(long id) {
        return roomAccessor.readItem(id);
    }

    @Override
    public DataItem updateDataItem(DataItem item) {
        roomAccessor.updateItem(item);
        return item;
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
