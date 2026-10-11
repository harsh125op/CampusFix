package com.example.campusfix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.campusfix.R;
import com.example.campusfix.adapter.ReportListAdapter;
import com.example.campusfix.db.DatabaseHelper;
import com.example.campusfix.model.Report;

import java.util.List;

public class MyReportsFragment extends Fragment {

    private SearchView svReports;
    private Spinner spinnerCategory;
    private Spinner spinnerStatus;
    private Spinner spinnerPriority;
    private ListView lvReports;
    private TextView tvReportCount;
    private TextView tvEmpty;

    private DatabaseHelper dbHelper;
    private ReportListAdapter adapter;

    private String currentQuery = "";
    private String currentCategory = "ALL";
    private String currentStatus = "ALL";
    private String currentPriority = "ALL";

    public MyReportsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        svReports = view.findViewById(R.id.sv_reports);
        spinnerCategory = view.findViewById(R.id.spinner_category_filter);
        spinnerStatus = view.findViewById(R.id.spinner_status_filter);
        spinnerPriority = view.findViewById(R.id.spinner_priority_filter);
        lvReports = view.findViewById(R.id.lv_reports);
        tvReportCount = view.findViewById(R.id.tv_report_count);
        tvEmpty = view.findViewById(R.id.tv_empty_reports);

        setupSpinners();
        setupSearch();

        adapter = new ReportListAdapter(requireContext(), null);
        lvReports.setAdapter(adapter);

        lvReports.setOnItemClickListener((parent, v, position, id) -> {
            Report report = (Report) adapter.getItem(position);
            if (report != null) {
                Intent intent = new Intent(requireContext(), IssueDetailsActivity.class);
                intent.putExtra("report_id", report.getId());
                startActivity(intent);
            }
        });

        loadFilteredReports();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFilteredReports();
    }

    private void setupSpinners() {
        String[] categories = new String[]{"ALL Categories", "Electrical", "Plumbing", "Furniture", "HVAC", "Safety/Hazard", "IT/Network", "Other"};
        String[] statuses = new String[]{"ALL Statuses", "OPEN", "IN_PROGRESS", "RESOLVED", "REJECTED"};
        String[] priorities = new String[]{"ALL Priorities", "LOW", "MEDIUM", "HIGH", "CRITICAL"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, priorities);
        prioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(prioAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = position == 0 ? "ALL" : categories[position];
                loadFilteredReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatus = position == 0 ? "ALL" : statuses[position];
                loadFilteredReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPriority = position == 0 ? "ALL" : priorities[position];
                loadFilteredReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        svReports.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                loadFilteredReports();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                loadFilteredReports();
                return true;
            }
        });
    }

    private void loadFilteredReports() {
        if (getContext() == null || dbHelper == null) return;

        List<Report> filteredList = dbHelper.getFilteredReports(currentQuery, currentCategory, currentStatus, currentPriority);
        adapter.updateData(filteredList);

        int count = filteredList.size();
        tvReportCount.setText("Showing " + count + " reports");

        if (count == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvReports.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvReports.setVisibility(View.VISIBLE);
        }
    }
}
