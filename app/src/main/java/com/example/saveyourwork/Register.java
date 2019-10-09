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

import com.example.saveyourwork.Model.RetrofitConfig;
import com.example.saveyourwork.Model.User;
import com.example.saveyourwork.Model.UserRepository;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private RetrofitConfig retrofitConfig;
    private UserRepository userRepository;

    TextView txtLogin;
    EditText editEmail, editUsername, editPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        retrofitConfig = new RetrofitConfig();

        userRepository = retrofitConfig.getRetrofit().create(UserRepository.class);

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

                if(TextUtils.isEmpty(editEmail.getText()) && TextUtils.isEmpty(editUsername.getText()) && TextUtils.isEmpty(editPassword.getText())){

                    editEmail.setError("Enter an E-Mail.");
                    editPassword.setError("Enter a Username.");
                    editPassword.setError("Enter a Password.");

                }else {

                    Call<User> user = userRepository.register(editEmail.getText().toString().trim(),
                            editUsername.getText().toString().trim(), editPassword.getText().toString().trim());

                    user.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            if(response.body().getUsername().equals(editUsername.getText().toString().trim())){
                                Snackbar.make(linearLayout, "Registration Successful : " + response.body().getUsername(), Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                            Snackbar.make(linearLayout, "Registration failed.",Snackbar.LENGTH_LONG).show();

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
