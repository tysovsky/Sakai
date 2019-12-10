package com.sky.sakai.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sky.sakai.R;
import com.sky.sakai.models.User;
import com.sky.sakai.network.NetworkManager;

public class LoginActivity extends AppCompatActivity implements NetworkManager.OnLoginListener {

    private CredentialsClient credentialsClient;
    private EditText usernameEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;


    String username, password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);

        TextWatcher loginButtonEnabler = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(usernameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty())
                    loginButton.setEnabled(false);
                else
                    loginButton.setEnabled(true);
            }
        };

        usernameEditText.addTextChangedListener(loginButtonEnabler);
        passwordEditText.addTextChangedListener(loginButtonEnabler);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                NetworkManager.getInstance().login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), LoginActivity.this);
            }
        });

        requestPermissions();

        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        this.credentialsClient = Credentials.getClient(this, options);

        SharedPreferences sp = getSharedPreferences("Sakai", Context.MODE_PRIVATE);

        if (sp.contains("Username")){
            username = sp.getString("Username", "");
            password = sp.getString("Password", "");

            usernameEditText.setText(username);
            passwordEditText.setText(password);

            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            NetworkManager.getInstance().login(username, password, this);
        }
    }

    public void requestPermissions(){
        String[] PERMS_INITIAL={
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission: PERMS_INITIAL){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMS_INITIAL, 127);
                    break;
                }
            }

        }
    }

    @Override
    public void onLoginSuccess(User loggedInUser) {
        final User user = loggedInUser;

        SharedPreferences.Editor editor = getSharedPreferences("Sakai", Context.MODE_PRIVATE).edit();
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Hello " + user.FullName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onLoginFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Sign In Failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
