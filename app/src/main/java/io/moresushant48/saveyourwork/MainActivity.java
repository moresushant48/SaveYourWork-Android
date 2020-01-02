package io.moresushant48.saveyourwork;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.saveyourwork.R;
import com.google.android.material.navigation.NavigationView;

import io.moresushant48.saveyourwork.Fragments.AboutFragment;
import io.moresushant48.saveyourwork.Fragments.AccountFragment;
import io.moresushant48.saveyourwork.Fragments.FilesFragment;
import io.moresushant48.saveyourwork.Fragments.PublicFilesFragment;
import io.moresushant48.saveyourwork.Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    boolean isLoggedIn;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.darkMode), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(R.layout.activity_main);

        askForFingerprint();

        Toolbar toolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_STORAGE_CODE);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragment_container, new FilesFragment()).commit();
            navigationView.setCheckedItem(R.id.menuFiles);
        }
    }

    private void askForFingerprint() {

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.fingerprint), false)) {
            new FingerprintHelper(MainActivity.this).startAuthenticationPrompt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // nothing special to do here.
                } else {
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.menuFiles:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new FilesFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuPublicSearch:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new PublicFilesFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuLogout:
                logout();
                break;

            case R.id.menuAbout:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new AboutFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuSettings:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new SettingsFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuAccount:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new AccountFragment()).commit();
                menuItem.setCheckable(true);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {

        getSharedPreferences("app", MODE_PRIVATE).getString("BASE_URL", getString(R.string.source_heroku));

        isLoggedIn = getSharedPreferences("user", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun || !isLoggedIn) {
            //show Login activity
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        super.onResume();
    }

    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout ?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSharedPreferences("user", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
        builder.create().show();
    }

}
