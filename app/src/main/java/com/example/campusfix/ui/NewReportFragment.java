package com.example.campusfix.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.campusfix.R;
import com.example.campusfix.db.DatabaseHelper;
import com.example.campusfix.model.Report;
import com.example.campusfix.util.DateUtils;
import com.example.campusfix.util.FileUtil;
import com.example.campusfix.util.NotificationHelper;
import com.example.campusfix.util.PreferencesManager;
import com.example.campusfix.util.PriorityRules;

import java.io.File;

public class NewReportFragment extends Fragment {

    private EditText etTitle;
    private EditText etLocation;
    private EditText etDesc;
    private Spinner spinnerCategory;
    private Spinner spinnerOverridePriority;
    private CheckBox chkHazard;
    private TextView tvPriorityResult;
    private ImageView ivPreview;

    private DatabaseHelper dbHelper;
    private PreferencesManager prefsManager;

    private String currentImagePath = null;
    private Uri cameraImageUri = null;
    private String calculatedPriority = "MEDIUM";

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public NewReportFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (cameraImageUri != null) {
                            currentImagePath = FileUtil.saveImageFromUri(requireContext(), cameraImageUri);
                            displayImagePreview(currentImagePath);
                        }
                    }
                }
        );

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            currentImagePath = FileUtil.saveImageFromUri(requireContext(), selectedUri);
                            displayImagePreview(currentImagePath);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        prefsManager = new PreferencesManager(requireContext());

        etTitle = view.findViewById(R.id.et_report_title);
        etLocation = view.findViewById(R.id.et_report_location);
        etDesc = view.findViewById(R.id.et_report_desc);
        spinnerCategory = view.findViewById(R.id.spinner_new_category);
        spinnerOverridePriority = view.findViewById(R.id.spinner_override_priority);
        chkHazard = view.findViewById(R.id.chk_hazard);
        tvPriorityResult = view.findViewById(R.id.tv_priority_assessment_result);
        ivPreview = view.findViewById(R.id.iv_photo_preview);

        setupSpinners();
        setupInputListeners();

        if (savedInstanceState != null) {
            currentImagePath = savedInstanceState.getString("current_image_path");
            String cameraUriStr = savedInstanceState.getString("camera_image_uri");
            if (cameraUriStr != null) {
                cameraImageUri = Uri.parse(cameraUriStr);
            }
            calculatedPriority = savedInstanceState.getString("calculated_priority", "MEDIUM");
            displayImagePreview(currentImagePath);
        }

        view.findViewById(R.id.btn_take_photo).setOnClickListener(v -> launchCamera());
        view.findViewById(R.id.btn_pick_photo).setOnClickListener(v -> launchGallery());
        view.findViewById(R.id.btn_submit_report).setOnClickListener(v -> submitReport());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentImagePath != null) {
            outState.putString("current_image_path", currentImagePath);
        }
        if (cameraImageUri != null) {
            outState.putString("camera_image_uri", cameraImageUri.toString());
        }
        outState.putString("calculated_priority", calculatedPriority);
    }

    private void setupSpinners() {
        String[] categories = new String[]{"Electrical", "Plumbing", "Furniture", "HVAC", "Safety/Hazard", "IT/Network", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        String[] priorities = new String[]{"(Auto Priority)", "LOW", "MEDIUM", "HIGH", "CRITICAL"};
        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, priorities);
        prioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOverridePriority.setAdapter(prioAdapter);
    }

    private void setupInputListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runPriorityAssessment();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etTitle.addTextChangedListener(watcher);
        etLocation.addTextChangedListener(watcher);
        etDesc.addTextChangedListener(watcher);

        chkHazard.setOnCheckedChangeListener((buttonView, isChecked) -> runPriorityAssessment());

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                runPriorityAssessment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerOverridePriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    calculatedPriority = (String) spinnerOverridePriority.getSelectedItem();
                    tvPriorityResult.setText("Priority (Overridden): " + calculatedPriority);
                } else {
                    runPriorityAssessment();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void runPriorityAssessment() {
        if (spinnerOverridePriority.getSelectedItemPosition() > 0) {
            return; // overridden by user
        }

        String cat = (String) spinnerCategory.getSelectedItem();
        String loc = etLocation.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        boolean isHazard = chkHazard.isChecked();

        PriorityRules.AssessmentResult result = PriorityRules.evaluatePriority(cat, loc, isHazard, desc);
        calculatedPriority = result.getPriority();
        tvPriorityResult.setText("Calculated Priority: " + result.getPriority() + "\nRule: " + result.getReason());
    }

    private void launchCamera() {
        File photoFile = FileUtil.createImageFile(requireContext());
        if (photoFile != null) {
            cameraImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            try {
                cameraLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Camera not available on this device.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void displayImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Bitmap bitmap = FileUtil.loadScaledBitmap(imagePath, 800, 600);
            if (bitmap != null) {
                ivPreview.setImageBitmap(bitmap);
                ivPreview.setVisibility(View.VISIBLE);
                return;
            }
        }
        ivPreview.setVisibility(View.GONE);
    }

    private void submitReport() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String category = (String) spinnerCategory.getSelectedItem();
        boolean isHazard = chkHazard.isChecked();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            etDesc.setError("Description is required");
            etDesc.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();
        String uniqueId = DateUtils.generateUniqueReportId();
        String userEmail = prefsManager.getUserEmail();

        Report newReport = new Report(
                uniqueId,
                title,
                desc,
                category,
                location,
                calculatedPriority,
                "OPEN",
                currentImagePath,
                now,
                now,
                isHazard,
                userEmail,
                false // isDemo = false for user created report
        );

        boolean success = dbHelper.insertReport(newReport);

        if (success) {
            NotificationHelper.sendReportCreatedNotification(requireContext(), uniqueId, title);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Report Submitted Successfully!")
                    .setMessage("Your unique Report ID is:\n\n" + uniqueId + "\n\nStored offline in local database.")
                    .setPositiveButton("View My Reports", (dialog, which) -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).navigateToFragment(new MyReportsFragment(), R.id.nav_my_reports);
                        }
                    })
                    .setNegativeButton("Submit Another", (dialog, which) -> clearForm())
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(requireContext(), "Failed to save report to database. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void clearForm() {
        etTitle.setText("");
        etLocation.setText("");
        etDesc.setText("");
        chkHazard.setChecked(false);
        spinnerCategory.setSelection(0);
        spinnerOverridePriority.setSelection(0);
        currentImagePath = null;
        ivPreview.setVisibility(View.GONE);
        runPriorityAssessment();
    }
}
