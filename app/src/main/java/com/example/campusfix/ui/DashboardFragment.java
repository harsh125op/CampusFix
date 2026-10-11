package com.example.campusfix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusfix.R;
import com.example.campusfix.db.DatabaseHelper;
import com.example.campusfix.model.Report;
import com.example.campusfix.util.DateUtils;
import com.example.campusfix.util.PreferencesManager;

import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvOpenCount;
    private TextView tvInProgressCount;
    private TextView tvResolvedCount;
    private TextView tvCriticalCount;
    private LinearLayout containerRecentReports;

    private DatabaseHelper dbHelper;
    private PreferencesManager prefsManager;

    public DashboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        prefsManager = new PreferencesManager(requireContext());

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvOpenCount = view.findViewById(R.id.tv_stat_open);
        tvInProgressCount = view.findViewById(R.id.tv_stat_in_progress);
        tvResolvedCount = view.findViewById(R.id.tv_stat_resolved);
        tvCriticalCount = view.findViewById(R.id.tv_stat_critical);
        containerRecentReports = view.findViewById(R.id.container_recent_reports);

        view.findViewById(R.id.btn_quick_report).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToFragment(new NewReportFragment(), R.id.nav_report);
            }
        });

        loadDashboardData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (getContext() == null) return;

        String userName = prefsManager.getUserName();
        tvWelcome.setText("Welcome, " + userName);

        Map<String, Integer> stats = dbHelper.getDashboardStats();
        tvOpenCount.setText(String.valueOf(stats.getOrDefault("open", 0)));
        tvInProgressCount.setText(String.valueOf(stats.getOrDefault("in_progress", 0)));
        tvResolvedCount.setText(String.valueOf(stats.getOrDefault("resolved", 0)));
        tvCriticalCount.setText(String.valueOf(stats.getOrDefault("critical", 0)));

        populateRecentReports();
    }

    private void populateRecentReports() {
        containerRecentReports.removeAllViews();
        List<Report> reports = dbHelper.getAllReports();

        if (reports.isEmpty()) {
            TextView emptyTv = new TextView(getContext());
            emptyTv.setText("No recent issue reports submitted yet.");
            emptyTv.setPadding(16, 16, 16, 16);
            emptyTv.setTextColor(getResources().getColor(R.color.text_secondary, null));
            containerRecentReports.addView(emptyTv);
            return;
        }

        int max = Math.min(3, reports.size());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < max; i++) {
            Report report = reports.get(i);
            View itemView = inflater.inflate(R.layout.item_report, containerRecentReports, false);

            TextView tvId = itemView.findViewById(R.id.tv_item_report_id);
            TextView tvTitle = itemView.findViewById(R.id.tv_item_title);
            TextView tvCategory = itemView.findViewById(R.id.tv_item_category);
            TextView tvLocation = itemView.findViewById(R.id.tv_item_location);
            TextView tvStatus = itemView.findViewById(R.id.tv_item_status);
            TextView tvDate = itemView.findViewById(R.id.tv_item_date);

            tvId.setText(report.getId());
            tvTitle.setText(report.getTitle());
            tvCategory.setText(report.getCategory());
            tvLocation.setText(report.getLocation());
            tvStatus.setText(report.getStatus());
            tvDate.setText(DateUtils.formatDate(report.getCreatedAt()));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), IssueDetailsActivity.class);
                intent.putExtra("report_id", report.getId());
                startActivity(intent);
            });

            containerRecentReports.addView(itemView);
        }
    }
}
