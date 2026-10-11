package com.example.campusfix.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.R;
import com.example.campusfix.model.HelpContact;

import java.util.List;

public class HelpContactAdapter extends RecyclerView.Adapter<HelpContactAdapter.ViewHolder> {

    private final Context context;
    private final List<HelpContact> contactList;

    public HelpContactAdapter(Context context, List<HelpContact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_help_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpContact contact = contactList.get(position);

        holder.tvName.setText(contact.getName());
        holder.tvRole.setText(contact.getRole());
        holder.tvLocation.setText("Location: " + contact.getLocation());
        holder.tvDesc.setText(contact.getDescription());

        // Call Action via Implicit Intent ACTION_DIAL
        holder.btnCall.setOnClickListener(v -> {
            if (contact.getPhone() != null && !contact.getPhone().isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                context.startActivity(dialIntent);
            } else {
                Toast.makeText(context, "Phone number unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        // Share Action via Implicit Intent ACTION_SEND
        holder.btnShare.setOnClickListener(v -> {
            String shareContent = "Campus Help Directory - " + contact.getName() + "\n" +
                    "Role: " + contact.getRole() + "\n" +
                    "Phone: " + contact.getPhone() + "\n" +
                    "Email: " + contact.getEmail() + "\n" +
                    "Location: " + contact.getLocation();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Campus Contact: " + contact.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            context.startActivity(Intent.createChooser(shareIntent, "Share Contact via"));
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvLocation, tvDesc;
        View btnCall, btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvRole = itemView.findViewById(R.id.tv_contact_role);
            tvLocation = itemView.findViewById(R.id.tv_contact_location);
            tvDesc = itemView.findViewById(R.id.tv_contact_desc);
            btnCall = itemView.findViewById(R.id.btn_call_contact);
            btnShare = itemView.findViewById(R.id.btn_share_contact);
        }
    }
}
