package com.mobdeve.s19.stocksmart;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.mobdeve.s19.stocksmart.database.models.Update;


public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {

    private List<Update> updates;

    public UpdateAdapter(List<Update> updates) {
        this.updates = updates;
    }

    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_update, parent, false);
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
        private TextView tvProductName;
        private TextView tvDescription;
        private TextView tvDate;
        private ImageView ivIcon;

        UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

        void bind(Update update) {
            tvProductName.setText(update.getProductName());
            tvDescription.setText(update.getDescription());
            tvDate.setText(update.getDate());

            // Set icon and color based on update type
            int iconResId;
            int colorResId;

            switch (update.getType()) {
                case STOCK_ADDED:
                    iconResId = R.drawable.ic_add_circle;
                    colorResId = R.color.success;
                    break;
                case STOCK_REMOVED:
                    iconResId = R.drawable.ic_remove_circle;
                    colorResId = R.color.warning;
                    break;
                case LOW_STOCK_ALERT:
                    iconResId = R.drawable.ic_warning;
                    colorResId = R.color.error;
                    break;
                case PRODUCT_ADDED:
                    iconResId = R.drawable.ic_new_product;
                    colorResId = R.color.primary;
                    break;
                case PRODUCT_REMOVED:
                    iconResId = R.drawable.ic_delete;
                    colorResId = R.color.error;
                    break;
                default:
                    iconResId = R.drawable.ic_update;
                    colorResId = R.color.primary;
                    break;
            }

            ivIcon.setImageResource(iconResId);
            ivIcon.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), colorResId)));
        }
    }
}