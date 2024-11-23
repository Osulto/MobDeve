package com.mobdeve.s19.stocksmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobdeve.s19.stocksmart.database.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private List<Category> filteredCategories;
    private final Context context;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.categories = new ArrayList<>();
        this.filteredCategories = new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(filteredCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredCategories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = new ArrayList<>(categories);
        this.filteredCategories = new ArrayList<>(categories);
        notifyDataSetChanged();
    }

    public void addCategory(Category category) {
        categories.add(category);
        filteredCategories.add(category);
        notifyItemInserted(filteredCategories.size() - 1);
    }

    public void updateCategory(Category category) {
        int index = -1;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                index = i;
                categories.set(i, category);
                break;
            }
        }
        if (index != -1) {
            int filteredIndex = -1;
            for (int i = 0; i < filteredCategories.size(); i++) {
                if (filteredCategories.get(i).getId() == category.getId()) {
                    filteredIndex = i;
                    filteredCategories.set(i, category);
                    break;
                }
            }
            if (filteredIndex != -1) {
                notifyItemChanged(filteredIndex);
            }
        }
    }

    public void removeCategory(Category category) {
        int index = -1;
        for (int i = 0; i < filteredCategories.size(); i++) {
            if (filteredCategories.get(i).getId() == category.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            categories.remove(category);
            filteredCategories.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void filter(String searchText) {
        if (searchText.isEmpty()) {
            filteredCategories = new ArrayList<>(categories);
        } else {
            String searchLower = searchText.toLowerCase();
            filteredCategories = categories.stream()
                    .filter(category -> category.getName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(filteredCategories.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showPopupMenu(v, filteredCategories.get(position));
                }
                return true;
            });
        }

        private void showPopupMenu(View view, Category category) {
            PopupMenu popup = new PopupMenu(context, view);
            popup.inflate(R.menu.menu_product_options);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onEditClick(category);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteClick(category);
                    return true;
                }
                return false;
            });
            popup.show();
        }

        void bind(Category category) {
            categoryName.setText(category.getName());

            // Load image from file path
            if (category.getIconPath() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(category.getIconPath());
                if (bitmap != null) {
                    categoryImage.setImageBitmap(bitmap);
                } else {
                    categoryImage.setImageResource(R.drawable.placeholder_image);
                }
            } else {
                categoryImage.setImageResource(R.drawable.placeholder_image);
            }
        }
    }
}