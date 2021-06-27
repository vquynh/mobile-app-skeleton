package org.quynhnguyen.mobile.android.todoApp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.quynhnguyen.mobile.android.todoApp.DataItemApplication;
import org.quynhnguyen.mobile.android.todoApp.MainActivity;
import org.quynhnguyen.mobile.android.todoApp.R;
import org.quynhnguyen.mobile.android.todoApp.databinding.ActivityLoginBinding;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.User;
import org.quynhnguyen.mobile.android.todoApp.model.impl.DataItemCRUDOperationsAsyncImpl;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private DataItemCRUDOperationsAsyncImpl crudOperations;
    private ProgressBar progressBar;
    private Snackbar snackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.progressBar = new ProgressBar(this);
        IDataItemCRUDOperations crudExecutor = ((DataItemApplication) this.getApplication()).getCRUDOperations();
        this.crudOperations = new DataItemCRUDOperationsAsyncImpl(crudExecutor, this,progressBar);

        if (!crudExecutor.isRemote()){
            goToMainActivity();
        }else {

            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());


            loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(this.crudOperations))
                    .get(LoginViewModel.class);

            final EditText usernameEditText = binding.username;
            final EditText passwordEditText = binding.password;
            final Button loginButton = binding.login;
            final ProgressBar loadingProgressBar = binding.loading;


            loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
                @Override
                public void onChanged(@Nullable LoginFormState loginFormState) {
                    if(snackbar != null && snackbar.isShown()){
                        snackbar.dismiss();
                    }
                    if (loginFormState == null) {
                        return;
                    }
                    loginButton.setEnabled(loginFormState.isDataValid());
                    if (loginFormState.getUsernameError() != null) {
                        usernameEditText.setError(getString(loginFormState.getUsernameError()));
                    }
                    if (loginFormState.getPasswordError() != null) {
                        passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                }
            });

            loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
                @Override
                public void onChanged(@Nullable LoginResult loginResult) {
                    if (loginResult == null) {
                        return;
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                    if (loginResult.getError() != null) {
                        showLoginFailed();

                    }
                    if (loginResult.getSuccess() != null) {
                        goToMainActivity();
                    }
                    setResult(Activity.RESULT_OK);

                    //Complete and destroy login activity once successful
                    //finish();
                }
            });

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        User user = new User(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                        crudOperations.authenticateUser(user,loggedInUser -> loginViewModel.updateLoginResult(loggedInUser));
                    }
                    return false;
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    User user = new User(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                    crudOperations.authenticateUser(user,loggedInUser -> loginViewModel.updateLoginResult(loggedInUser));
                }
            });

        }
    }

    private void goToMainActivity() {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        this.startActivityForResult(mainActivityIntent, 0);
        Toast.makeText(getApplicationContext(), "Welcome to Todo App", Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed() {
        snackbar = Snackbar
                .make(findViewById(R.id.loginContainer), "Authentication failed!", Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(findViewById(R.id.login));
        snackbar.show();
    }

}