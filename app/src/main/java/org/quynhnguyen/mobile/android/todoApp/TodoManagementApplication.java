package org.quynhnguyen.mobile.android.todoApp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.quynhnguyen.mobile.android.todoApp.model.impl.RemoteTodoItemCRUDOperationsImpl;
import org.quynhnguyen.mobile.android.todoApp.model.impl.LocalTodoItemCRUDOperationsImpl;
import org.quynhnguyen.mobile.android.todoApp.model.impl.SyncedTodoItemCRUDOperations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/*
 * This is an android application for managing to-do items based on the android.app.Application
 * By overriding the super onCreate() method, SyncedTodoItemCRUDOperations
 * can be initiated depending on the connectivity status (online or offline).
 * The SyncedTodoItemCRUDOperations can then be acquired in all activities of the application
 * such as MainActivity or DetailViewActivity.
 */
public class TodoManagementApplication extends Application {

    public static final String REMOTE_SERVER = "http://192.168.178.69:8089";
    public static final int READ_TIMEOUT = 1000; //milliseconds
    public static final int CONNECT_TIMEOUT = 1000; //milliseconds
    protected static String logTag = "TodoManagementApplication";
    private SyncedTodoItemCRUDOperations syncedTodoItemCRUDOperations;
    private boolean serverAvailable = false;

    /*
     * this method overriding the super method and initiate SyncedTodoItemCRUDOperations
     * logs are added to make debugging easier
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // As the check for connectivity can be long running and is computed asynchronously,
        // the result is encapsulated in a Future so other processes can be executed
        // while the connectivity check takes its time to complete the task
        Future<Boolean> connectivityFuture = checkConnectivityAsync();
        try {
            // get() waits if necessary for the connectivity check to complete,
            // then retrieves its result which is a boolean value whether the server is connected
            boolean isConnectedToServer = connectivityFuture.get();

            if(isConnectedToServer){

                // toast to notify user about the connectivity
                Toast.makeText(this, "Connected to server.", Toast.LENGTH_SHORT).show();

                // syncedTodoItemCRUDOperations is initiated with both LocalTodoItemCRUDOperationsImpl
                // and RemoteTodoItemCRUDOperationsImpl because the connection to the server
                // needed for RemoteTodoItemCRUDOperationsImpl exists
                this.syncedTodoItemCRUDOperations = new SyncedTodoItemCRUDOperations(
                        new LocalTodoItemCRUDOperationsImpl(this),
                        new RemoteTodoItemCRUDOperationsImpl());
                // set serverAvailable to true
                this.serverAvailable = true;
            }else{

                // toast to notify user that server is not connected and app is in offline mode
                Toast.makeText(this,
                        "Cannot connect to server. Only local database will be used.",
                        Toast.LENGTH_SHORT)
                        .show();

                // syncedTodoItemCRUDOperations is initiated with only LocalTodoItemCRUDOperationsImpl
                this.syncedTodoItemCRUDOperations = new SyncedTodoItemCRUDOperations(
                        new LocalTodoItemCRUDOperationsImpl(this), null);
            }
        } catch (Exception e) {
            Log.e(logTag, "onCreate(): Got exception", e);

            // toast to notify user about exception while checking connectivity to server
            Toast.makeText(this, "Got exception while checking connectivity to server: " + e,
                    Toast.LENGTH_SHORT).show();

            // syncedTodoItemCRUDOperations is initiated with only LocalTodoItemCRUDOperationsImpl
            this.syncedTodoItemCRUDOperations = new SyncedTodoItemCRUDOperations(
                    new LocalTodoItemCRUDOperationsImpl(this), null);
        }
    }

    /*
     * returns the current syncedTodoItemCRUDOperations
     */
    public SyncedTodoItemCRUDOperations getCRUDOperations(){
        return this.syncedTodoItemCRUDOperations;
    }

    /*
     * returns the future result of the connectivity check
     * the actual check is done in a new thread
     */
    public Future<Boolean> checkConnectivityAsync(){

        // creates a new incomplete CompletableFuture
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // start a new thread to complete the future with the result from checkConnectivity()
        new Thread( () -> {
            boolean connectionAvailable = checkConnectivity();
            future.complete(connectionAvailable);
        }).start();
        return future;
    }

    /*
     * returns a boolean value whether the REMOTE_SERVER can be connected
     * within the READ_TIMEOUT and CONNECT_TIMEOUT
     */
    public boolean checkConnectivity()  {
        HttpURLConnection httpURLConnection = null;
        try {
            // open httpURLConnection to remote server
            httpURLConnection = (HttpURLConnection) new URL(REMOTE_SERVER).openConnection();

            // set timeouts
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);

            // set request method and do input
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);

            // connect to remote server
            httpURLConnection.connect();

            // get an input stream that reads from this open connection
            httpURLConnection.getInputStream();

            // if no exception is thrown, connection was successful
            Log.i(logTag, "checkConnectivity(): connected ");
            return true;

        } catch (IOException e) {
            // if exception happen while connection to remote server
            Log.e(logTag, "checkConnectivity(): Got exception: ", e);
            return false;
        } finally {
            // disconnect the connection if it is still opened
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

    }


    /*
     * returns a boolean value whether the remote server can be connected
     */
    public boolean isServerAvailable() {
        return this.serverAvailable;
    }
}
