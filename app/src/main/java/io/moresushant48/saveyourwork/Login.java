package io.moresushant48.saveyourwork;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveyourwork.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Model.User;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private RetrofitConfig retrofitConfig;
    private Repository repository;
    private RelativeLayout loginLayout, formLayout;

    private SpinKitView spinKitView;
    private TextView txtRegister;
    private TextInputLayout editUsernameLayout, editPasswordLayout;
    private TextInputEditText editUsername, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = findViewById(R.id.login_layout);
        formLayout = findViewById(R.id.form);

        retrofitConfig = new RetrofitConfig();

        repository = retrofitConfig.getRetrofit().create(Repository.class);

        spinKitView = findViewById(R.id.spin_kit);

        editUsernameLayout = findViewById(R.id.txtUsernameLayout);
        editPasswordLayout = findViewById(R.id.txtPasswordLayout);
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

                if (TextUtils.isEmpty(editUsername.getText()) || TextUtils.isEmpty(editPassword.getText())) {

                    if (TextUtils.isEmpty(editUsername.getText())) {

                        editUsername.setError("Enter your username.");
                    }
                    if (TextUtils.isEmpty(editPassword.getText())) {

                        editPassword.setError("Enter your password.");
                    }
                } else {

                    activateSpinKit();

                    Call<User> user = repository.login(editUsername.getText().toString().trim(), editPassword.getText().toString().trim());

                    user.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            User tempUser = Objects.requireNonNull(response.body());

                            if (tempUser.getId() == 0) {
                                spinKitView.setColor(Color.RED);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deactivateSpinKit();
                                        Snackbar.make(loginLayout, "Wrong Credentials.", Snackbar.LENGTH_LONG).show();
                                    }
                                }, 1500);
                            } else {

                                loginSuccess(tempUser);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(loginLayout, "Service Unreachable.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void activateSpinKit() {
        formLayout.setVisibility(View.INVISIBLE);
        spinKitView.setVisibility(View.VISIBLE);
    }

    private void deactivateSpinKit() {
        formLayout.setVisibility(View.VISIBLE);
        spinKitView.setVisibility(View.INVISIBLE);
    }

    private void loginSuccess(User tempUser) {

        spinKitView.setColor(getColor(R.color.colorPrimaryDark));

        saveUserInfo(tempUser);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        }, 1500);

    }

    private void saveUserInfo(User tempUser) {

        getSharedPreferences("user", MODE_PRIVATE).edit().putBoolean("isLoggedIn", true).apply();

        getSharedPreferences("user", MODE_PRIVATE).edit().putInt("id", tempUser.getId()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("username", tempUser.getUsername()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("email", tempUser.getEmail()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("password", editPassword.getText().toString().trim()).apply();

    }

    @Override
    public void onBackPressed() {

        Snackbar.make(loginLayout, "Please Login.", Snackbar.LENGTH_LONG).show();

    }
}
