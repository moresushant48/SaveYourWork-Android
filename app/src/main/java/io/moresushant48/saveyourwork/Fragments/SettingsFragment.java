package io.moresushant48.saveyourwork.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.saveyourwork.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)  {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        SwitchPreference switchPreference = findPreference(getString(R.string.darkMode));
        Objects.requireNonNull(switchPreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Intent i = Objects.requireNonNull(getContext()).getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
                Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                return true;
            }
        });
    }
}
