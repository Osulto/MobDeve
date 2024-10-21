package com.mobdeve.s19.mobdev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {

    private List<Update> updates;

    public UpdateAdapter(List<Update> updates) {
        this.updates = updates;
    }

    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_update, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {
        Update update = updates.get(position);
        holder.bind(update);
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    static class UpdateViewHolder extends RecyclerView.ViewHolder {
        TextView tvUpdateTitle, tvUpdateDescription, tvUpdateDate;

        UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpdateTitle = itemView.findViewById(R.id.tvUpdateTitle);
            tvUpdateDescription = itemView.findViewById(R.id.tvUpdateDescription);
            tvUpdateDate = itemView.findViewById(R.id.tvUpdateDate);
        }

        void bind(Update update) {
            tvUpdateTitle.setText(update.getTitle());
            tvUpdateDescription.setText(update.getDescription());
            tvUpdateDate.setText(update.getDate());
        }
    }
}