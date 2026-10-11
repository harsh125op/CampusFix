package com.example.campusfix.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.campusfix.R;
import com.example.campusfix.util.NotificationHelper;
import com.example.campusfix.util.PreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private BottomNavigationView bottomNav;
    private PreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PreferencesManager(this);
        NotificationHelper.createNotificationChannel(this);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_report) {
                selectedFragment = new NewReportFragment();
            } else if (itemId == R.id.nav_my_reports) {
                selectedFragment = new MyReportsFragment();
            } else if (itemId == R.id.nav_help) {
                selectedFragment = new HelpDirectoryFragment();
            } else if (itemId == R.id.nav_guidelines) {
                selectedFragment = new GuidelinesFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }

        checkAndRequestPermissions();
    }

    public void navigateToFragment(Fragment fragment, int navItemId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        bottomNav.setSelectedItemId(navItemId);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE
                );
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        MenuItem adminItem = menu.findItem(R.id.action_admin_mode);
        if (adminItem != null) {
            adminItem.setChecked(prefsManager.isAdminMode());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin_mode) {
            boolean newState = !item.isChecked();
            item.setChecked(newState);
            prefsManager.setAdminMode(newState);
            Toast.makeText(this, "Admin Mode: " + (newState ? "ENABLED (Status editing unlocked)" : "DISABLED"), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_reset_demo) {
            com.example.campusfix.db.DatabaseHelper.getInstance(this).resetDemoData();
            Toast.makeText(this, "Sample demo data reset successfully. User reports preserved.", Toast.LENGTH_SHORT).show();
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current != null) {
                current.onResume();
            }
            return true;
        } else if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_user_name);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_user_email);
        SwitchMaterial switchNotifs = dialogView.findViewById(R.id.switch_notifications);

        etName.setText(prefsManager.getUserName());
        etEmail.setText(prefsManager.getUserEmail());
        switchNotifs.setChecked(prefsManager.isNotificationsEnabled());

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_user_preferences)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

                    if (!name.isEmpty()) prefsManager.setUserName(name);
                    if (!email.isEmpty()) prefsManager.setUserEmail(email);
                    prefsManager.setNotificationsEnabled(switchNotifs.isChecked());

                    Toast.makeText(MainActivity.this, "Preferences Saved!", Toast.LENGTH_SHORT).show();

                    // Refresh active fragment if Dashboard
                    Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (current instanceof DashboardFragment) {
                        ((DashboardFragment) current).onResume();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
