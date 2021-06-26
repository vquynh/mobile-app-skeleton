package org.quynhnguyen.mobile.android.todoApp.model;

import java.util.List;
import java.util.function.Consumer;

public interface IDataItemCRUDOperationsAsync {

    public void createDataItem(DataItem item, Consumer<DataItem> onCreated);

    public void readAllDataItems(Consumer<List<DataItem>> onRead);

    public void readDataItem(long id, Consumer<DataItem> onUpdated);

    public void updateDataItem(DataItem item, Consumer<DataItem> onUpdated);

    public void deleteDataItem(long id, Consumer<Boolean> onDeleted);

}
