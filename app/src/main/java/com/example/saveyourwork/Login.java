package com.example.saveyourwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saveyourwork.Config.RetrofitConfig;
import com.example.saveyourwork.Repository.UserRepository;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private RetrofitConfig retrofitConfig;
    private UserRepository userRepository;

    TextView txtRegister;
    EditText editUsername, editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofitConfig = new RetrofitConfig();

        userRepository = retrofitConfig.getRetrofit().create(UserRepository.class);

        final LinearLayout linearLayout = findViewById(R.id.activity_login);

        editUsername = findViewById(R.id.txtUsername);
        editPassword = findViewById(R.id.txtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(editUsername.getText()) || TextUtils.isEmpty(editPassword.getText())) {

                    if (TextUtils.isEmpty(editUsername.getText())) {

                        editUsername.setError("Enter your username.");
                    }
                    if (TextUtils.isEmpty(editPassword.getText())) {

                        editPassword.setError("Enter your password.");
                    }
                }else{

                    Call<Boolean> user = userRepository.login(editUsername.getText().toString().trim(), editPassword.getText().toString().trim());

                    user.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                            if(response.body().equals(true)) {

                                Snackbar.make(linearLayout, "Logged in Successfully.", Snackbar.LENGTH_LONG).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }
                                }, 2000);

                            }else{

                                Snackbar.make(linearLayout, "Wrong Credentials.", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Snackbar.make(linearLayout, "Service Unreachable.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
