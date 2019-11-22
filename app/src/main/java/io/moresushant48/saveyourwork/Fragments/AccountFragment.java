package io.moresushant48.saveyourwork.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.saveyourwork.R;

import java.util.Objects;

public class AccountFragment extends Fragment {

    private Context context;

    private LinearLayout layout1, layout2;
    private TextView heading, data;

    private String id;
    private String username;
    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = Objects.requireNonNull(getContext());

        id = String.valueOf(context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt("id", -1));
        username = getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("username", "null");
        email = getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", "null");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView toolbarTitle = Objects.requireNonNull(container).getRootView().findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.fragAccount);

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

        return view;
    }
}
