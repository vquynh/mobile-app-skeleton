package org.quynhnguyen.mobile.android.todoApp.data;

import android.security.keystore.UserNotAuthenticatedException;

import androidx.lifecycle.MutableLiveData;

import org.quynhnguyen.mobile.android.todoApp.data.model.LoggedInUser;
import org.quynhnguyen.mobile.android.todoApp.model.User;
import org.quynhnguyen.mobile.android.todoApp.model.impl.DataItemCRUDOperationsAsyncImpl;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private DataItemCRUDOperationsAsyncImpl crudOperations;
    private MutableLiveData<LoggedInUser> loggedInUser = new MutableLiveData<>();

    public LoginDataSource(DataItemCRUDOperationsAsyncImpl crudOperations) {
        this.crudOperations = crudOperations;
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            User user = new User(username, password);
            crudOperations.authenticateUser(user, result -> loggedInUser.setValue(result));
            if(loggedInUser.getValue()!=null){
                return new Result.Success<>(loggedInUser.getValue());

            }else {
                return new Result.Error(new UserNotAuthenticatedException("User: " + username + "could not be authenticated"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}