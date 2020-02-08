package io.moresushant48.saveyourwork.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.saveyourwork.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Model.User;
import io.moresushant48.saveyourwork.Repository.Repository;
import io.moresushant48.saveyourwork.ResetPasswordBottomModal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private RetrofitConfig retrofitConfig = new RetrofitConfig();
    private Repository repository;

    private Context context;

    private LinearLayout layout1, layout2;
    private TextView txtUsername, txtEmail, txtSharedKey;
    private FloatingActionButton btnResetKey;
    private ExtendedFloatingActionButton btnResetPassword;

    private int id;
    private String username;
    private String email;;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = Objects.requireNonNull(getContext());

        id = context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("id", -1);
    }

    private void hasFetchedData() {

        repository = retrofitConfig.getRetrofit().create(Repository.class);
        Call<User> userCall = repository.getUser(id);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(Objects.requireNonNull(response.body()).getId() == id){
                    user = response.body();
                    setDataIntoViews();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(getString(R.string.fragAccount));

        hasFetchedData();

        txtUsername = view.findViewById(R.id.username);
        txtEmail = view.findViewById(R.id.email);
        txtSharedKey = view.findViewById(R.id.sharedKey);

        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        btnResetKey = view.findViewById(R.id.btnResetKey);

        btnResetPassword.setOnClickListener(v ->
            new ResetPasswordBottomModal().show(Objects.requireNonNull(getFragmentManager()), "resetPasswordBottomModal")
        );

        btnResetKey.setOnClickListener(v -> {
            generateResetKey();
        });

        return view;
    }

    private void generateResetKey() {


        new AlertDialog.Builder(getContext())
                .setTitle("Generate Key")
                .setMessage("Do your really want to generate a new Shared Key ?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    repository = retrofitConfig.getRetrofit().create(Repository.class);
                    Call<String> genSharedKey = repository.genSharedKey(id);

                    genSharedKey.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            assert response.body() != null;
                            txtSharedKey.setText(response.body().trim());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                })
                .setNegativeButton("No", null)
                .setCancelable(true).create().show();
    }

    private void setDataIntoViews() {
        // for Username.
        txtUsername.setText(user.getUsername());

        // for Email.
        txtEmail.setText(user.getEmail());

        // for Shared KEY.
        txtSharedKey.setText(user.getPublicPass());
    }
}
