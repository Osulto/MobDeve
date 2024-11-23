package com.mobdeve.s19.stocksmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobdeve.s19.stocksmart.database.models.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private List<Product> filteredProducts;
    private final Context context;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.products = new ArrayList<>();
        this.filteredProducts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(filteredProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }

    public void setProducts(List<Product> products) {
        this.products = new ArrayList<>(products);
        this.filteredProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    public void removeProduct(Product product) {
        products.remove(product);
        filteredProducts.remove(product);
        notifyDataSetChanged();
    }

    public void updateProduct(Product product) {
        int index = -1;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            products.set(index, product);
            filter(""); // Refresh filtered list
        }
    }

    public void filter(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredProducts = new ArrayList<>(products);
        } else {
            String searchLower = searchText.toLowerCase();
            filteredProducts = products.stream()
                    .filter(product ->
                            product.getName().toLowerCase().contains(searchLower) ||
                                    (product.getDescription() != null &&
                                            product.getDescription().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void filterInStock() {
        filteredProducts = products.stream()
                .filter(product -> product.getQuantity() > 0)
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public void filterLowStock() {
        filteredProducts = products.stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public void sortByPrice() {
        Collections.sort(filteredProducts,
                Comparator.comparingDouble(Product::getSellingPrice));
        notifyDataSetChanged();
    }

    public void sortByDate() {
        Collections.sort(filteredProducts, (p1, p2) -> {
            String date1 = p1.getCreatedAt();
            String date2 = p2.getCreatedAt();
            if (date1 == null || date2 == null) return 0;
            return date2.compareTo(date1);
        });
        notifyDataSetChanged();
    }

    public void resetFilter() {
        filteredProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productQuantity;
        TextView productPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productPrice = itemView.findViewById(R.id.productPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(filteredProducts.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showPopupMenu(v, filteredProducts.get(position));
                }
                return true;
            });
        }

        private void showPopupMenu(View view, Product product) {
            PopupMenu popup = new PopupMenu(context, view);
            popup.inflate(R.menu.menu_product_options);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onEditClick(product);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteClick(product);
                    return true;
                }
                return false;
            });
            popup.show();
        }

        void bind(Product product) {
            productName.setText(product.getName());
            productQuantity.setText("Stock: " + product.getQuantity());
            productPrice.setText(String.format("₱%.2f", product.getSellingPrice()));

            if (product.isLowStock()) {
                productQuantity.setTextColor(context.getColor(R.color.error));
            } else {
                productQuantity.setTextColor(context.getColor(R.color.text_primary));
            }
        }
    }
}