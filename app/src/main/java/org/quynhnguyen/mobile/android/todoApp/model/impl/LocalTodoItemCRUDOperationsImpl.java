package org.quynhnguyen.mobile.android.todoApp.model.impl;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.User;

import java.util.List;

public class LocalTodoItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        long timestamp = System.currentTimeMillis();
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE DataItem "
                    +"ADD COLUMN expiry INTEGER default " + timestamp);
            database.execSQL("ALTER TABLE DataItem "
                    +"ADD COLUMN favourite INTEGER default 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        long timestamp = System.currentTimeMillis();
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE DataItem "
                    +"RENAME COLUMN checked TO done ");
        }
    };

    @Database(entities = {DataItem.class}, version = 3)
    public static abstract class RoomDataItemDatabase extends RoomDatabase {

        public abstract RoomDataItemCRUDAccess getDao();

    }

    @Dao
    public static interface RoomDataItemCRUDAccess {

        @Insert
        public long createItem(DataItem item);

        @Query("select * from dataitem")
        public List<DataItem> readAllItems();

        @Query("delete from dataitem")
        public int deleteAllItems();

        @Query("select * from dataitem where id = (:id)")
        public DataItem readItem(long id);

        @Update
        public int updateItem(DataItem item);

    }

    private RoomDataItemCRUDAccess roomAccessor;
    public LocalTodoItemCRUDOperationsImpl(Context databaseOwner){
        RoomDataItemDatabase db = Room
                .databaseBuilder(databaseOwner, RoomDataItemDatabase.class, "data-items-database")
                .addMigrations(MIGRATION_2_3)
                .build();
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
        return roomAccessor.deleteAllItems() > 0;
    }

    @Override
    public boolean authenticateUser(User user) {
        return true;
    }
}
