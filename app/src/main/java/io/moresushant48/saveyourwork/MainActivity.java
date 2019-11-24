package io.moresushant48.saveyourwork;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import io.moresushant48.saveyourwork.Fragments.AboutFragment;
import io.moresushant48.saveyourwork.Fragments.AccountFragment;
import io.moresushant48.saveyourwork.Fragments.FilesFragment;
import io.moresushant48.saveyourwork.Fragments.SettingsFragment;

import com.example.saveyourwork.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    boolean isLoggedIn;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.darkMode), false)){
            setTheme(R.style.DarkMode);
        }

        setContentView(R.layout.activity_main);

        askForFingerprint();

        Toolbar toolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.fragFiles);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_STORAGE_CODE);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
            navigationView.setCheckedItem(R.id.menuFiles);
        }
    }

    private void askForFingerprint(){

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.fingerprint), false))
        {
            new FingerprintHelper(MainActivity.this).startAuthenticationPrompt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case PERMISSION_STORAGE_CODE : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // nothing special to do here.
                }else{
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.menuFiles:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuLogout:
                logout();
                break;

            case R.id.menuAbout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuSettings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                menuItem.setCheckable(true);
                break;

            case R.id.menuAccount:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
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
                .getBoolean("isLoggedIn",false);

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

    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout ?");
        builder.setNegativeButton("Cancel",null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSharedPreferences("user",MODE_PRIVATE).edit().putBoolean("isLoggedIn",false).apply();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
        builder.create().show();
    }

}
