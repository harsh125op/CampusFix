package com.example.campusfix.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.campusfix.R;
import com.example.campusfix.model.Report;
import com.example.campusfix.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Report> reportList;

    public ReportListAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.reportList = reportList != null ? reportList : new ArrayList<>();
    }

    public void updateData(List<Report> newList) {
        this.reportList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reportList.size();
    }

    @Override
    public Object getItem(int position) {
        return reportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvReportId;
        TextView tvPriority;
        TextView tvDemoBadge;
        TextView tvStatus;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvLocation;
        TextView tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_report, parent, false);
            holder = new ViewHolder();
            holder.tvReportId = convertView.findViewById(R.id.tv_item_report_id);
            holder.tvPriority = convertView.findViewById(R.id.tv_item_priority);
            holder.tvDemoBadge = convertView.findViewById(R.id.tv_item_demo_badge);
            holder.tvStatus = convertView.findViewById(R.id.tv_item_status);
            holder.tvTitle = convertView.findViewById(R.id.tv_item_title);
            holder.tvCategory = convertView.findViewById(R.id.tv_item_category);
            holder.tvLocation = convertView.findViewById(R.id.tv_item_location);
            holder.tvDate = convertView.findViewById(R.id.tv_item_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Report report = reportList.get(position);

        holder.tvReportId.setText(report.getId());
        holder.tvTitle.setText(report.getTitle());
        holder.tvCategory.setText(report.getCategory());
        holder.tvLocation.setText(report.getLocation());
        holder.tvDate.setText(DateUtils.formatDate(report.getCreatedAt()));

        if (report.isDemo()) {
            holder.tvDemoBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvDemoBadge.setVisibility(View.GONE);
        }

        // Priority
        String priority = report.getPriority() != null ? report.getPriority().toUpperCase() : "MEDIUM";
        holder.tvPriority.setText(priority);
        switch (priority) {
            case "CRITICAL":
                holder.tvPriority.setTextColor(context.getResources().getColor(R.color.priority_critical, null));
                break;
            case "HIGH":
                holder.tvPriority.setTextColor(context.getResources().getColor(R.color.priority_high, null));
                break;
            case "LOW":
                holder.tvPriority.setTextColor(context.getResources().getColor(R.color.priority_low, null));
                break;
            default:
                holder.tvPriority.setTextColor(context.getResources().getColor(R.color.priority_medium, null));
                break;
        }

        // Status
        String status = report.getStatus() != null ? report.getStatus().toUpperCase() : "OPEN";
        holder.tvStatus.setText(status);
        switch (status) {
            case "RESOLVED":
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_resolved, null));
                break;
            case "IN_PROGRESS":
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_in_progress, null));
                break;
            case "REJECTED":
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected, null));
                break;
            default:
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_open, null));
                break;
        }

        return convertView;
    }
}
