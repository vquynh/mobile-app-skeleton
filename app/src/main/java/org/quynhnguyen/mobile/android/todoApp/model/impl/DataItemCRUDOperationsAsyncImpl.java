package org.quynhnguyen.mobile.android.todoApp.model.impl;

import android.app.Activity;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;

import org.quynhnguyen.mobile.android.todoApp.model.LoggedInUser;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperationsAsync;
import org.quynhnguyen.mobile.android.todoApp.model.User;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DataItemCRUDOperationsAsyncImpl implements IDataItemCRUDOperationsAsync {
    private SyncedDataItemCRUDOperations crudExecutor;
    private Activity uiThreadProvider;
    private ProgressBar progressBar;

    public DataItemCRUDOperationsAsyncImpl(SyncedDataItemCRUDOperations crudExecutor, Activity uiThreadProvider, ProgressBar progressBar) {
        this.crudExecutor = crudExecutor;
        this.uiThreadProvider = uiThreadProvider;
        this.progressBar = progressBar;
    }

    @Override
    public void createDataItem(DataItem item, Consumer<DataItem> onCreated) {
        new Thread(() -> {
            DataItem create = this.crudExecutor.createDataItem(item);
            this.uiThreadProvider.runOnUiThread(() -> onCreated.accept(create));
        }).start();

    }

    @Override
    public void readAllDataItems(Consumer<List<DataItem>> onRead) {
        this.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            List<DataItem> dataItems = crudExecutor.readAllDataItems();
            uiThreadProvider.runOnUiThread(() -> {
                this.progressBar.setVisibility(View.GONE);

                onRead.accept(dataItems);
            });
        }).start();
    }

    @Override
    public void readDataItem(long id, Consumer<DataItem> onUpdated) {

    }

    @Override
    public void updateDataItem(DataItem item, Consumer<DataItem> onUpdated) {
        new Thread(() -> {
            DataItem updated = crudExecutor.updateDataItem(item);
            this.uiThreadProvider.runOnUiThread(() -> onUpdated.accept(updated));
        }).start();
    }

    @Override
    public void deleteAllDataItems(boolean remote, Consumer<Boolean> onDeleted) {
        new Thread(() -> {
            boolean deleted = crudExecutor.deleteAllDataItem(remote);
            this.uiThreadProvider.runOnUiThread(() -> onDeleted.accept(deleted));
        }).start();
    }

    @Override
    public void deleteDataItem(long id, Consumer<Boolean> onDeleted) {
        new Thread(() -> {
            boolean deleted = crudExecutor.deleteDataItem(id);
            this.uiThreadProvider.runOnUiThread(() -> onDeleted.accept(deleted));
        }).start();
    }

    @Override
    public void authenticateUser(User user, Consumer<LoggedInUser> onAuthenticated) {
        new Thread(() -> {
            boolean authenticated = crudExecutor.authenticateUser(user);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            uiThreadProvider.runOnUiThread(() -> {
                onAuthenticated.accept(authenticated ?
                        new LoggedInUser(UUID.randomUUID().toString(), user.getEmail()) : null);
            });
        }).start();
    }

    @Override
    public void synchroniseData(Consumer<List<DataItem>> onSynchronised) {
        new Thread(() -> {
            List<DataItem> synchronised = crudExecutor.synchroniseData();
            this.uiThreadProvider.runOnUiThread(() -> onSynchronised.accept(synchronised));
        }).start();
    }
}
