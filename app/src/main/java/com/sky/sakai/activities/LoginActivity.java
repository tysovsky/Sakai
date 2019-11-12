package com.sky.sakai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sky.sakai.R;
import com.sky.sakai.models.User;
import com.sky.sakai.network.NetworkManager;

public class LoginActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

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

                loginButton.setEnabled(false);

                NetworkManager.getInstance().login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new NetworkManager.OnLoginListener() {
                    @Override
                    public void onLoginSuccess(User loggedInUser) {
                        final User user = loggedInUser;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Hello " + user.FullName, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onLoginFailure() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Sign In Failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        requestPermissions();
    }

    public void requestPermissions(){
        String[] PERMS_INITIAL={
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMS_INITIAL, 127);
                // Get the result in onRequestPermissionsResult(int, String[], int[])
            } else {
                // Permission was granted, do your stuff here
            }
        }
    }

}
