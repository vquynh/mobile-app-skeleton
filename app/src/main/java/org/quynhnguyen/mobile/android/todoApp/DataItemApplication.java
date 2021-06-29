package org.quynhnguyen.mobile.android.todoApp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import org.quynhnguyen.mobile.android.todoApp.model.impl.RoomDataItemCRUDOperationsImpl;
import org.quynhnguyen.mobile.android.todoApp.model.impl.SyncedDataItemCRUDOperationsImpl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DataItemApplication extends Application {

    protected static String logTag = "DataApplication";
    private IDataItemCRUDOperations crudOperations;
    private boolean serverAvailable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Future<Boolean> connectivityFuture = checkConnectivityAsync();
        try {
            if(connectivityFuture.get()){
                Toast.makeText(this, "Application started...", Toast.LENGTH_SHORT).show();
                this.crudOperations = new SyncedDataItemCRUDOperationsImpl(
                        new RoomDataItemCRUDOperationsImpl(this), new RetrofitRemoteDataItemCRUDOperationsImpl());
                this.serverAvailable = true;
            }else{
                this.crudOperations = new RoomDataItemCRUDOperationsImpl(this);
            }
        } catch (Exception e) {
            Log.e(logTag, "onCreate(): Got exception", e);
            Toast.makeText(this, "Backend not accessible, got exception: " + e, Toast.LENGTH_SHORT).show();
            this.crudOperations = new RoomDataItemCRUDOperationsImpl(this);
        }
    }

    public IDataItemCRUDOperations getCRUDOperations(){

        return this.crudOperations;
    }

    public Future<Boolean> checkConnectivityAsync(){
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new Thread( () -> {
            boolean connectionAvailable = checkConnectivity();
            future.complete(connectionAvailable);
        }).start();
        return future;
    }

    public boolean checkConnectivity()  {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL("http://192.168.178.69:8089")
                    .openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getInputStream();
            Log.i(logTag, "checkConnectivity(): connected ");
            return true;
        } catch (IOException e) {
            Log.e(logTag, "checkConnectivity(): Got exception: ", e);
            return false;
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }

    }

    public boolean isServerAvailable() {
        return this.serverAvailable;
    }
}
