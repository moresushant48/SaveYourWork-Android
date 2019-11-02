package io.moresushant48.saveyourwork;

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

import io.moresushant48.saveyourwork.Config.RetrofitConfig;

import com.example.saveyourwork.R;

import io.moresushant48.saveyourwork.Repository.Repository;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private RetrofitConfig retrofitConfig;
    private Repository repository;
    private LinearLayout linearLayout;

    private TextView txtRegister;
    private EditText editUsername, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linearLayout = findViewById(R.id.activity_login);

        retrofitConfig = new RetrofitConfig();

        repository = retrofitConfig.getRetrofit().create(Repository.class);

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

                    Call<Integer> user = repository.login(editUsername.getText().toString().trim(), editPassword.getText().toString().trim());

                    user.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {

                            if(response.body().equals(-1)) {

                                Snackbar.make(linearLayout, "Wrong Credentials.", Snackbar.LENGTH_LONG).show();

                            }else{

                                Snackbar.make(linearLayout, "Logged in Successfully.", Snackbar.LENGTH_LONG).show();
                                getSharedPreferences("user", MODE_PRIVATE).edit().putBoolean("isLoggedIn",true).apply();
                                getSharedPreferences("user", MODE_PRIVATE).edit().putInt("id",response.body()).apply();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }
                                }, 2000);
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Snackbar.make(linearLayout, "Service Unreachable.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Snackbar.make(linearLayout, "Please Login.", Snackbar.LENGTH_LONG).show();

    }
}
