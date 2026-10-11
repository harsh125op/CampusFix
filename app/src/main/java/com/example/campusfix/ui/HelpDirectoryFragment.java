package com.example.campusfix.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.R;
import com.example.campusfix.adapter.HelpContactAdapter;
import com.example.campusfix.db.DatabaseHelper;
import com.example.campusfix.model.HelpContact;

import java.util.List;

public class HelpDirectoryFragment extends Fragment {

    private RecyclerView rvContacts;
    private DatabaseHelper dbHelper;

    public HelpDirectoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_directory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        rvContacts = view.findViewById(R.id.rv_help_contacts);

        rvContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<HelpContact> contacts = dbHelper.getAllHelpContacts();
        HelpContactAdapter adapter = new HelpContactAdapter(requireContext(), contacts);
        rvContacts.setAdapter(adapter);
    }
}
