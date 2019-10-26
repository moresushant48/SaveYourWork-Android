package com.example.saveyourwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saveyourwork.Config.RetrofitConfig;
import com.example.saveyourwork.Repository.Repository;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    TextView txtLogin;
    EditText editEmail, editUsername, editPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        retrofitConfig = new RetrofitConfig();

        repository = retrofitConfig.getRetrofit().create(Repository.class);

        final LinearLayout linearLayout = findViewById(R.id.activity_register);

        txtLogin = findViewById(R.id.txtLogin);

        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        btnRegister = findViewById(R.id.btnRegister);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(editEmail.getText()) || TextUtils.isEmpty(editUsername.getText()) || TextUtils.isEmpty(editPassword.getText())){

                    if(TextUtils.isEmpty(editEmail.getText())) {
                        editEmail.setError("Enter an E-Mail.");
                    }
                    if(TextUtils.isEmpty(editUsername.getText())) {
                        editPassword.setError("Enter a Username.");
                    }
                    if(TextUtils.isEmpty(editPassword.getText())) {
                        editPassword.setError("Enter a Password.");
                    }

                }else {

                    Call<String> register = repository.register(editEmail.getText().toString().trim(),
                            editUsername.getText().toString().trim(), editPassword.getText().toString().trim());

                    register.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if(response.body().equals(editEmail.getText().toString().trim()) || response.body().equals(editUsername.getText().toString().trim())) {

                                if (response.body().equals(editEmail.getText().toString().trim())) {
                                    editEmail.setError("Account exists for your Email.");
                                }
                                if (response.body().equals(editUsername.getText().toString().trim())) {
                                    editUsername.setError("Username is already taken.");
                                }
                            }else {

                                Snackbar.make(linearLayout, response.body(), Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

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
