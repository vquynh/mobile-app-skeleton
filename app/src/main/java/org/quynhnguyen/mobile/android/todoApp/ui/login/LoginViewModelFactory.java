package org.quynhnguyen.mobile.android.todoApp.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import org.quynhnguyen.mobile.android.todoApp.DataItemApplication;
import org.quynhnguyen.mobile.android.todoApp.data.LoginDataSource;
import org.quynhnguyen.mobile.android.todoApp.data.LoginRepository;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.impl.DataItemCRUDOperationsAsyncImpl;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private DataItemCRUDOperationsAsyncImpl crudOperations;

    public LoginViewModelFactory(DataItemCRUDOperationsAsyncImpl crudOperations) {
        this.crudOperations = crudOperations;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource(crudOperations)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}