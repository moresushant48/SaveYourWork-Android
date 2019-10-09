package com.example.saveyourwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saveyourwork.Model.RetrofitConfig;
import com.example.saveyourwork.Model.User;
import com.example.saveyourwork.Model.UserRepository;
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

                if(TextUtils.isEmpty(editUsername.getText()) && TextUtils.isEmpty(editPassword.getText())){

                    editUsername.setError("Enter your username.");
                    editPassword.setError("Enter your password.");

                }else {

                    Call<User> user = userRepository.login(editUsername.getText().toString().trim());

                    user.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            if(response.body().getUsername().equals(editUsername.getText().toString().trim())){

                                // startActivity(new Intent(Login.this, MainActivity.class));
                                Login.this.finish();

                            }

                            Snackbar.make(linearLayout, "Response : " + response.body().getUsername(), Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(linearLayout, "Fail : " + t.getMessage(), Snackbar.LENGTH_LONG).show();
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
