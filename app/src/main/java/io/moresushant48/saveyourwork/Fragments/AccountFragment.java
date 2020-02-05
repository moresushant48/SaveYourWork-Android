package io.moresushant48.saveyourwork.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.saveyourwork.R;

import java.util.Objects;

import io.moresushant48.saveyourwork.ResetPasswordBottomModal;

public class AccountFragment extends Fragment {

    private Context context;

    private LinearLayout layout1, layout2;
    private TextView heading, data;
    private Button btnResetPassword;

    private int id;
    private String username;
    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = Objects.requireNonNull(getContext());

        id = context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("id", -1);
        username = getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "null");
        email = getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", "null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(getString(R.string.fragAccount));

        // for Username.
        layout1 = view.findViewById(R.id.account_item_1);
        heading = layout1.findViewById(R.id.txtAccountItemHeading);
        data = layout1.findViewById(R.id.txtAccountItemData);

        heading.setText("Username");
        data.setText(username);

        // for Email.
        layout2 = view.findViewById(R.id.account_item_2);
        heading = layout2.findViewById(R.id.txtAccountItemHeading);
        data = layout2.findViewById(R.id.txtAccountItemData);

        heading.setText("Email");
        data.setText(email);

        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResetPasswordBottomModal().show(Objects.requireNonNull(getFragmentManager()), "resetPasswordBottomModal");
            }
        });

        return view;
    }
}
