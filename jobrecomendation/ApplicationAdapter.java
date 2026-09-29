package com.example.jobrecomendation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private Context context;
    private FirebaseFirestore db;

    public ApplicationAdapter(List<Application> applicationList, Context context) {
        this.applicationList = applicationList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applicationList.get(position);
        holder.nameText.setText(app.getSeekerName());
        holder.jobText.setText(app.getJobTitle());
        holder.statusText.setText("Status: " + app.getStatus());

        if ("shortlisted".equals(app.getStatus())) {
            holder.statusText.setTextColor(context.getColor(R.color.green));
        }

        holder.shortlistButton.setOnClickListener(v -> {
            db.collection("applications").document(app.getId())
                    .update("status", "shortlisted")
                    .addOnSuccessListener(aVoid -> {
                        app.setStatus("shortlisted");
                        holder.statusText.setText("Status: shortlisted");
                        holder.statusText.setTextColor(context.getColor(R.color.green));
                    });
        });

        holder.rejectButton.setOnClickListener(v -> {
            db.collection("applications").document(app.getId())
                    .update("status", "rejected")
                    .addOnSuccessListener(aVoid -> {
                        app.setStatus("rejected");
                        holder.statusText.setText("Status: rejected");
                        holder.statusText.setTextColor(context.getColor(R.color.red));
                    });
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, jobText, statusText;
        Button shortlistButton, rejectButton;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.seekerNameText);
            jobText = itemView.findViewById(R.id.jobTitleText);
            statusText = itemView.findViewById(R.id.statusText);
            shortlistButton = itemView.findViewById(R.id.shortlistButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}