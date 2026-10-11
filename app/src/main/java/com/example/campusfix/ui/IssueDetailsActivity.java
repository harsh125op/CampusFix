package com.example.campusfix.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusfix.R;
import com.example.campusfix.db.DatabaseHelper;
import com.example.campusfix.model.Report;
import com.example.campusfix.model.StatusHistory;
import com.example.campusfix.util.DateUtils;
import com.example.campusfix.util.FileUtil;
import com.example.campusfix.util.NotificationHelper;
import com.example.campusfix.util.PreferencesManager;

import java.util.Calendar;
import java.util.List;

public class IssueDetailsActivity extends AppCompatActivity {

    private TextView tvId;
    private TextView tvStatus;
    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvLocation;
    private TextView tvPriority;
    private TextView tvHazardBadge;
    private TextView tvEmail;
    private TextView tvDesc;
    private ImageView ivPhoto;

    private View cardAdminControls;
    private Spinner spinnerAdminStatus;
    private EditText etAdminNotes;
    private TextView tvSelectedDateTime;

    private LinearLayout containerStatusHistory;

    private DatabaseHelper dbHelper;
    private PreferencesManager prefsManager;

    private String reportId;
    private Report currentReport;
    private Calendar customCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Issue Details & History");
        }

        dbHelper = DatabaseHelper.getInstance(this);
        prefsManager = new PreferencesManager(this);

        reportId = getIntent().getStringExtra("report_id");
        if (reportId == null) {
            Toast.makeText(this, "Report ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState != null) {
            long savedTime = savedInstanceState.getLong("custom_timestamp", 0);
            if (savedTime > 0) {
                customCalendar.setTimeInMillis(savedTime);
            }
        }

        initViews();
        loadReportDetails();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("custom_timestamp", customCalendar.getTimeInMillis());
    }

    private void initViews() {
        tvId = findViewById(R.id.tv_detail_id);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvPriority = findViewById(R.id.tv_detail_priority);
        tvHazardBadge = findViewById(R.id.tv_detail_hazard_badge);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvDesc = findViewById(R.id.tv_detail_desc);
        ivPhoto = findViewById(R.id.iv_detail_photo);

        cardAdminControls = findViewById(R.id.card_admin_controls);
        spinnerAdminStatus = findViewById(R.id.spinner_admin_status);
        etAdminNotes = findViewById(R.id.et_admin_notes);
        tvSelectedDateTime = findViewById(R.id.tv_picker_selected_datetime);

        containerStatusHistory = findViewById(R.id.container_status_history);

        setupAdminControls();
    }

    private void setupAdminControls() {
        if (prefsManager.isAdminMode()) {
            cardAdminControls.setVisibility(View.VISIBLE);

            String[] statuses = new String[]{"OPEN", "IN_PROGRESS", "RESOLVED", "REJECTED"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdminStatus.setAdapter(adapter);

            updateDateTimeDisplay();

            findViewById(R.id.btn_pick_datetime).setOnClickListener(v -> showDatePicker());
            findViewById(R.id.btn_admin_update_status).setOnClickListener(v -> applyAdminStatusUpdate());
        } else {
            cardAdminControls.setVisibility(View.GONE);
        }
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    customCalendar.set(Calendar.YEAR, year);
                    customCalendar.set(Calendar.MONTH, month);
                    customCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    customCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    customCalendar.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        tvSelectedDateTime.setText("Timestamp: " + DateUtils.formatDateTime(customCalendar.getTimeInMillis()));
    }

    private void applyAdminStatusUpdate() {
        if (currentReport == null) return;

        String newStatus = (String) spinnerAdminStatus.getSelectedItem();
        String notes = etAdminNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            notes = "Status updated to " + newStatus + " by Administrator.";
        }

        long timestamp = customCalendar.getTimeInMillis();
        boolean success = dbHelper.updateReportStatus(reportId, newStatus, notes, "Admin User", timestamp);

        if (success) {
            NotificationHelper.sendStatusChangeNotification(this, reportId, currentReport.getTitle(), newStatus);
            Toast.makeText(this, "Status updated successfully!", Toast.LENGTH_SHORT).show();
            etAdminNotes.setText("");
            loadReportDetails();
        } else {
            Toast.makeText(this, "Failed to update status in database.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReportDetails() {
        currentReport = dbHelper.getReportById(reportId);
        if (currentReport == null) {
            Toast.makeText(this, "Failed to load report details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvId.setText(currentReport.getId());
        tvTitle.setText(currentReport.getTitle());
        tvCategory.setText(currentReport.getCategory());
        tvLocation.setText(currentReport.getLocation());
        tvEmail.setText("Contact: " + currentReport.getContactEmail());
        tvDesc.setText(currentReport.getDescription());

        // Priority
        String priority = currentReport.getPriority() != null ? currentReport.getPriority().toUpperCase() : "MEDIUM";
        tvPriority.setText(priority);

        // Hazard Badge
        if (currentReport.isHazard()) {
            tvHazardBadge.setVisibility(View.VISIBLE);
        } else {
            tvHazardBadge.setVisibility(View.GONE);
        }

        // Status Badge
        String status = currentReport.getStatus() != null ? currentReport.getStatus().toUpperCase() : "OPEN";
        tvStatus.setText(status);

        // Photo Display
        if (currentReport.getImagePath() != null && !currentReport.getImagePath().isEmpty()) {
            Bitmap bitmap = FileUtil.loadScaledBitmap(currentReport.getImagePath(), 800, 600);
            if (bitmap != null) {
                ivPhoto.setImageBitmap(bitmap);
                ivPhoto.setVisibility(View.VISIBLE);
            } else {
                ivPhoto.setVisibility(View.GONE);
            }
        } else {
            ivPhoto.setVisibility(View.GONE);
        }

        loadStatusHistory();
    }

    private void loadStatusHistory() {
        containerStatusHistory.removeAllViews();
        List<StatusHistory> historyList = dbHelper.getStatusHistoryForReport(reportId);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (StatusHistory sh : historyList) {
            View itemView = inflater.inflate(R.layout.item_status_history, containerStatusHistory, false);

            TextView tvHStatus = itemView.findViewById(R.id.tv_history_status);
            TextView tvHTime = itemView.findViewById(R.id.tv_history_time);
            TextView tvHNotes = itemView.findViewById(R.id.tv_history_notes);
            TextView tvHAuthor = itemView.findViewById(R.id.tv_history_author);

            tvHStatus.setText(sh.getStatus());
            tvHTime.setText(DateUtils.formatDateTime(sh.getTimestamp()));
            tvHNotes.setText(sh.getNotes());
            tvHAuthor.setText("Updated by: " + (sh.getUpdatedBy() != null ? sh.getUpdatedBy() : "System"));

            containerStatusHistory.addView(itemView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
