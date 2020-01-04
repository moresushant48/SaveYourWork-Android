package io.moresushant48.saveyourwork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveyourwork.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity implements Button.OnClickListener {

    TextView txtLogin;
    TextInputLayout editEmailLayout, editUsernameLayout, editPasswordLayout;
    TextInputEditText editEmail, editUsername, editPassword;
    Button btnRegister;
    String email, username, password;
    private RetrofitConfig retrofitConfig;
    private Repository repository;
    private LinearLayout linearLayout;
    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        retrofitConfig = new RetrofitConfig();

        repository = retrofitConfig.getRetrofit().create(Repository.class);

        linearLayout = findViewById(R.id.activity_register);
        spinKitView = linearLayout.findViewById(R.id.spin_kit);

        txtLogin = findViewById(R.id.txtLogin);

        editEmailLayout = findViewById(R.id.editEmailLayout);
        editUsernameLayout = findViewById(R.id.editUsernameLayout);
        editPasswordLayout = findViewById(R.id.editPasswordLayout);
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

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (validateInputs()) {

            Call<String> register = repository.register(email,
                    username, password);

            register.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    String result = Objects.requireNonNull(response.body());

                    if (result.equals(email) || result.equals(username)) {

                        if (result.equals(email)) {
                            editEmail.setError("Account exists for your Email.");
                        }
                        if (result.equals(username)) {
                            editUsername.setError("Username is already taken.");
                        }
                    } else {

                        activateSpinKit();
                        Snackbar.make(linearLayout, result, Snackbar.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                deactivateSpinKit();

                                finish();
                            }
                        }, 1500);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Snackbar.make(linearLayout, "Service Unreachable.", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validateInputs() {

        editEmail.setError(null);
        editUsername.setError(null);
        editPassword.setError(null);

        email = editEmail.getText().toString().trim();
        username = editUsername.getText().toString().trim();
        password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Enter an E-Mail.");
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            editUsername.setError("Enter a Username.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Enter a Password.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter valid E-Mail Format.");
            return false;
        }
        if (!username.trim().matches("[a-zA-Z0-9]{4,15}")) {
            editUsername.setError("Min 4 & Max 15, letters & digits.");
            return false;
        }
        if (!password.trim().matches("[a-zA-Z0-9]{3,15}")) {
            editPassword.setError("Min 3 & Max 15, letters & digits.");
            return false;
        }

        return true;
    }

    private void activateSpinKit() {
        editEmailLayout.setVisibility(View.INVISIBLE);
        editUsernameLayout.setVisibility(View.INVISIBLE);
        editPasswordLayout.setVisibility(View.INVISIBLE);
        editEmail.setVisibility(View.INVISIBLE);
        editUsername.setVisibility(View.INVISIBLE);
        editPassword.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        txtLogin.setVisibility(View.INVISIBLE);
        spinKitView.setVisibility(View.VISIBLE);
    }

    private void deactivateSpinKit() {
        editEmailLayout.setVisibility(View.VISIBLE);
        editUsernameLayout.setVisibility(View.VISIBLE);
        editPasswordLayout.setVisibility(View.VISIBLE);
        editEmail.setVisibility(View.VISIBLE);
        editUsername.setVisibility(View.VISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        txtLogin.setVisibility(View.VISIBLE);
        spinKitView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {

    }

}
