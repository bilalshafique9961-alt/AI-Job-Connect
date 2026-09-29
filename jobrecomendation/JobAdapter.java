package com.example.jobrecomendation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList;
    private Context context;

    public JobAdapter(List<Job> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.titleText.setText(job.getTitle());
        holder.companyText.setText(job.getCompany());
        holder.locationText.setText(job.getLocation());
        holder.salaryText.setText(job.getSalary());

        int matchScore = job.getMatchScore();
        holder.matchScoreText.setText(matchScore + "% Match");

        if (matchScore >= 70) {
            holder.matchScoreText.setTextColor(context.getColor(R.color.green));
        } else if (matchScore >= 40) {
            holder.matchScoreText.setTextColor(context.getColor(R.color.teal_200));
        } else {
            holder.matchScoreText.setTextColor(context.getColor(R.color.red));
        }

        holder.applyButton.setOnClickListener(v -> {
            Toast.makeText(context, "Applied to " + job.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, companyText, locationText, salaryText, matchScoreText;
        Button applyButton;
        CardView cardView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.jobTitleText);
            companyText = itemView.findViewById(R.id.companyText);
            locationText = itemView.findViewById(R.id.locationText);
            salaryText = itemView.findViewById(R.id.salaryText);
            matchScoreText = itemView.findViewById(R.id.matchScoreText);
            applyButton = itemView.findViewById(R.id.applyButton);
            cardView = itemView.findViewById(R.id.jobCardView);
        }
    }
}