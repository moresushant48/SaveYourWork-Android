package io.moresushant48.saveyourwork;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saveyourwork.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ResetPasswordBottomModal extends BottomSheetDialogFragment {

    private int userId;
    private String userPassword;

    private TextInputEditText currentPassword, newPassword;
    private Button confirm;
    private TextView txtRulesPassword;

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = Objects.requireNonNull(getContext()).getSharedPreferences("user", MODE_PRIVATE).getInt("id", -1);
        userPassword = Objects.requireNonNull(getContext()).getSharedPreferences("user", MODE_PRIVATE).getString("password", "null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_bottom_modal, container, false);

        currentPassword = view.findViewById(R.id.txtCurrentPassword);
        newPassword = view.findViewById(R.id.txtPassword);
        txtRulesPassword = view.findViewById(R.id.rulesPassword);
        confirm = view.findViewById(R.id.btnResetPasswordConfirm);

        confirm.setEnabled(false);

        currentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Objects.requireNonNull(currentPassword.getText()).toString().trim().equals(userPassword)) {
                    confirm.setEnabled(true);
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strNewPassword = Objects.requireNonNull(newPassword.getText()).toString().trim();

                if (TextUtils.isEmpty(strNewPassword) || strNewPassword.length() < 3) {
                    txtRulesPassword.setTextColor(Color.RED);
                } else {

                    resetPassword(strNewPassword);
                }
            }
        });

        return view;
    }

    private void resetPassword(final String newPassword) {

        confirm.setEnabled(false);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        Call<Boolean> resetPassword = repository.resetPassword(userId, newPassword);
        resetPassword.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (Objects.equals(response.body(), true)) {
                    Toast.makeText(getContext(), "Password changed successfully.", Toast.LENGTH_LONG).show();
                    Objects.requireNonNull(getContext()).getSharedPreferences("user", MODE_PRIVATE).edit().putString("password", newPassword).apply();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Couldn't change password.", Toast.LENGTH_LONG).show();
                    confirm.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getContext(), "Service unreachable.", Toast.LENGTH_LONG).show();
                confirm.setEnabled(true);
            }
        });

    }
}
